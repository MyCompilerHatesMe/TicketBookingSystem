import { useEffect, useState } from 'react'
import { apiGet } from '../api'
import styles from './DataTable.module.css'

export default function DataTable({ endpoint, columns, title }) {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  const fetch_ = async () => {
    setLoading(true)
    setError(null)
    const res = await apiGet(endpoint, {})
    setLoading(false)
    if (res.status === 200) setData(res.data)
    else setError(`HTTP ${res.status} - ${res.data?.message || res.statusText}`)
  }

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetch_()
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [endpoint])

  return (
    <div className={styles.wrap}>
      <div className={styles.header}>
        <span className={styles.title}>{title}</span>
        <button className={styles.refresh} onClick={fetch_} disabled={loading} title="Refresh">
          {loading ? '…' : '↻ Refresh'}
        </button>
      </div>
      {error && <div className={styles.error}>{error}</div>}
      {!error && loading && <div className={styles.loading}>Loading…</div>}
      {!error && !loading && data && (
        data.length === 0
          ? <div className={styles.empty}>No records found.</div>
          : (
            <div className={styles.tableWrap}>
              <table className={styles.table}>
                <thead>
                  <tr>
                    {columns.map(c => <th key={c.key}>{c.label}</th>)}
                  </tr>
                </thead>
                <tbody>
                  {data.map((row, i) => (
                    <tr key={i}>
                      {columns.map(c => (
                        <td key={c.key}>{c.render ? c.render(row[c.key], row) : (row[c.key] ?? '-')}</td>
                      ))}
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )
      )}
    </div>
  )
}
