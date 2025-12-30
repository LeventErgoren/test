import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Input, Textarea } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Table } from '../../components/ui/table.jsx'
import { Badge } from '../../components/ui/badge.jsx'
import { safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

function statusBadge(status) {
    if (status === 'OPEN' || status === 'IN_PROGRESS') return <Badge variant="open">Açık</Badge>
    if (status === 'CLOSED') return <Badge variant="closed">Kapalı</Badge>
    return <Badge>{safeText(status)}</Badge>
}

export function ResidentTicketsPage() {
    const { resident } = useAuth()
    const residentId = resident?.id

    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    const [title, setTitle] = useState('')
    const [description, setDescription] = useState('')
    const [result, setResult] = useState(null)

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/tickets')
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

    const myTickets = useMemo(() => {
        if (!residentId) return []
        return items.filter((t) => t?.resident?.id === residentId)
    }, [items, residentId])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Taleplerim</h1>
                <p className="mt-1 text-sm text-slate-600">Yeni talep oluşturun ve durumunu takip edin.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Talep" subtitle="Aynı anda en fazla 3 açık talep oluşturabilirsiniz.">
                <form
                    className="grid gap-4"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setResult(null)
                        try {
                            await api.post(`/api/tickets/resident/${residentId}`, {
                                title: title.trim(),
                                description: description.trim(),
                            })
                            setResult({ ok: true, message: 'Talep oluşturuldu.' })
                            setTitle('')
                            setDescription('')
                            await load()
                        } catch (err) {
                            setResult({ ok: false, message: getErrorMessage(err) })
                        }
                    }}
                >
                    <Input label="Başlık" value={title} onChange={(e) => setTitle(e.target.value)} required />
                    <Textarea label="Açıklama" rows={4} value={description} onChange={(e) => setDescription(e.target.value)} />
                    <div>
                        <Button type="submit" disabled={!residentId}>
                            Talep Oluştur
                        </Button>
                    </div>
                </form>

                {result ? (
                    <div className="mt-4">
                        <Alert variant={result.ok ? 'success' : 'error'} title={result.ok ? 'Başarılı' : 'Hata'}>
                            {result.message}
                        </Alert>
                    </div>
                ) : null}
            </Card>

            <Card title="Talep Listem" right={<Button variant="secondary" onClick={load}>Yenile</Button>}>
                <Table
                    columns={[
                        { key: 'title', header: 'Başlık', render: (r) => safeText(r.title) },
                        { key: 'desc', header: 'Açıklama', render: (r) => safeText(r.description) },
                        { key: 'status', header: 'Durum', render: (r) => statusBadge(r.status) },
                    ]}
                    rows={myTickets}
                    emptyText={loading ? 'Yükleniyor…' : 'Talep kaydı yok.'}
                />
            </Card>
        </div>
    )
}
