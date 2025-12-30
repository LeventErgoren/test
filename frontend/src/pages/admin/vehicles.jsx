import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Select } from '../../components/ui/select.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { safeText } from '../../lib/format.js'

export function AdminVehiclesPage() {
    const [items, setItems] = useState([])
    const [residents, setResidents] = useState([])

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [residentId, setResidentId] = useState('')
    const [plateNumber, setPlateNumber] = useState('')
    const [brand, setBrand] = useState('')
    const [model, setModel] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const [vehRes, resRes] = await Promise.all([api.get('/api/vehicles'), api.get('/api/residents')])
            setItems(Array.isArray(vehRes.data) ? vehRes.data : [])
            const residentsList = Array.isArray(resRes.data) ? resRes.data : []
            setResidents(residentsList)
            if (!residentId && residentsList[0]?.id) setResidentId(String(residentsList[0].id))
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

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Araçlar</h1>
                <p className="mt-1 text-sm text-slate-600">Sakine araç tanımlayın. Kişi başı max 2 araç kuralı uygulanır.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Veriler yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Araç Ekle">
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
                            await api.post(`/api/vehicles/resident/${residentId}`, {
                                plateNumber: plateNumber.trim(),
                                brand: brand.trim(),
                                model: model.trim(),
                            })
                            setPlateNumber('')
                            setBrand('')
                            setModel('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Select label="Sakin" value={residentId} onChange={(e) => setResidentId(e.target.value)}>
                        {residents.map((r) => (
                            <option key={r.id} value={String(r.id)}>
                                {safeText(r.firstName)} {safeText(r.lastName)}
                            </option>
                        ))}
                    </Select>
                    <Input label="Plaka" value={plateNumber} onChange={(e) => setPlateNumber(e.target.value)} required />
                    <Input label="Marka" value={brand} onChange={(e) => setBrand(e.target.value)} />
                    <Input label="Model" value={model} onChange={(e) => setModel(e.target.value)} />
                    <div className="md:col-span-4">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Araç Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'plate', header: 'Plaka', render: (r) => safeText(r.plateNumber) },
                        { key: 'brand', header: 'Marka', render: (r) => safeText(r.brand) },
                        { key: 'model', header: 'Model', render: (r) => safeText(r.model) },
                        {
                            key: 'resident',
                            header: 'Sakin',
                            render: (r) => `${safeText(r?.resident?.firstName)} ${safeText(r?.resident?.lastName)}`,
                        },
                    ]}
                    rows={items}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
