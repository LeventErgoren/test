import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { formatMoney, safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

function duesLabel(d) {
    if (!d) return '-'
    const period = `${safeText(d.month)}/${safeText(d.year)}`
    return `${period} • ${formatMoney(d.amount)}`
}

export function ResidentPaymentPage() {
    const { resident } = useAuth()
    const residentId = resident?.id
    const flatId = resident?.flat?.id

    const [dues, setDues] = useState([])
    const [payments, setPayments] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const [duesId, setDuesId] = useState('')
    const [amount, setAmount] = useState('')
    const [paymentDate, setPaymentDate] = useState(() => {
        const d = new Date()
        const y = d.getFullYear()
        const m = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        return `${y}-${m}-${day}`
    })

    const [result, setResult] = useState(null)

    async function load() {
        try {
            setLoading(true)
            setError('')
            const [duesRes, paymentsRes] = await Promise.all([api.get('/api/dues'), api.get('/api/payments')])
            setDues(Array.isArray(duesRes.data) ? duesRes.data : [])
            setPayments(Array.isArray(paymentsRes.data) ? paymentsRes.data : [])
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
    }, [])

    const myDues = useMemo(() => {
        if (!flatId) return []
        return dues.filter((d) => d?.flat?.id === flatId)
    }, [dues, flatId])

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

    const payableDues = useMemo(() => {
        return myDues.filter((d) => {
            const total = Number(String(d?.amount ?? '').replace(',', '.'))
            if (Number.isNaN(total)) return true
            const paid = paidByDuesId.get(String(d?.id)) || 0
            return total - paid > 0
        })
    }, [myDues, paidByDuesId])

    useEffect(() => {
        const current = payableDues.find((d) => String(d?.id) === String(duesId))
        if (current) return
        if (payableDues[0]?.id) setDuesId(String(payableDues[0].id))
        else setDuesId('')
    }, [duesId, payableDues])

    const selectedDues = useMemo(() => myDues.find((d) => String(d?.id) === String(duesId)), [myDues, duesId])

    const paidBySelected = useMemo(() => {
        if (!duesId) return 0
        let total = 0
        for (const p of payments) {
            if (String(p?.dues?.id) !== String(duesId)) continue
            const amt = Number(String(p?.amount ?? '').replace(',', '.'))
            if (!Number.isNaN(amt)) total += amt
        }
        return total
    }, [payments, duesId])

    const remainingBySelected = useMemo(() => {
        const total = Number(String(selectedDues?.amount ?? '').replace(',', '.'))
        if (Number.isNaN(total)) return null
        return Math.max(0, total - paidBySelected)
    }, [selectedDues, paidBySelected])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Ödeme Yap</h1>
                <p className="mt-1 text-sm text-slate-600">Aidat için ödeme girişi yapın. Fazla ödeme engellenir.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            {!flatId ? (
                <Alert variant="info" title="Daire bilgisi yok">
                    Ödeme yapmak için bir daireye bağlı olmanız gerekir.
                </Alert>
            ) : null}

            <Card title="Ödeme Formu">
                <form
                    className="grid gap-4 md:grid-cols-3"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setResult(null)
                        try {
                            const res = await api.post(`/api/payments/dues/${duesId}`, {
                                amount: amount,
                                paymentDate: paymentDate || null,
                                resident: residentId ? { id: Number(residentId) } : undefined,
                            })
                            setResult({ ok: true, message: 'Ödeme kaydedildi.', data: res.data })
                            setAmount('')
                            await load()
                        } catch (err) {
                            setResult({ ok: false, message: getErrorMessage(err) })
                        }
                    }}
                >
                    <Select label="Aidat" value={duesId} onChange={(e) => setDuesId(e.target.value)}>
                        {payableDues.map((d) => (
                            <option key={d.id} value={String(d.id)}>
                                {duesLabel(d)}
                            </option>
                        ))}
                    </Select>
                    <Input label="Tarih" type="date" value={paymentDate} onChange={(e) => setPaymentDate(e.target.value)} />
                    <Input label="Tutar" type="number" min="0" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
                    <div className="md:col-span-3">
                        <Button type="submit" disabled={!duesId || !flatId || loading}>
                            Ödemeyi Gönder
                        </Button>
                    </div>
                </form>

                {selectedDues ? (
                    <div className="mt-4 grid gap-2 rounded-xl bg-slate-50 px-4 py-3 text-sm text-slate-700 md:grid-cols-3">
                        <div>
                            <span className="font-medium">Toplam:</span> {formatMoney(selectedDues.amount)}
                        </div>
                        <div>
                            <span className="font-medium">Ödenen:</span> {formatMoney(paidBySelected)}
                        </div>
                        <div>
                            <span className="font-medium">Kalan:</span> {remainingBySelected === null ? '-' : formatMoney(remainingBySelected)}
                        </div>
                    </div>
                ) : null}

                {result ? (
                    <div className="mt-4">
                        <Alert variant={result.ok ? 'success' : 'error'} title={result.ok ? 'Başarılı' : 'Hata'}>
                            {result.message}
                        </Alert>
                    </div>
                ) : null}

                {!myDues.length && !loading ? (
                    <div className="mt-4 text-sm text-slate-500">Ödenebilir aidat kaydı yok.</div>
                ) : null}

                {!payableDues.length && myDues.length && !loading ? (
                    <div className="mt-2 text-sm text-slate-500">Bu daireye ait tüm aidatlar ödenmiş görünüyor.</div>
                ) : null}
            </Card>
        </div>
    )
}
