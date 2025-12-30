import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Alert } from '../components/ui/alert.jsx'
import { Button } from '../components/ui/button.jsx'
import { Card } from '../components/ui/card.jsx'
import { Input } from '../components/ui/input.jsx'
import { Select } from '../components/ui/select.jsx'
import { api, getErrorMessage } from '../lib/api.js'
import { safeText } from '../lib/format.js'
import { useAuth } from '../state/auth.jsx'

function flatLabel(flat) {
    if (!flat) return '-'
    const blockName = flat?.block?.name ? `${flat.block.name} / ` : ''
    return `${blockName}Kapı ${safeText(flat.doorNumber)}`
}

export function ResidentSignupPage() {
    const navigate = useNavigate()
    const { loginResident } = useAuth()

    const [flats, setFlats] = useState([])
    const [loadingFlats, setLoadingFlats] = useState(true)
    const [flatsError, setFlatsError] = useState('')

    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [email, setEmail] = useState('')
    const [owner, setOwner] = useState('false')
    const [flatId, setFlatId] = useState('')

    const [saving, setSaving] = useState(false)
    const [result, setResult] = useState(null)

    useEffect(() => {
        let mounted = true

        async function loadFlats() {
            try {
                setLoadingFlats(true)
                setFlatsError('')
                const res = await api.get('/api/flats')
                const list = Array.isArray(res.data) ? res.data : []
                if (!mounted) return
                setFlats(list)

                const eligible = list.filter((f) => {
                    const isEmpty = f?.empty ?? f?.isEmpty
                    return isEmpty === false
                })

                if (!flatId && eligible[0]?.id) setFlatId(String(eligible[0].id))
            } catch (err) {
                if (!mounted) return
                setFlatsError(getErrorMessage(err))
            } finally {
                if (mounted) setLoadingFlats(false)
            }
        }

        loadFlats()
        return () => {
            mounted = false
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const eligibleFlats = useMemo(() => {
        return flats.filter((f) => {
            const isEmpty = f?.empty ?? f?.isEmpty
            return isEmpty === false
        })
    }, [flats])

    return (
        <div className="min-h-dvh bg-slate-50">
            <div className="mx-auto max-w-xl px-4 py-14">
                <div className="mb-6">
                    <div className="text-sm font-medium text-slate-500">Sakin Kaydı</div>
                    <h1 className="mt-2 text-2xl font-semibold text-slate-900">Hesap oluştur</h1>
                    <p className="mt-2 text-sm text-slate-600">
                        Dairenizi seçip bilgilerinizi girin. Daireniz listede yoksa yönetimle iletişime geçin.
                    </p>
                </div>

                {flatsError ? (
                    <Alert variant="error" title="Daire listesi alınamadı">{flatsError}</Alert>
                ) : null}

                {result ? (
                    <div className="mt-4">
                        <Alert variant={result.ok ? 'success' : 'error'} title={result.ok ? 'Başarılı' : 'Hata'}>
                            {result.message}
                        </Alert>
                    </div>
                ) : null}

                <div className="mt-6 space-y-4">
                    <Card title="Bilgiler">
                        <form
                            className="grid gap-4 md:grid-cols-2"
                            onSubmit={async (e) => {
                                e.preventDefault()
                                setResult(null)

                                if (!flatId) {
                                    setResult({ ok: false, message: 'Lütfen daire seçin.' })
                                    return
                                }

                                setSaving(true)
                                try {
                                    await api.post('/api/residents', {
                                        firstName: firstName.trim(),
                                        lastName: lastName.trim(),
                                        phoneNumber: phoneNumber.trim(),
                                        email: email.trim(),
                                        owner: owner === 'true',
                                        flat: { id: Number(flatId) },
                                    })

                                    const loginRes = await loginResident({ email, phoneNumber })
                                    if (!loginRes.ok) {
                                        setResult({ ok: true, message: 'Kayıt tamamlandı. Şimdi giriş yapabilirsiniz.' })
                                        return
                                    }

                                    navigate('/sakin', { replace: true })
                                } catch (err) {
                                    setResult({ ok: false, message: getErrorMessage(err) })
                                } finally {
                                    setSaving(false)
                                }
                            }}
                        >
                            <Input label="Ad" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                            <Input label="Soyad" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                            <Input label="E-posta" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                            <Input label="Telefon" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} required />

                            <Select label="Tür" value={owner} onChange={(e) => setOwner(e.target.value)}>
                                <option value="false">Kiracı</option>
                                <option value="true">Ev sahibi</option>
                            </Select>

                            <Select
                                label="Daire"
                                value={flatId}
                                onChange={(e) => setFlatId(e.target.value)}
                                hint={loadingFlats ? 'Daireler yükleniyor…' : eligibleFlats.length ? '' : 'Uygun daire bulunamadı.'}
                            >
                                {eligibleFlats.map((f) => (
                                    <option key={f.id} value={String(f.id)}>
                                        {flatLabel(f)}
                                    </option>
                                ))}
                            </Select>

                            <div className="md:col-span-2 flex items-center justify-between gap-3">
                                <Button type="submit" disabled={saving || loadingFlats || !eligibleFlats.length}>
                                    {saving ? 'Kaydediliyor...' : 'Kayıt Ol'}
                                </Button>
                                <Link className="text-sm font-medium text-slate-700 hover:text-slate-900" to="/sakin/giris">
                                    Zaten hesabım var
                                </Link>
                            </div>
                        </form>
                    </Card>

                    <div className="flex justify-between">
                        <Button variant="secondary" onClick={() => navigate('/')}>Geri</Button>
                        <div className="text-xs text-slate-500">
                            Kayıt için dairelerin sistemde tanımlı olması gerekir.
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
