import { NavLink, useNavigate } from 'react-router-dom'
import { Button } from '../ui/button.jsx'
import { useAuth } from '../../state/auth.jsx'

export function Shell({ title, menu, children }) {
    const navigate = useNavigate()
    const { logout, role, resident, admin } = useAuth()

    const userLabel =
        role === 'admin'
            ? `Yönetici: ${admin?.username || ''}`
            : `Sakin: ${resident?.firstName || ''} ${resident?.lastName || ''}`

    return (
        <div className="min-h-dvh bg-slate-50">
            <div className="mx-auto flex min-h-dvh max-w-7xl">
                <aside className="hidden w-72 flex-col gap-4 border-r border-slate-100 bg-white px-4 py-5 md:flex">
                    <div>
                        <div className="text-lg font-semibold text-slate-900">{title}</div>
                        <div className="mt-1 text-xs text-slate-500">{userLabel}</div>
                    </div>

                    <nav className="flex flex-col gap-1">
                        {menu.map((item) => (
                            <NavLink
                                key={item.to}
                                to={item.to}
                                className={({ isActive }) =>
                                    `rounded-lg px-3 py-2 text-sm font-medium transition ${isActive ? 'bg-slate-900 text-white' : 'text-slate-700 hover:bg-slate-50'
                                    }`
                                }
                                end={item.end}
                            >
                                {item.label}
                            </NavLink>
                        ))}
                    </nav>

                    <div className="mt-auto">
                        <Button
                            variant="secondary"
                            className="w-full"
                            onClick={() => {
                                logout()
                                navigate('/')
                            }}
                        >
                            Çıkış Yap
                        </Button>
                    </div>
                </aside>

                <main className="flex-1 px-4 py-6 md:px-8">
                    <div className="mb-6 flex items-center justify-between gap-3 md:hidden">
                        <div>
                            <div className="text-base font-semibold text-slate-900">{title}</div>
                            <div className="mt-1 text-xs text-slate-500">{userLabel}</div>
                        </div>
                        <Button
                            variant="secondary"
                            onClick={() => {
                                logout()
                                navigate('/')
                            }}
                        >
                            Çıkış
                        </Button>
                    </div>

                    {children}
                </main>
            </div>
        </div>
    )
}
