import { useState } from 'react'
import Panel from './Panel'
import Field from './Field'
import ResponseBox from './ResponseBox'
import DataTable from './DataTable'
import { apiPost } from '../api'
import styles from './FormPanel.module.css'

const fmt = iso => iso ? new Date(iso).toLocaleString('en-IN', {
  day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
}) : '-'

const TRIP_COLUMNS = [
  { key: 'tripId',      label: 'ID' },
  { key: 'busName',     label: 'Bus' },
  { key: 'busNumber',   label: 'Bus No.' },
  { key: 'sourceCity',  label: 'From' },
  { key: 'destinationCity', label: 'To' },
  { key: 'startTime',   label: 'Departure',  render: v => fmt(v) },
  { key: 'arrivalTime', label: 'Arrival',    render: v => fmt(v) },
  { key: 'fare',        label: 'Fare (₹)',   render: v => `₹${Number(v).toLocaleString('en-IN')}` },
]

const INIT = { busId: '', routeId: '', startTime: '', arrivalTime: '', fare: '' }

export default function TripPanel() {
  const [form, setForm] = useState(INIT)
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)

  const handle = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    setResponse(null)
    try {
      const res = await apiPost('/admin/trips', {
        busId: parseInt(form.busId, 10),
        routeId: parseInt(form.routeId, 10),
        startTime: form.startTime ? form.startTime + ':00' : '',
        arrivalTime: form.arrivalTime ? form.arrivalTime + ':00' : '',
        fare: parseFloat(form.fare),
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
        <h1 className={styles.pageTitle}>Trip Scheduling</h1>
        <p className={styles.pageDesc}>Schedule operational journeys by linking buses to routes with timetable and fare details.</p>
      </div>

      <Panel
        title="Schedule New Trip"
        endpoint="/admin/trips"
        description="Links a registered bus to a route and schedules a journey. This clones the bus seat layout into a fresh availability grid for this trip."
      >
        <form onSubmit={submit}>
          <div className={styles.grid2}>
            <Field label="Bus ID" name="busId" type="number" value={form.busId} onChange={handle}
              placeholder="12" min="1" required />
            <Field label="Route ID" name="routeId" type="number" value={form.routeId} onChange={handle}
              placeholder="5" min="1" required />
          </div>
          <div className={styles.grid2} style={{ marginTop: 16 }}>
            <Field label="Departure Time" name="startTime" type="datetime-local" value={form.startTime} onChange={handle} required />
            <Field label="Arrival Time" name="arrivalTime" type="datetime-local" value={form.arrivalTime} onChange={handle} required />
          </div>
          <div className={styles.grid2} style={{ marginTop: 16 }}>
            <Field label="Fare (₹)" name="fare" type="number" value={form.fare} onChange={handle}
              placeholder="1250.00" min="0" required />
          </div>
          <div className={styles.actions}>
            <button type="submit" className={styles.btn} disabled={loading}>
              {loading ? 'Scheduling…' : 'Schedule Trip'}
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
        endpoint="/admin/trips"
        columns={TRIP_COLUMNS}
        title="All Scheduled Trips"
      />
    </>
  )
}
