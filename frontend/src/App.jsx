import { Navigate, Route, Routes } from 'react-router-dom'
import { LandingPage } from './pages/landing.jsx'
import { AdminLoginPage } from './pages/login-admin.jsx'
import { ResidentLoginPage } from './pages/login-resident.jsx'
import { ResidentSignupPage } from './pages/signup-resident.jsx'
import { AdminApp } from './pages/admin/admin-app.jsx'
import { ResidentApp } from './pages/resident/resident-app.jsx'
import { RequireRole } from './state/require-role.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/admin/giris" element={<AdminLoginPage />} />
      <Route path="/sakin/giris" element={<ResidentLoginPage />} />
      <Route path="/sakin/kayit" element={<ResidentSignupPage />} />

      <Route
        path="/admin/*"
        element={
          <RequireRole role="admin">
            <AdminApp />
          </RequireRole>
        }
      />
      <Route
        path="/sakin/*"
        element={
          <RequireRole role="resident">
            <ResidentApp />
          </RequireRole>
        }
      />

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
