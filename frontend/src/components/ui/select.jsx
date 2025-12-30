export function Select({ label, hint, error, className = '', children, ...props }) {
    return (
        <label className={`block ${className}`}>
            {label ? <div className="mb-1 text-sm font-medium text-slate-700">{label}</div> : null}
            <select
                {...props}
                className={`w-full rounded-lg bg-white px-3 py-2 text-sm text-slate-900 ring-1 ring-slate-200 focus:ring-2 focus:ring-slate-400 ${error ? 'ring-rose-300 focus:ring-rose-400' : ''
                    }`}
            >
                {children}
            </select>
            {hint ? <div className="mt-1 text-xs text-slate-500">{hint}</div> : null}
            {error ? <div className="mt-1 text-xs text-rose-700">{error}</div> : null}
        </label>
    )
}
