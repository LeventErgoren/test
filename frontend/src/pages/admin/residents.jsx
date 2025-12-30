import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { safeText } from '../../lib/format.js'

function flatLabel(flat) {
    if (!flat) return '-'
    const blockName = flat?.block?.name ? `${flat.block.name} / ` : ''
    return `${blockName}Kapı ${safeText(flat.doorNumber)}`
}

export function AdminResidentsPage() {
    const [residents, setResidents] = useState([])
    const [flats, setFlats] = useState([])

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [firstName, setFirstName] = useState('')
    const [lastName, setLastName] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [email, setEmail] = useState('')
    const [owner, setOwner] = useState('false')
    const [flatId, setFlatId] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const [resRes, flatsRes] = await Promise.all([api.get('/api/residents'), api.get('/api/flats')])
            setResidents(Array.isArray(resRes.data) ? resRes.data : [])
            const flatsList = Array.isArray(flatsRes.data) ? flatsRes.data : []
            setFlats(flatsList)
            if (!flatId && flatsList[0]?.id) setFlatId(String(flatsList[0].id))
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Sakinler</h1>
                <p className="mt-1 text-sm text-slate-600">Sakin ekleyin. Aynı e-posta olamaz; ev sahibi kuralı kontrol edilir.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Sakin">
                {actionError ? (
                    <div className="mb-4">
                        <Alert variant="error" title="İşlem başarısız">{actionError}</Alert>
                    </div>
                ) : null}

                <form
                    className="grid gap-4 md:grid-cols-3"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setActionError('')
                        setSaving(true)
                        try {
                            await api.post('/api/residents', {
                                firstName: firstName.trim(),
                                lastName: lastName.trim(),
                                phoneNumber: phoneNumber.trim(),
                                email: email.trim(),
                                owner: owner === 'true',
                                flat: flatId ? { id: Number(flatId) } : null,
                            })

                            setFirstName('')
                            setLastName('')
                            setPhoneNumber('')
                            setEmail('')
                            setOwner('false')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input label="Ad" value={firstName} onChange={(e) => setFirstName(e.target.value)} required />
                    <Input label="Soyad" value={lastName} onChange={(e) => setLastName(e.target.value)} required />
                    <Input label="Telefon" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} required />
                    <Input label="E-posta" type="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                    <Select label="Tür" value={owner} onChange={(e) => setOwner(e.target.value)}>
                        <option value="false">Kiracı</option>
                        <option value="true">Ev sahibi</option>
                    </Select>
                    <Select label="Daire" value={flatId} onChange={(e) => setFlatId(e.target.value)}>
                        {flats.map((f) => (
                            <option key={f.id} value={String(f.id)}>
                                {flatLabel(f)}
                            </option>
                        ))}
                    </Select>
                    <div className="md:col-span-3">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Sakin Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'name', header: 'Ad Soyad', render: (r) => `${safeText(r.firstName)} ${safeText(r.lastName)}` },
                        { key: 'email', header: 'E-posta', render: (r) => safeText(r.email) },
                        { key: 'phone', header: 'Telefon', render: (r) => safeText(r.phoneNumber) },
                        {
                            key: 'owner',
                            header: 'Tür',
                            render: (r) => {
                                const v = r?.owner ?? r?.isOwner
                                return v ? 'Ev sahibi' : 'Kiracı'
                            },
                        },
                        {
                            key: 'flat',
                            header: 'Daire',
                            render: (r) => flatLabel(r.flat),
                        },
                    ]}
                    rows={residents}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
