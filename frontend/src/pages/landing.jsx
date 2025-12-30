import { Link } from 'react-router-dom'

export function LandingPage() {
    return (
        <div className="min-h-dvh bg-slate-50">
            <div className="mx-auto max-w-5xl px-4 py-16">
                <div className="rounded-3xl bg-white p-8 ring-1 ring-slate-200">
                    <div className="text-sm font-medium text-slate-500">Apartman Yönetimi</div>
                    <h1 className="mt-2 text-3xl font-semibold tracking-tight text-slate-900">
                        Hoş geldiniz
                    </h1>
                    <p className="mt-3 max-w-2xl text-slate-600">
                        Bu ekranda yönetici ve sakin girişlerini yapabilir, tüm işlemleri kolayca yürütebilirsiniz.
                    </p>

                    <div className="mt-8 grid gap-4 md:grid-cols-2">
                        <Link
                            to="/admin/giris"
                            className="rounded-2xl bg-slate-900 p-6 text-white transition hover:bg-slate-800"
                        >
                            <div className="text-sm font-medium text-slate-200">Yönetici</div>
                            <div className="mt-1 text-xl font-semibold">Admin girişi</div>
                            <div className="mt-2 text-sm text-slate-200">
                                Bloklar, daireler, sakinler, aidatlar, giderler ve talepler.
                            </div>
                        </Link>

                        <Link
                            to="/sakin/giris"
                            className="rounded-2xl bg-white p-6 text-slate-900 ring-1 ring-slate-200 transition hover:bg-slate-50"
                        >
                            <div className="text-sm font-medium text-slate-500">Sakin</div>
                            <div className="mt-1 text-xl font-semibold">Sakin girişi</div>
                            <div className="mt-2 text-sm text-slate-600">
                                Aidatlarınızı görüntüleyin, ödeme yapın, araç ekleyin, talep oluşturun.
                            </div>
                        </Link>
                    </div>

                    <div className="mt-4">
                        <Link
                            to="/sakin/kayit"
                            className="inline-flex items-center rounded-lg bg-white px-4 py-2 text-sm font-medium text-slate-900 ring-1 ring-slate-200 hover:bg-slate-50"
                        >
                            Sakin kayıt ol
                        </Link>
                    </div>

                    <div className="mt-8 text-xs text-slate-500">
                        Not: Sakin girişi, e-posta + telefon eşleşmesi ile yapılır.
                    </div>
                </div>
            </div>
        </div>
    )
}
