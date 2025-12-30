import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Badge } from '../../components/ui/badge.jsx'
import { formatMoney, safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

function Metric({ label, value }) {
    return (
        <div className="rounded-2xl bg-white px-5 py-4 ring-1 ring-slate-200">
            <div className="text-xs font-medium text-slate-500">{label}</div>
            <div className="mt-1 text-2xl font-semibold text-slate-900">{value}</div>
        </div>
    )
}

export function ResidentDashboardPage() {
    const { resident } = useAuth()
    const residentId = resident?.id
    const flatId = resident?.flat?.id

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [dues, setDues] = useState([])
    const [vehicles, setVehicles] = useState([])
    const [tickets, setTickets] = useState([])

    useEffect(() => {
        let mounted = true

        async function load() {
            try {
                setLoading(true)
                setError('')

                const [duesRes, vehiclesRes, ticketsRes] = await Promise.all([
                    api.get('/api/dues'),
                    api.get('/api/vehicles'),
                    api.get('/api/tickets'),
                ])

                if (!mounted) return
                setDues(Array.isArray(duesRes.data) ? duesRes.data : [])
                setVehicles(Array.isArray(vehiclesRes.data) ? vehiclesRes.data : [])
                setTickets(Array.isArray(ticketsRes.data) ? ticketsRes.data : [])
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

    const myDues = useMemo(() => {
        if (!flatId) return []
        return dues.filter((d) => d?.flat?.id === flatId)
    }, [dues, flatId])

    const myVehicles = useMemo(() => {
        if (!residentId) return []
        return vehicles.filter((v) => v?.resident?.id === residentId)
    }, [vehicles, residentId])

    const myTickets = useMemo(() => {
        if (!residentId) return []
        return tickets.filter((t) => t?.resident?.id === residentId)
    }, [tickets, residentId])

    const openTickets = myTickets.filter((t) => t?.status !== 'CLOSED').length

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Anasayfa</h1>
                <p className="mt-1 text-sm text-slate-600">Size ait özet bilgiler.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            {!resident?.flat ? (
                <Alert variant="info" title="Daire bilgisi yok">
                    Bu kullanıcı bir daireye bağlı görünmüyor. Yönetim panelinden daire seçilmesi gerekebilir.
                </Alert>
            ) : null}

            <div className="grid gap-4 md:grid-cols-3">
                <Metric label="Aidat kaydı" value={loading ? '…' : myDues.length} />
                <Metric label="Araç" value={loading ? '…' : myVehicles.length} />
                <Metric label="Açık talep" value={loading ? '…' : openTickets} />
            </div>

            <Card title="Daire Bilgisi" subtitle={resident?.flat?.block?.name ? `Blok: ${resident.flat.block.name}` : undefined}>
                <div className="flex flex-wrap items-center gap-3 text-sm text-slate-700">
                    <div>
                        <span className="font-medium">Kapı No:</span> {safeText(resident?.flat?.doorNumber)}
                    </div>
                    <div>
                        <span className="font-medium">Kat:</span> {safeText(resident?.flat?.floor)}
                    </div>
                    <div>
                        <span className="font-medium">Tip:</span> {safeText(resident?.flat?.flatType?.typeName)}
                    </div>
                    <div>
                        <span className="font-medium">Durum:</span>{' '}
                        {resident?.flat?.empty ?? resident?.flat?.isEmpty ? (
                            <Badge>Aidat işlemleri için boş görünüyor</Badge>
                        ) : (
                            <Badge variant="closed">Dolu</Badge>
                        )}
                    </div>
                </div>
            </Card>

            <Card title="Son Aidatlar">
                <div className="space-y-2">
                    {myDues.slice(0, 5).map((d) => (
                        <div key={d.id} className="flex items-center justify-between rounded-xl bg-slate-50 px-4 py-3">
                            <div className="text-sm text-slate-800">
                                <span className="font-medium">{safeText(d.month)}/{safeText(d.year)}</span>
                            </div>
                            <div className="text-sm font-semibold text-slate-900">{formatMoney(d.amount)}</div>
                        </div>
                    ))}
                    {!myDues.length ? <div className="text-sm text-slate-500">Aidat kaydı yok.</div> : null}
                </div>
            </Card>
        </div>
    )
}
