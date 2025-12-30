import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { formatMoney, safeText } from '../../lib/format.js'

export function AdminFlatTypesPage() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const [typeName, setTypeName] = useState('')
    const [defaultDuesAmount, setDefaultDuesAmount] = useState('')
    const [saving, setSaving] = useState(false)
    const [actionError, setActionError] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/flat-types')
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
                <h1 className="text-2xl font-semibold text-slate-900">Daire Tipleri</h1>
                <p className="mt-1 text-sm text-slate-600">Daire tipi tanımlayın.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Daire Tipi">
                {actionError ? (
                    <div className="mb-4">
                        <Alert variant="error" title="İşlem başarısız">{actionError}</Alert>
                    </div>
                ) : null}

                <form
                    className="grid gap-4 md:grid-cols-3"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setActionError('')
                        setSaving(true)
                        try {
                            await api.post('/api/flat-types', {
                                typeName: typeName.trim(),
                                defaultDuesAmount: defaultDuesAmount === '' ? null : defaultDuesAmount,
                            })
                            setTypeName('')
                            setDefaultDuesAmount('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input label="Tip adı" value={typeName} onChange={(e) => setTypeName(e.target.value)} required />
                    <Input
                        label="Varsayılan aidat"
                        type="number"
                        min="0"
                        step="0.01"
                        value={defaultDuesAmount}
                        onChange={(e) => setDefaultDuesAmount(e.target.value)}
                    />
                    <div className="flex items-end">
                        <Button type="submit" disabled={saving} className="w-full">
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Daire Tipi Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'typeName', header: 'Tip', render: (r) => safeText(r.typeName) },
                        { key: 'defaultDuesAmount', header: 'Varsayılan aidat', render: (r) => formatMoney(r.defaultDuesAmount) },
                    ]}
                    rows={items}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
