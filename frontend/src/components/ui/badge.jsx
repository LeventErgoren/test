export function Badge({ variant = 'neutral', children }) {
    const styles = {
        neutral: 'bg-slate-100 text-slate-700',
        open: 'bg-amber-100 text-amber-900',
        closed: 'bg-emerald-100 text-emerald-900',
    }

    return (
        <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-medium ${styles[variant] || styles.neutral}`}>
            {children}
        </span>
    )
}
