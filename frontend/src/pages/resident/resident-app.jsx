import { Navigate, Route, Routes } from 'react-router-dom'
import { Shell } from '../../components/layout/shell.jsx'
import { ResidentDashboardPage } from './dashboard.jsx'
import { ResidentDuesPage } from './dues.jsx'
import { ResidentPaymentPage } from './payment.jsx'
import { ResidentVehiclesPage } from './vehicles.jsx'
import { ResidentTicketsPage } from './tickets.jsx'
import { ResidentProfilePage } from './profile.jsx'

const menu = [
    { to: '/sakin', label: 'Anasayfa', end: true },
    { to: '/sakin/aidatlarim', label: 'Aidatlarım' },
    { to: '/sakin/odeme', label: 'Ödeme Yap' },
    { to: '/sakin/araclarim', label: 'Araçlarım' },
    { to: '/sakin/taleplerim', label: 'Taleplerim' },
    { to: '/sakin/profil', label: 'Profil' },
]

export function ResidentApp() {
    return (
        <Shell title="Sakin Paneli" menu={menu}>
            <Routes>
                <Route index element={<ResidentDashboardPage />} />
                <Route path="/aidatlarim" element={<ResidentDuesPage />} />
                <Route path="/odeme" element={<ResidentPaymentPage />} />
                <Route path="/araclarim" element={<ResidentVehiclesPage />} />
                <Route path="/taleplerim" element={<ResidentTicketsPage />} />
                <Route path="/profil" element={<ResidentProfilePage />} />
                <Route path="*" element={<Navigate to="/sakin" replace />} />
            </Routes>
        </Shell>
    )
}
