import axios from 'axios'

const configuredBaseUrl = (import.meta.env.VITE_API_BASE_URL || '').trim()

export const api = axios.create({
    baseURL: configuredBaseUrl.length ? configuredBaseUrl : '',
    headers: {
        'Content-Type': 'application/json',
    },
})

export function getErrorMessage(err) {
    if (!err) return 'Beklenmeyen bir hata oluştu.'

    if (axios.isAxiosError?.(err)) {
        const data = err.response?.data
        if (data && typeof data === 'object' && typeof data.error === 'string' && data.error.trim()) {
            return data.error
        }

        if (typeof data === 'string' && data.trim()) {
            return data
        }

        if (err.response?.status) {
            const status = err.response.status
            if (status >= 500) return 'Sunucu tarafında bir hata oluştu.'
            return `İstek başarısız: ${status}`
        }

        return 'Sunucuya bağlanılamadı. Backend çalışıyor mu?'
    }

    if (err instanceof Error && err.message) return err.message
    return 'Beklenmeyen bir hata oluştu.'
}
