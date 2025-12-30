import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

export function ResidentProfilePage() {
    const { resident } = useAuth()

    if (!resident) {
        return (
            <Alert variant="error" title="Oturum bulunamadı">
                Lütfen tekrar giriş yapın.
            </Alert>
        )
    }

    const owner = resident?.owner ?? resident?.isOwner

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Profil</h1>
                <p className="mt-1 text-sm text-slate-600">Kişisel bilgileriniz.</p>
            </div>

            <Card title="Sakin Bilgisi">
                <div className="grid gap-3 text-sm text-slate-800 md:grid-cols-2">
                    <div>
                        <div className="text-xs font-medium text-slate-500">Ad</div>
                        <div className="mt-1 font-medium">{safeText(resident.firstName)}</div>
                    </div>
                    <div>
                        <div className="text-xs font-medium text-slate-500">Soyad</div>
                        <div className="mt-1 font-medium">{safeText(resident.lastName)}</div>
                    </div>
                    <div>
                        <div className="text-xs font-medium text-slate-500">E-posta</div>
                        <div className="mt-1 font-medium">{safeText(resident.email)}</div>
                    </div>
                    <div>
                        <div className="text-xs font-medium text-slate-500">Telefon</div>
                        <div className="mt-1 font-medium">{safeText(resident.phoneNumber)}</div>
                    </div>
                    <div>
                        <div className="text-xs font-medium text-slate-500">Tür</div>
                        <div className="mt-1 font-medium">{owner ? 'Ev sahibi' : 'Kiracı'}</div>
                    </div>
                </div>
            </Card>

            <Card title="Daire">
                {resident.flat ? (
                    <div className="grid gap-3 text-sm text-slate-800 md:grid-cols-2">
                        <div>
                            <div className="text-xs font-medium text-slate-500">Blok</div>
                            <div className="mt-1 font-medium">{safeText(resident.flat?.block?.name)}</div>
                        </div>
                        <div>
                            <div className="text-xs font-medium text-slate-500">Kapı No</div>
                            <div className="mt-1 font-medium">{safeText(resident.flat?.doorNumber)}</div>
                        </div>
                        <div>
                            <div className="text-xs font-medium text-slate-500">Kat</div>
                            <div className="mt-1 font-medium">{safeText(resident.flat?.floor)}</div>
                        </div>
                        <div>
                            <div className="text-xs font-medium text-slate-500">Tip</div>
                            <div className="mt-1 font-medium">{safeText(resident.flat?.flatType?.typeName)}</div>
                        </div>
                    </div>
                ) : (
                    <Alert variant="info" title="Daire bilgisi yok">Yönetim panelinden daire eşleştirmesi yapılmalıdır.</Alert>
                )}
            </Card>
        </div>
    )
}
