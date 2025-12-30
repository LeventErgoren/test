export function Table({ columns, rows, emptyText = 'Kayıt bulunamadı.' }) {
    return (
        <div className="overflow-x-auto rounded-xl ring-1 ring-slate-200">
            <table className="min-w-full divide-y divide-slate-200 bg-white">
                <thead className="bg-slate-50">
                    <tr>
                        {columns.map((c) => (
                            <th
                                key={c.key}
                                className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-600"
                            >
                                {c.header}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                    {rows.length ? (
                        rows.map((row, idx) => (
                            <tr key={row?.id ?? idx} className="hover:bg-slate-50">
                                {columns.map((c) => (
                                    <td key={c.key} className="px-4 py-3 text-sm text-slate-800">
                                        {c.render(row)}
                                    </td>
                                ))}
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td className="px-4 py-10 text-center text-sm text-slate-500" colSpan={columns.length}>
                                {emptyText}
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}
