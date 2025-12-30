import { useEffect, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Table } from '../../components/ui/table.jsx'
import { safeText } from '../../lib/format.js'
import { useAuth } from '../../state/auth.jsx'

export function ResidentVehiclesPage() {
    const { resident } = useAuth()
    const residentId = resident?.id

    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [result, setResult] = useState(null)

    const [plateNumber, setPlateNumber] = useState('')
    const [brand, setBrand] = useState('')
    const [model, setModel] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/vehicles')
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

    const myVehicles = useMemo(() => {
        if (!residentId) return []
        return items.filter((v) => v?.resident?.id === residentId)
    }, [items, residentId])

    return (
        <div className="space-y-6">
            <div>
                <h1 className="text-2xl font-semibold text-slate-900">Araçlarım</h1>
                <p className="mt-1 text-sm text-slate-600">Kayıtlı araçlarınızı görüntüleyin ve yeni araç ekleyin.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Araç Ekle" subtitle="Kişi başı en fazla 2 araç tanımlanabilir.">
                <form
                    className="grid gap-4 md:grid-cols-3"
                    onSubmit={async (e) => {
                        e.preventDefault()
                        setResult(null)
                        try {
                            await api.post(`/api/vehicles/resident/${residentId}`, {
                                plateNumber: plateNumber.trim(),
                                brand: brand.trim(),
                                model: model.trim(),
                            })
                            setResult({ ok: true, message: 'Araç eklendi.' })
                            setPlateNumber('')
                            setBrand('')
                            setModel('')
                            await load()
                        } catch (err) {
                            setResult({ ok: false, message: getErrorMessage(err) })
                        }
                    }}
                >
                    <Input label="Plaka" value={plateNumber} onChange={(e) => setPlateNumber(e.target.value)} required />
                    <Input label="Marka" value={brand} onChange={(e) => setBrand(e.target.value)} />
                    <Input label="Model" value={model} onChange={(e) => setModel(e.target.value)} />
                    <div className="md:col-span-3">
                        <Button type="submit" disabled={!residentId}>
                            Ekle
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

            <Card title="Araç Listem">
                <Table
                    columns={[
                        { key: 'plate', header: 'Plaka', render: (r) => safeText(r.plateNumber) },
                        { key: 'brand', header: 'Marka', render: (r) => safeText(r.brand) },
                        { key: 'model', header: 'Model', render: (r) => safeText(r.model) },
                    ]}
                    rows={myVehicles}
                    emptyText={loading ? 'Yükleniyor…' : 'Araç kaydı yok.'}
                />
            </Card>
        </div>
    )
}
