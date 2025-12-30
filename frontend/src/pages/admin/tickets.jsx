import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { Badge } from '../../components/ui/badge.jsx'
import { safeText } from '../../lib/format.js'

function statusBadge(status) {
    if (status === 'OPEN' || status === 'IN_PROGRESS') return <Badge variant="open">Açık</Badge>
    if (status === 'CLOSED') return <Badge variant="closed">Kapalı</Badge>
    return <Badge>{safeText(status)}</Badge>
}

export function AdminTicketsPage() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')

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

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Talepler</h1>
                <p className="mt-1 text-sm text-slate-600">Talepleri görüntüleyin ve kapatın.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            {actionError ? (
                <Alert variant="error" title="İşlem başarısız">
                    {actionError}
                </Alert>
            ) : null}

            <Card
                title="Talep Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'title', header: 'Başlık', render: (r) => safeText(r.title) },
                        { key: 'desc', header: 'Açıklama', render: (r) => safeText(r.description) },
                        { key: 'status', header: 'Durum', render: (r) => statusBadge(r.status) },
                        {
                            key: 'resident',
                            header: 'Sakin',
                            render: (r) => `${safeText(r?.resident?.firstName)} ${safeText(r?.resident?.lastName)}`,
                        },
                        {
                            key: 'actions',
                            header: 'İşlem',
                            render: (r) => (
                                <Button
                                    variant="secondary"
                                    disabled={r?.status === 'CLOSED'}
                                    onClick={async () => {
                                        setActionError('')
                                        try {
                                            await api.put(`/api/tickets/close/${r.id}`)
                                            await load()
                                        } catch (err) {
                                            setActionError(getErrorMessage(err))
                                        }
                                    }}
                                >
                                    Kapat
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
