import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { safeText } from '../../lib/format.js'

export function AdminBlocksPage() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const [name, setName] = useState('')
    const [totalFloors, setTotalFloors] = useState('')
    const [saving, setSaving] = useState(false)
    const [actionError, setActionError] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/blocks')
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
                <h1 className="text-2xl font-semibold text-slate-900">Bloklar</h1>
                <p className="mt-1 text-sm text-slate-600">Blok ekleyin ve gerektiğinde silin.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Blok">
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
                            await api.post('/api/blocks', {
                                name: name.trim(),
                                totalFloors: totalFloors === '' ? null : Number(totalFloors),
                            })
                            setName('')
                            setTotalFloors('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input label="Blok adı" value={name} onChange={(e) => setName(e.target.value)} required />
                    <Input
                        label="Toplam kat"
                        type="number"
                        min="1"
                        value={totalFloors}
                        onChange={(e) => setTotalFloors(e.target.value)}
                        required
                    />
                    <div className="flex items-end">
                        <Button type="submit" disabled={saving} className="w-full">
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Blok Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'name', header: 'Ad', render: (r) => safeText(r.name) },
                        { key: 'totalFloors', header: 'Kat', render: (r) => safeText(r.totalFloors) },
                        {
                            key: 'actions',
                            header: 'İşlem',
                            render: (r) => (
                                <Button
                                    variant="danger"
                                    onClick={async () => {
                                        if (!confirm('Bu bloğu silmek istiyor musunuz?')) return
                                        setActionError('')
                                        try {
                                            await api.delete(`/api/blocks/${r.id}`)
                                            await load()
                                        } catch (err) {
                                            setActionError(getErrorMessage(err))
                                        }
                                    }}
                                >
                                    Sil
                                </Button>
                            ),
                        },
                    ]}
                    rows={items}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
