import { Navigate, Route, Routes } from 'react-router-dom'
import { Shell } from '../../components/layout/shell.jsx'
import { AdminDashboardPage } from './dashboard.jsx'
import { AdminBlocksPage } from './blocks.jsx'
import { AdminFlatTypesPage } from './flat-types.jsx'
import { AdminFlatsPage } from './flats.jsx'
import { AdminResidentsPage } from './residents.jsx'
import { AdminDuesPage } from './dues.jsx'
import { AdminPaymentsPage } from './payments.jsx'
import { AdminExpensesPage } from './expenses.jsx'
import { AdminStaffPage } from './staff.jsx'
import { AdminVehiclesPage } from './vehicles.jsx'
import { AdminTicketsPage } from './tickets.jsx'

const menu = [
    { to: '/admin', label: 'Genel Bakış', end: true },
    { to: '/admin/bloklar', label: 'Bloklar' },
    { to: '/admin/daire-tipleri', label: 'Daire Tipleri' },
    { to: '/admin/daireler', label: 'Daireler' },
    { to: '/admin/sakinler', label: 'Sakinler' },
    { to: '/admin/aidatlar', label: 'Aidatlar' },
    { to: '/admin/odemeler', label: 'Ödeme Al' },
    { to: '/admin/giderler', label: 'Giderler' },
    { to: '/admin/personel', label: 'Personel' },
    { to: '/admin/araclar', label: 'Araçlar' },
    { to: '/admin/talepler', label: 'Talepler' },
]

export function AdminApp() {
    return (
        <Shell title="Yönetim Paneli" menu={menu}>
            <Routes>
                <Route index element={<AdminDashboardPage />} />
                <Route path="/bloklar" element={<AdminBlocksPage />} />
                <Route path="/daire-tipleri" element={<AdminFlatTypesPage />} />
                <Route path="/daireler" element={<AdminFlatsPage />} />
                <Route path="/sakinler" element={<AdminResidentsPage />} />
                <Route path="/aidatlar" element={<AdminDuesPage />} />
                <Route path="/odemeler" element={<AdminPaymentsPage />} />
                <Route path="/giderler" element={<AdminExpensesPage />} />
                <Route path="/personel" element={<AdminStaffPage />} />
                <Route path="/araclar" element={<AdminVehiclesPage />} />
                <Route path="/talepler" element={<AdminTicketsPage />} />
                <Route path="*" element={<Navigate to="/admin" replace />} />
            </Routes>
        </Shell>
    )
}
