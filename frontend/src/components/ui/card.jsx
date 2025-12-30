export function Card({ title, subtitle, right, children }) {
    return (
        <div className="rounded-2xl bg-white ring-1 ring-slate-200">
            {(title || subtitle || right) && (
                <div className="flex items-start justify-between gap-4 border-b border-slate-100 px-5 py-4">
                    <div>
                        {title ? <div className="text-base font-semibold text-slate-900">{title}</div> : null}
                        {subtitle ? <div className="mt-1 text-sm text-slate-600">{subtitle}</div> : null}
                    </div>
                    {right ? <div className="shrink-0">{right}</div> : null}
                </div>
            )}
            <div className="px-5 py-4">{children}</div>
        </div>
    )
}
