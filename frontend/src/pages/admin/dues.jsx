import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { formatMoney, safeText } from '../../lib/format.js'

function flatLabel(flat) {
    if (!flat) return '-'
    const blockName = flat?.block?.name ? `${flat.block.name} / ` : ''
    return `${blockName}Kapı ${safeText(flat.doorNumber)}`
}

export function AdminDuesPage() {
    const [dues, setDues] = useState([])
    const [flats, setFlats] = useState([])
    const [payments, setPayments] = useState([])

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [flatId, setFlatId] = useState('')
    const [month, setMonth] = useState('')
    const [year, setYear] = useState(String(new Date().getFullYear()))
    const [amount, setAmount] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const [duesRes, flatsRes, paymentsRes] = await Promise.all([api.get('/api/dues'), api.get('/api/flats'), api.get('/api/payments')])
            setDues(Array.isArray(duesRes.data) ? duesRes.data : [])
            const flatsList = Array.isArray(flatsRes.data) ? flatsRes.data : []
            setFlats(flatsList)
            setPayments(Array.isArray(paymentsRes.data) ? paymentsRes.data : [])
            if (!flatId && flatsList[0]?.id) setFlatId(String(flatsList[0].id))
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const flatMap = useMemo(() => new Map(flats.map((f) => [String(f.id), f])), [flats])

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

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Aidatlar</h1>
                <p className="mt-1 text-sm text-slate-600">Daireye ait aidat kaydı oluşturun. Boş daireye aidat girilemez.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Aidat Tanımla">
                {actionError ? (
                    <div className="mb-4">
                        <Alert variant="error" title="İşlem başarısız">{actionError}</Alert>
                    </div>
                ) : null}

                <form
                    className="grid gap-4 md:grid-cols-4"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setActionError('')
                        setSaving(true)
                        try {
                            await api.post(`/api/dues/flat/${flatId}`, {
                                month: Number(month),
                                year: Number(year),
                                amount: amount,
                            })

                            setMonth('')
                            setAmount('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Select label="Daire" value={flatId} onChange={(e) => setFlatId(e.target.value)}>
                        {flats.map((f) => (
                            <option key={f.id} value={String(f.id)}>
                                {flatLabel(f)}
                            </option>
                        ))}
                    </Select>
                    <Input label="Ay" type="number" min="1" max="12" value={month} onChange={(e) => setMonth(e.target.value)} required />
                    <Input label="Yıl" type="number" min="2000" value={year} onChange={(e) => setYear(e.target.value)} required />
                    <Input label="Tutar" type="number" min="0" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
                    <div className="md:col-span-4">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Oluştur'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Aidat Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'flat', header: 'Daire', render: (r) => flatLabel(r.flat || flatMap.get(String(r?.flat?.id))) },
                        { key: 'period', header: 'Dönem', render: (r) => `${safeText(r.month)}/${safeText(r.year)}` },
                        { key: 'amount', header: 'Tutar', render: (r) => formatMoney(r.amount) },
                        {
                            key: 'paid',
                            header: 'Ödenen',
                            render: (r) => formatMoney(paidByDuesId.get(String(r?.id)) || 0),
                        },
                        {
                            key: 'remaining',
                            header: 'Kalan',
                            render: (r) => {
                                const total = Number(String(r?.amount ?? '').replace(',', '.'))
                                const paid = paidByDuesId.get(String(r?.id)) || 0
                                if (Number.isNaN(total)) return '-'
                                return formatMoney(Math.max(0, total - paid))
                            },
                        },
                    ]}
                    rows={dues}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
