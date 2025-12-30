export function Button({
    type = 'button',
    variant = 'primary',
    disabled,
    className = '',
    children,
    ...props
}) {
    const base =
        'inline-flex items-center justify-center rounded-lg px-4 py-2 text-sm font-medium transition focus:outline-none focus:ring-2 focus:ring-slate-400 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'

    const variants = {
        primary: 'bg-slate-900 text-white hover:bg-slate-800',
        secondary: 'bg-white text-slate-900 ring-1 ring-slate-200 hover:bg-slate-50',
        danger: 'bg-rose-600 text-white hover:bg-rose-500',
    }

    return (
        <button
            type={type}
            disabled={disabled}
            className={`${base} ${variants[variant] || variants.primary} ${className}`}
            {...props}
        >
            {children}
        </button>
    )
}
