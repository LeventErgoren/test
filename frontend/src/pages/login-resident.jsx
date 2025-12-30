import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Button } from '../components/ui/button.jsx'
import { Input } from '../components/ui/input.jsx'
import { Alert } from '../components/ui/alert.jsx'
import { useAuth } from '../state/auth.jsx'

export function ResidentLoginPage() {
    const navigate = useNavigate()
    const { loginResident } = useAuth()

    const [email, setEmail] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    return (
        <div className="min-h-dvh bg-slate-50">
            <div className="mx-auto max-w-md px-4 py-14">
                <div className="rounded-3xl bg-white p-8 ring-1 ring-slate-200">
                    <div className="text-sm font-medium text-slate-500">Sakin Girişi</div>
                    <h1 className="mt-2 text-2xl font-semibold text-slate-900">Hesabınızı bulun</h1>
                    <p className="mt-2 text-sm text-slate-600">
                        E-posta ve telefon numaranız ile giriş yapın.
                    </p>

                    {error ? (
                        <div className="mt-4">
                            <Alert variant="error" title="Giriş başarısız">
                                {error}
                            </Alert>
                        </div>
                    ) : null}

                    <form
                        className="mt-6 space-y-4"
                        onSubmit={async (e) => {
                            e.preventDefault()
                            setError('')
                            setLoading(true)
                            const res = await loginResident({ email, phoneNumber })
                            setLoading(false)

                            if (!res.ok) {
                                setError(res.message || 'Giriş başarısız.')
                                return
                            }

                            navigate('/sakin', { replace: true })
                        }}
                    >
                        <Input
                            label="E-posta"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            type="email"
                            autoComplete="email"
                            placeholder="ornek@mail.com"
                            required
                        />
                        <Input
                            label="Telefon"
                            value={phoneNumber}
                            onChange={(e) => setPhoneNumber(e.target.value)}
                            autoComplete="tel"
                            placeholder="05xx xxx xx xx"
                            required
                        />

                        <div className="flex gap-3">
                            <Button type="submit" disabled={loading} className="flex-1">
                                {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
                            </Button>
                            <Button type="button" variant="secondary" onClick={() => navigate('/')}>
                                Geri
                            </Button>
                        </div>
                    </form>

                    <div className="mt-6 text-center text-sm text-slate-600">
                        Hesabınız yok mu?{' '}
                        <Link className="font-medium text-slate-900 hover:text-slate-700" to="/sakin/kayit">
                            Kayıt ol
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    )
}
