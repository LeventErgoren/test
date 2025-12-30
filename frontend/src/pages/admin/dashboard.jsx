import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'

function Metric({ label, value }) {
    return (
        <div className="rounded-2xl bg-white px-5 py-4 ring-1 ring-slate-200">
            <div className="text-xs font-medium text-slate-500">{label}</div>
            <div className="mt-1 text-2xl font-semibold text-slate-900">{value}</div>
        </div>
    )
}

export function AdminDashboardPage() {
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [counts, setCounts] = useState({
        blocks: 0,
        flatTypes: 0,
        flats: 0,
        residents: 0,
        dues: 0,
        expenses: 0,
        staff: 0,
        vehicles: 0,
        tickets: 0,
    })

    useEffect(() => {
        let mounted = true

        async function load() {
            try {
                setLoading(true)
                setError('')

                const [blocks, flatTypes, flats, residents, dues, expenses, staff, vehicles, tickets] =
                    await Promise.all([
                        api.get('/api/blocks'),
                        api.get('/api/flat-types'),
                        api.get('/api/flats'),
                        api.get('/api/residents'),
                        api.get('/api/dues'),
                        api.get('/api/expenses'),
                        api.get('/api/staff'),
                        api.get('/api/vehicles'),
                        api.get('/api/tickets'),
                    ])

                if (!mounted) return

                setCounts({
                    blocks: Array.isArray(blocks.data) ? blocks.data.length : 0,
                    flatTypes: Array.isArray(flatTypes.data) ? flatTypes.data.length : 0,
                    flats: Array.isArray(flats.data) ? flats.data.length : 0,
                    residents: Array.isArray(residents.data) ? residents.data.length : 0,
                    dues: Array.isArray(dues.data) ? dues.data.length : 0,
                    expenses: Array.isArray(expenses.data) ? expenses.data.length : 0,
                    staff: Array.isArray(staff.data) ? staff.data.length : 0,
                    vehicles: Array.isArray(vehicles.data) ? vehicles.data.length : 0,
                    tickets: Array.isArray(tickets.data) ? tickets.data.length : 0,
                })
            } catch (err) {
                if (mounted) setError(getErrorMessage(err))
            } finally {
                if (mounted) setLoading(false)
            }
        }

        load()
        return () => {
            mounted = false
        }
    }, [])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Genel Bakış</h1>
                <p className="mt-1 text-sm text-slate-600">Sistemdeki genel durum.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <div className="grid gap-4 md:grid-cols-3">
                <Metric label="Blok" value={loading ? '…' : counts.blocks} />
                <Metric label="Daire" value={loading ? '…' : counts.flats} />
                <Metric label="Sakin" value={loading ? '…' : counts.residents} />
                <Metric label="Aidat kaydı" value={loading ? '…' : counts.dues} />
                <Metric label="Gider" value={loading ? '…' : counts.expenses} />
                <Metric label="Talep" value={loading ? '…' : counts.tickets} />
            </div>

            <Card
                title="İpucu"
                subtitle="Bazı işlemler kurallarla kısıtlıdır (ör. blok kapasitesi, tek ev sahibi, fazla ödeme)."
            >
                <div className="text-sm text-slate-700">
                    İşlem sırasında bir kural ihlali olursa, ekranda açıklayıcı hata mesajı gösterilir.
                </div>
            </Card>
        </div>
    )
}
