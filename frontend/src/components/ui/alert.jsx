export function Alert({ variant = 'info', title, children }) {
    const styles = {
        info: 'bg-slate-50 text-slate-800 ring-slate-200',
        success: 'bg-emerald-50 text-emerald-900 ring-emerald-200',
        error: 'bg-rose-50 text-rose-900 ring-rose-200',
    }

    return (
        <div className={`rounded-xl px-4 py-3 ring-1 ${styles[variant] || styles.info}`}>
            {title ? <div className="text-sm font-semibold">{title}</div> : null}
            {children ? <div className="mt-1 text-sm">{children}</div> : null}
        </div>
    )
}
