import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Button } from '../components/ui/button.jsx'
import { Input } from '../components/ui/input.jsx'
import { Alert } from '../components/ui/alert.jsx'
import { useAuth } from '../state/auth.jsx'

export function AdminLoginPage() {
    const navigate = useNavigate()
    const { loginAdmin } = useAuth()

    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState('')

    return (
        <div className="min-h-dvh bg-slate-50">
            <div className="mx-auto max-w-md px-4 py-14">
                <div className="rounded-3xl bg-white p-8 ring-1 ring-slate-200">
                    <div className="text-sm font-medium text-slate-500">Yönetici Girişi</div>
                    <h1 className="mt-2 text-2xl font-semibold text-slate-900">Admin hesabı</h1>
                    <p className="mt-2 text-sm text-slate-600">Kullanıcı adı ve şifrenizle giriş yapın.</p>

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
                            const res = await loginAdmin({ username, password })
                            setLoading(false)

                            if (!res.ok) {
                                setError(res.message || 'Giriş başarısız.')
                                return
                            }

                            navigate('/admin', { replace: true })
                        }}
                    >
                        <Input
                            label="Kullanıcı adı"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            autoComplete="username"
                            placeholder="örn: admin"
                            required
                        />
                        <Input
                            label="Şifre"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            autoComplete="current-password"
                            placeholder="••••••••"
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
                </div>
            </div>
        </div>
    )
}
