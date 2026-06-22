import { useState } from 'react'
import { apiGet } from '../api'
import Field from './Field'
import styles from './FormPanel.module.css'
import tripStyles from './TripSearch.module.css'

const INIT = { sourceCity: '', destinationCity: '', date: '' }

export default function TripSearch({ onSelectTrip }) {
  const [form, setForm] = useState(INIT)
  const [trips, setTrips] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handle = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const search = async e => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setTrips(null)
    const res = await apiGet('/trips', form)
    setLoading(false)
    if (res.status === 200) setTrips(res.data)
    else setError(res.data?.message || 'Failed to fetch trips.')
  }

  const fmt = iso => {
    const d = new Date(iso)
    return d.toLocaleString('en-IN', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' })
  }

  const duration = (start, end) => {
    const diff = (new Date(end) - new Date(start)) / 60000
    const h = Math.floor(diff / 60), m = diff % 60
    return `${h}h ${m > 0 ? m + 'm' : ''}`.trim()
  }

  return (
    <div>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Find Buses</h1>
        <p className={styles.pageDesc}>Search for available trips. All filters are optional.</p>
      </div>

      <div className={tripStyles.searchCard}>
        <form onSubmit={search}>
          <div className={styles.grid2} style={{ gridTemplateColumns: '1fr 1fr 1fr auto' }}>
            <Field label="From" name="sourceCity" value={form.sourceCity} onChange={handle} placeholder="Hyderabad" />
            <Field label="To" name="destinationCity" value={form.destinationCity} onChange={handle} placeholder="Bangalore" />
            <Field label="Date" name="date" type="date" value={form.date} onChange={handle} />
            <div className={tripStyles.searchBtnWrap}>
              <button type="submit" className={tripStyles.searchBtn} disabled={loading}>
                {loading ? '…' : 'Search'}
              </button>
            </div>
          </div>
        </form>
      </div>

      {error && <div className={tripStyles.errorMsg}>{error}</div>}

      {trips !== null && trips.length === 0 && (
        <div className={tripStyles.empty}>No trips found matching your criteria.</div>
      )}

      {trips && trips.length > 0 && (
        <div className={tripStyles.results}>
          <div className={tripStyles.resultsLabel}>{trips.length} trip{trips.length !== 1 ? 's' : ''} found</div>
          {trips.map(t => (
            <div key={t.tripId} className={tripStyles.tripCard}>
              <div className={tripStyles.route}>
                <div className={tripStyles.city}>{t.sourceCity}</div>
                <div className={tripStyles.routeLine}>
                  <span className={tripStyles.dot} />
                  <span className={tripStyles.line} />
                  <span className={tripStyles.dur}>{duration(t.startTime, t.arrivalTime)}</span>
                  <span className={tripStyles.line} />
                  <span className={tripStyles.dot} />
                </div>
                <div className={tripStyles.city}>{t.destinationCity}</div>
              </div>
              <div className={tripStyles.times}>
                <span>{fmt(t.startTime)}</span>
                <span className={tripStyles.timeSep}>→</span>
                <span>{fmt(t.arrivalTime)}</span>
              </div>
              <div className={tripStyles.tripMeta}>
                <span className={tripStyles.busTag}>{t.busName} · {t.busType}</span>
                <span className={tripStyles.busTag}>{t.busNumber}</span>
              </div>
              <div className={tripStyles.fareRow}>
                <span className={tripStyles.fare}>₹{t.fare.toLocaleString('en-IN')}</span>
                <button className={tripStyles.selectBtn} onClick={() => onSelectTrip(t)}>
                  Select Seats
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
