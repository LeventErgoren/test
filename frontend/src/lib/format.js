export function formatMoney(value) {
    if (value === null || value === undefined || value === '') return '-'

    const num = typeof value === 'number' ? value : Number(String(value).replace(',', '.'))
    if (Number.isNaN(num)) return String(value)

    return new Intl.NumberFormat('tr-TR', {
        style: 'currency',
        currency: 'TRY',
        maximumFractionDigits: 2,
    }).format(num)
}

export function formatDate(value) {
    if (!value) return '-'
    const str = String(value)
    if (str.length >= 10) return str.slice(0, 10)
    return str
}

export function safeText(value) {
    if (value === null || value === undefined) return '-'
    const s = String(value)
    return s.trim().length ? s : '-'
}
