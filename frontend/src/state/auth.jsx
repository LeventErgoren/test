/* eslint-disable react-refresh/only-export-components */

import { createContext, useCallback, useContext, useMemo, useState } from 'react'
import { api, getErrorMessage } from '../lib/api.js'

const STORAGE_KEY = 'demo.apartman.session'

function loadSession() {
    try {
        const raw = localStorage.getItem(STORAGE_KEY)
        if (!raw) return null
        return JSON.parse(raw)
    } catch {
        return null
    }
}

function saveSession(session) {
    try {
        if (!session) localStorage.removeItem(STORAGE_KEY)
        else localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    } catch {
        // ignore
    }
}

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
    const [session, setSession] = useState(() => loadSession())

    const setAndPersist = useCallback((next) => {
        setSession(next)
        saveSession(next)
    }, [])

    const logout = useCallback(() => {
        setAndPersist(null)
    }, [setAndPersist])

    const loginAdmin = useCallback(async ({ username, password }) => {
        try {
            const res = await api.post('/api/admin/login', { username, password })
            if (res.data !== true) {
                throw new Error('Giriş başarısız. Bilgilerinizi kontrol edin.')
            }

            setAndPersist({
                role: 'admin',
                admin: {
                    username,
                },
            })

            return { ok: true }
        } catch (err) {
            return { ok: false, message: getErrorMessage(err) }
        }
    }, [setAndPersist])

    const loginResident = useCallback(async ({ email, phoneNumber }) => {
        const normalizedEmail = (email || '').trim().toLowerCase()
        const normalizedPhone = (phoneNumber || '').replace(/\s+/g, '')

        try {
            const res = await api.get('/api/residents')
            const residents = Array.isArray(res.data) ? res.data : []

            const match = residents.find((r) => {
                const rEmail = String(r?.email || '').trim().toLowerCase()
                const rPhone = String(r?.phoneNumber || '').replace(/\s+/g, '')
                return rEmail === normalizedEmail && rPhone === normalizedPhone
            })

            if (!match) {
                return {
                    ok: false,
                    message: 'E-posta veya telefon bilgisi eşleşmedi.',
                }
            }

            setAndPersist({
                role: 'resident',
                resident: match,
            })

            return { ok: true }
        } catch (err) {
            return { ok: false, message: getErrorMessage(err) }
        }
    }, [setAndPersist])

    const value = useMemo(() => {
        return {
            session,
            isAuthenticated: Boolean(session?.role),
            role: session?.role || null,
            resident: session?.role === 'resident' ? session.resident : null,
            admin: session?.role === 'admin' ? session.admin : null,
            loginAdmin,
            loginResident,
            logout,
        }
    }, [session, loginAdmin, loginResident, logout])

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const ctx = useContext(AuthContext)
    if (!ctx) throw new Error('useAuth must be used within AuthProvider')
    return ctx
}
