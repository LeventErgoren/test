import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { formatDate, formatMoney, safeText } from '../../lib/format.js'

export function AdminExpensesPage() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [description, setDescription] = useState('')
    const [category, setCategory] = useState('')
    const [amount, setAmount] = useState('')
    const [expenseDate, setExpenseDate] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/expenses')
            setItems(Array.isArray(res.data) ? res.data : [])
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        load()
    }, [])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Giderler</h1>
                <p className="mt-1 text-sm text-slate-600">Gider kaydı oluşturun ve listeleyin.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Gider">
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
                            await api.post('/api/expenses', {
                                description: description.trim(),
                                category: category.trim(),
                                amount: amount,
                                expenseDate: expenseDate || null,
                            })
                            setDescription('')
                            setCategory('')
                            setAmount('')
                            setExpenseDate('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input label="Açıklama" value={description} onChange={(e) => setDescription(e.target.value)} required />
                    <Input label="Kategori" value={category} onChange={(e) => setCategory(e.target.value)} placeholder="örn: Elektrik" />
                    <Input label="Tutar" type="number" min="0" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
                    <Input label="Tarih" type="date" value={expenseDate} onChange={(e) => setExpenseDate(e.target.value)} />
                    <div className="md:col-span-4">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Gider Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'description', header: 'Açıklama', render: (r) => safeText(r.description) },
                        { key: 'category', header: 'Kategori', render: (r) => safeText(r.category) },
                        { key: 'amount', header: 'Tutar', render: (r) => formatMoney(r.amount) },
                        { key: 'date', header: 'Tarih', render: (r) => formatDate(r.expenseDate) },
                    ]}
                    rows={items}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
