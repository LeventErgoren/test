import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { safeText } from '../../lib/format.js'

export function AdminFlatsPage() {
    const [flats, setFlats] = useState([])
    const [blocks, setBlocks] = useState([])
    const [flatTypes, setFlatTypes] = useState([])

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [doorNumber, setDoorNumber] = useState('')
    const [floor, setFloor] = useState('')
    const [empty, setEmpty] = useState('true')
    const [blockId, setBlockId] = useState('')
    const [flatTypeId, setFlatTypeId] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const [flatsRes, blocksRes, typesRes] = await Promise.all([
                api.get('/api/flats'),
                api.get('/api/blocks'),
                api.get('/api/flat-types'),
            ])

            setFlats(Array.isArray(flatsRes.data) ? flatsRes.data : [])
            setBlocks(Array.isArray(blocksRes.data) ? blocksRes.data : [])
            setFlatTypes(Array.isArray(typesRes.data) ? typesRes.data : [])

            const blocksList = Array.isArray(blocksRes.data) ? blocksRes.data : []
            const typesList = Array.isArray(typesRes.data) ? typesRes.data : []
            if (!blockId && blocksList[0]?.id) setBlockId(String(blocksList[0].id))
            if (!flatTypeId && typesList[0]?.id) setFlatTypeId(String(typesList[0].id))
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

    const blockMap = useMemo(() => new Map(blocks.map((b) => [String(b.id), b])), [blocks])
    const typeMap = useMemo(() => new Map(flatTypes.map((t) => [String(t.id), t])), [flatTypes])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Daireler</h1>
                <p className="mt-1 text-sm text-slate-600">Daire ekleyin. Blok kapasitesi doluysa sistem izin vermez.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Daire">
                {actionError ? (
                    <div className="mb-4">
                        <Alert variant="error" title="İşlem başarısız">{actionError}</Alert>
                    </div>
                ) : null}

                <form
                    className="grid gap-4 md:grid-cols-5"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setActionError('')
                        setSaving(true)
                        try {
                            await api.post('/api/flats', {
                                doorNumber: Number(doorNumber),
                                floor: floor === '' ? null : Number(floor),
                                empty: empty === 'true',
                                block: { id: Number(blockId) },
                                flatType: { id: Number(flatTypeId) },
                            })

                            setDoorNumber('')
                            setFloor('')
                            setEmpty('true')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input
                        label="Kapı No"
                        type="number"
                        min="1"
                        value={doorNumber}
                        onChange={(e) => setDoorNumber(e.target.value)}
                        required
                    />
                    <Input
                        label="Kat"
                        type="number"
                        min="0"
                        value={floor}
                        onChange={(e) => setFloor(e.target.value)}
                    />
                    <Select label="Durum" value={empty} onChange={(e) => setEmpty(e.target.value)}>
                        <option value="true">Boş</option>
                        <option value="false">Dolu</option>
                    </Select>
                    <Select label="Blok" value={blockId} onChange={(e) => setBlockId(e.target.value)}>
                        {blocks.map((b) => (
                            <option key={b.id} value={String(b.id)}>
                                {b.name}
                            </option>
                        ))}
                    </Select>
                    <Select label="Daire tipi" value={flatTypeId} onChange={(e) => setFlatTypeId(e.target.value)}>
                        {flatTypes.map((t) => (
                            <option key={t.id} value={String(t.id)}>
                                {t.typeName}
                            </option>
                        ))}
                    </Select>
                    <div className="md:col-span-5">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Daire Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        {
                            key: 'doorNumber',
                            header: 'Kapı No',
                            render: (r) => safeText(r.doorNumber),
                        },
                        {
                            key: 'floor',
                            header: 'Kat',
                            render: (r) => safeText(r.floor),
                        },
                        {
                            key: 'empty',
                            header: 'Durum',
                            render: (r) => {
                                const v = r?.empty ?? r?.isEmpty
                                return v ? 'Boş' : 'Dolu'
                            },
                        },
                        {
                            key: 'block',
                            header: 'Blok',
                            render: (r) => safeText(r?.block?.name || blockMap.get(String(r?.block?.id))?.name),
                        },
                        {
                            key: 'flatType',
                            header: 'Tip',
                            render: (r) => safeText(r?.flatType?.typeName || typeMap.get(String(r?.flatType?.id))?.typeName),
                        },
                    ]}
                    rows={flats}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
