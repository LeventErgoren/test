import { useEffect, useState } from 'react'
import { api, getErrorMessage } from '../../lib/api.js'
import { Card } from '../../components/ui/card.jsx'
import { Input } from '../../components/ui/input.jsx'
import { Button } from '../../components/ui/button.jsx'
import { Alert } from '../../components/ui/alert.jsx'
import { Table } from '../../components/ui/table.jsx'
import { formatMoney, safeText } from '../../lib/format.js'

export function AdminStaffPage() {
    const [items, setItems] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [actionError, setActionError] = useState('')
    const [saving, setSaving] = useState(false)

    const [fullName, setFullName] = useState('')
    const [role, setRole] = useState('')
    const [phoneNumber, setPhoneNumber] = useState('')
    const [salary, setSalary] = useState('')

    async function load() {
        try {
            setLoading(true)
            setError('')
            const res = await api.get('/api/staff')
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
                <h1 className="text-2xl font-semibold text-slate-900">Personel</h1>
                <p className="mt-1 text-sm text-slate-600">Personel kaydı oluşturun ve listeleyin.</p>
            </div>

            {error ? (
                <Alert variant="error" title="Liste yüklenemedi">
                    {error}
                </Alert>
            ) : null}

            <Card title="Yeni Personel">
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
                            await api.post('/api/staff', {
                                fullName: fullName.trim(),
                                role: role.trim(),
                                phoneNumber: phoneNumber.trim(),
                                salary: salary === '' ? null : salary,
                            })
                            setFullName('')
                            setRole('')
                            setPhoneNumber('')
                            setSalary('')
                            await load()
                        } catch (err) {
                            setActionError(getErrorMessage(err))
                        } finally {
                            setSaving(false)
                        }
                    }}
                >
                    <Input label="Ad Soyad" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
                    <Input label="Görev" value={role} onChange={(e) => setRole(e.target.value)} placeholder="örn: Güvenlik" required />
                    <Input label="Telefon" value={phoneNumber} onChange={(e) => setPhoneNumber(e.target.value)} required />
                    <Input label="Maaş" type="number" min="0" step="0.01" value={salary} onChange={(e) => setSalary(e.target.value)} />
                    <div className="md:col-span-4">
                        <Button type="submit" disabled={saving}>
                            {saving ? 'Kaydediliyor...' : 'Ekle'}
                        </Button>
                    </div>
                </form>
            </Card>

            <Card
                title="Personel Listesi"
                right={
                    <Button variant="secondary" onClick={load} disabled={loading}>
                        Yenile
                    </Button>
                }
            >
                <Table
                    columns={[
                        { key: 'id', header: 'ID', render: (r) => safeText(r.id) },
                        { key: 'fullName', header: 'Ad Soyad', render: (r) => safeText(r.fullName) },
                        { key: 'role', header: 'Görev', render: (r) => safeText(r.role) },
                        { key: 'phone', header: 'Telefon', render: (r) => safeText(r.phoneNumber) },
                        { key: 'salary', header: 'Maaş', render: (r) => formatMoney(r.salary) },
                    ]}
                    rows={items}
                    emptyText={loading ? 'Yükleniyor…' : 'Kayıt bulunamadı.'}
                />
            </Card>
        </div>
    )
}
