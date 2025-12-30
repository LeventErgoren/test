import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { formatMoney, safeText } from '../../lib/format.js'

function duesLabel(d) {
    if (!d) return '-'
    const flatDoor = d?.flat?.doorNumber ? `Kapı ${d.flat.doorNumber}` : 'Daire'
    const period = `${safeText(d.month)}/${safeText(d.year)}`
    return `${flatDoor} • ${period} • ${formatMoney(d.amount)}`
}

export function AdminPaymentsPage() {
    const [dues, setDues] = useState([])
    const [residents, setResidents] = useState([])
    const [payments, setPayments] = useState([])

    const [error, setError] = useState('')
    const [result, setResult] = useState(null)

    const [duesId, setDuesId] = useState('')
    const [residentId, setResidentId] = useState('')
    const [amount, setAmount] = useState('')
    const [paymentDate, setPaymentDate] = useState(() => {
        const d = new Date()
        const y = d.getFullYear()
        const m = String(d.getMonth() + 1).padStart(2, '0')
        const day = String(d.getDate()).padStart(2, '0')
        return `${y}-${m}-${day}`
    })

    async function load() {
        try {
            setError('')
            const [duesRes, residentsRes, paymentsRes] = await Promise.all([api.get('/api/dues'), api.get('/api/residents'), api.get('/api/payments')])
            const duesList = Array.isArray(duesRes.data) ? duesRes.data : []
            const residentsList = Array.isArray(residentsRes.data) ? residentsRes.data : []
            const paymentsList = Array.isArray(paymentsRes.data) ? paymentsRes.data : []

            setDues(duesList)
            setResidents(residentsList)
            setPayments(paymentsList)

            if (!duesId && duesList[0]?.id) setDuesId(String(duesList[0].id))
            if (!residentId && residentsList[0]?.id) setResidentId(String(residentsList[0].id))
        } catch (err) {
            setError(getErrorMessage(err))
        }
    }

    useEffect(() => {
        load()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const duesMap = useMemo(() => new Map(dues.map((d) => [String(d.id), d])), [dues])

    const selectedDues = useMemo(() => duesMap.get(String(duesId)), [duesMap, duesId])

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
        return dues.filter((d) => {
            const total = Number(String(d?.amount ?? '').replace(',', '.'))
            if (Number.isNaN(total)) return true
            const paid = paidByDuesId.get(String(d?.id)) || 0
            return total - paid > 0
        })
    }, [dues, paidByDuesId])

    useEffect(() => {
        const current = payableDues.find((d) => String(d?.id) === String(duesId))
        if (current) return
        if (payableDues[0]?.id) setDuesId(String(payableDues[0].id))
        else setDuesId('')
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [payableDues])

    const remainingBySelected = useMemo(() => {
        const total = Number(String(selectedDues?.amount ?? '').replace(',', '.'))
        if (Number.isNaN(total)) return null
        return Math.max(0, total - paidBySelected)
    }, [selectedDues, paidBySelected])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Ödeme Al</h1>
                <p className="mt-1 text-sm text-slate-600">Bir aidat kaydı için ödeme girin. Fazla ödeme engellenir.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Ödeme Oluştur">
                <form
                    className="grid gap-4 md:grid-cols-4"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setResult(null)
                        try {
                            const payload = {
                                amount: amount,
                                paymentDate: paymentDate || null,
                            }

                            if (residentId) {
                                payload.resident = { id: Number(residentId) }
                            }

                            const res = await api.post(`/api/payments/dues/${duesId}`, payload)
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
                    <Select label="Ödeyen (opsiyonel)" value={residentId} onChange={(e) => setResidentId(e.target.value)}>
                        <option value="">Seçilmedi</option>
                        {residents.map((r) => (
                            <option key={r.id} value={String(r.id)}>
                                {safeText(r.firstName)} {safeText(r.lastName)}
                            </option>
                        ))}
                    </Select>
                    <Input label="Tarih" type="date" value={paymentDate} onChange={(e) => setPaymentDate(e.target.value)} />
                    <Input label="Tutar" type="number" min="0" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />

                    <div className="md:col-span-4 flex items-center justify-between gap-3">
                        <Button type="submit">Ödemeyi Kaydet</Button>
                        <div className="text-xs text-slate-500">
                            Seçili aidat: {duesLabel(duesMap.get(String(duesId)))}
                        </div>
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
            </Card>

            <Card title="Not" subtitle="Bu ekranda sadece ödeme girişi yapılır.">
                <div className="text-sm text-slate-700">Ödeme yaptıkça "Kalan" tutar azalır.</div>
            </Card>
        </div>
    )
}
