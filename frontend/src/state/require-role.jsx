import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from './auth.jsx'

export function RequireRole({ role, children }) {
    const { role: currentRole } = useAuth()
    const location = useLocation()

    if (!currentRole) {
        return <Navigate to="/" replace state={{ from: location.pathname }} />
    }

    if (currentRole !== role) {
        return <Navigate to="/" replace />
    }

    return children
}
