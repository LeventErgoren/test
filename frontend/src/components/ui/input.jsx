export function Input({ label, hint, error, className = '', ...props }) {
    return (
        <label className={`block ${className}`}>
            {label ? <div className="mb-1 text-sm font-medium text-slate-700">{label}</div> : null}
            <input
                {...props}
                className={`w-full rounded-lg bg-white px-3 py-2 text-sm text-slate-900 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-slate-400 ${error ? 'ring-rose-300 focus:ring-rose-400' : ''
                    }`}
            />
            {hint ? <div className="mt-1 text-xs text-slate-500">{hint}</div> : null}
            {error ? <div className="mt-1 text-xs text-rose-700">{error}</div> : null}
        </label>
    )
}

export function Textarea({ label, hint, error, className = '', ...props }) {
    return (
        <label className={`block ${className}`}>
            {label ? <div className="mb-1 text-sm font-medium text-slate-700">{label}</div> : null}
            <textarea
                {...props}
                className={`w-full rounded-lg bg-white px-3 py-2 text-sm text-slate-900 ring-1 ring-slate-200 placeholder:text-slate-400 focus:ring-2 focus:ring-slate-400 ${error ? 'ring-rose-300 focus:ring-rose-400' : ''
                    }`}
            />
            {hint ? <div className="mt-1 text-xs text-slate-500">{hint}</div> : null}
            {error ? <div className="mt-1 text-xs text-rose-700">{error}</div> : null}
        </label>
    )
}
