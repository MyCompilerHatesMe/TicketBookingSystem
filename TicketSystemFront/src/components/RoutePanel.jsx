import { useState } from 'react'
import Panel from './Panel'
import Field from './Field'
import ResponseBox from './ResponseBox'
import DataTable from './DataTable'
import { apiPost } from '../api'
import styles from './FormPanel.module.css'

const ROUTE_COLUMNS = [
  { key: 'routeId',         label: 'ID' },
  { key: 'sourceCity',      label: 'From' },
  { key: 'destinationCity', label: 'To' },
  { key: 'distanceKm',      label: 'Distance (km)' },
]

const INIT = { sourceCity: '', destinationCity: '', distanceKm: '' }

export default function RoutePanel() {
  const [form, setForm] = useState(INIT)
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)

  const handle = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    setResponse(null)
    try {
      const res = await apiPost('/admin/routes', {
        ...form,
        distanceKm: parseInt(form.distanceKm, 10),
      })
      setResponse(res)
    } catch (err) {
      setResponse({ status: 0, statusText: 'Network Error', data: { error: err.message } })
    } finally {
      setLoading(false)
    }
  }

  return (
    <>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Route Management</h1>
        <p className={styles.pageDesc}>Define point-to-point travel paths between cities.</p>
      </div>

      <Panel
        title="Create New Route"
        endpoint="/admin/routes"
        description="Establishes a new source-to-destination travel path and logs it in the route registry."
      >
        <form onSubmit={submit}>
          <div className={styles.grid2}>
            <Field label="Source City" name="sourceCity" value={form.sourceCity} onChange={handle}
              placeholder="Hyderabad" required />
            <Field label="Destination City" name="destinationCity" value={form.destinationCity} onChange={handle}
              placeholder="Bangalore" required />
          </div>
          <div className={styles.grid2} style={{ marginTop: 16 }}>
            <Field label="Distance (km)" name="distanceKm" type="number" value={form.distanceKm} onChange={handle}
              placeholder="575" min="1" required />
          </div>
          <div className={styles.actions}>
            <button type="submit" className={styles.btn} disabled={loading}>
              {loading ? 'Creating…' : 'Create Route'}
            </button>
            <button type="button" className={styles.btnSecondary}
              onClick={() => { setForm(INIT); setResponse(null) }}>
              Clear
            </button>
          </div>
          <ResponseBox response={response} />
        </form>
      </Panel>

      <DataTable
        endpoint="/admin/routes"
        columns={ROUTE_COLUMNS}
        title="All Registered Routes"
      />
    </>
  )
}
