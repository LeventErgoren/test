import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { formatMoney, safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

export function ResidentDuesPage() {
    const { resident } = useAuth()
    const flatId = resident?.flat?.id

    const [items, setItems] = useState([])
    const [payments, setPayments] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        async function load() {
            try {
                setLoading(true)
                setError('')
                const [duesRes, paymentsRes] = await Promise.all([api.get('/api/dues'), api.get('/api/payments')])
                setItems(Array.isArray(duesRes.data) ? duesRes.data : [])
                setPayments(Array.isArray(paymentsRes.data) ? paymentsRes.data : [])
            } catch (err) {
                setError(getErrorMessage(err))
            } finally {
                setLoading(false)
            }
        }

        load()
    }, [])

    const myDues = useMemo(() => {
        if (!flatId) return []
        return items.filter((d) => d?.flat?.id === flatId)
    }, [items, flatId])

    const paidByDuesId = useMemo(() => {
        const map = new Map()
        for (const p of payments) {
            const id = p?.dues?.id
            if (!id) continue
            const amt = Number(String(p?.amount ?? '').replace(',', '.'))
            if (Number.isNaN(amt)) continue
            const key = String(id)
            map.set(key, (map.get(key) || 0) + amt)
        }
        return map
    }, [payments])

    const myDuesWithRemaining = useMemo(() => {
        return myDues.map((d) => {
            const total = Number(String(d?.amount ?? '').replace(',', '.'))
            const paid = paidByDuesId.get(String(d?.id)) || 0
            const remaining = Number.isNaN(total) ? null : Math.max(0, total - paid)
            return { dues: d, paid, remaining }
        })
    }, [myDues, paidByDuesId])

    const unpaidRows = useMemo(() => myDuesWithRemaining.filter((x) => (x.remaining ?? 0) > 0), [myDuesWithRemaining])
    const paidRows = useMemo(() => myDuesWithRemaining.filter((x) => x.remaining === 0), [myDuesWithRemaining])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Aidatlarım</h1>
                <p className="mt-1 text-sm text-slate-600">Dairenize yansıtılan aidatlar.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            {!flatId ? (
                <Alert variant="info" title="Daire bilgisi yok">
                    Aidatları görüntülemek için bir daireye bağlı olmanız gerekir.
                </Alert>
            ) : null}

            <Card title="Ödenmemiş Aidatlar" subtitle="Ödeme yapılması gereken aidatlar.">
                <Table
                    columns={[
                        { key: 'period', header: 'Dönem', render: (r) => `${safeText(r?.dues?.month)}/${safeText(r?.dues?.year)}` },
                        { key: 'amount', header: 'Tutar', render: (r) => formatMoney(r?.dues?.amount) },
                        { key: 'paid', header: 'Ödenen', render: (r) => formatMoney(r?.paid ?? 0) },
                        { key: 'remaining', header: 'Kalan', render: (r) => (r.remaining === null ? '-' : formatMoney(r.remaining)) },
                    ]}
                    rows={unpaidRows}
                    emptyText={loading ? 'Yükleniyor…' : 'Ödenmemiş aidat yok.'}
                />
            </Card>

            <Card title="Ödenmiş Aidatlar" subtitle="Tamamı ödenmiş kayıtlar.">
                <Table
                    columns={[
                        { key: 'period', header: 'Dönem', render: (r) => `${safeText(r?.dues?.month)}/${safeText(r?.dues?.year)}` },
                        { key: 'amount', header: 'Tutar', render: (r) => formatMoney(r?.dues?.amount) },
                        { key: 'paid', header: 'Ödenen', render: (r) => formatMoney(r?.paid ?? 0) },
                    ]}
                    rows={paidRows}
                    emptyText={loading ? 'Yükleniyor…' : 'Ödenmiş aidat yok.'}
                />
            </Card>
        </div>
    )
}
