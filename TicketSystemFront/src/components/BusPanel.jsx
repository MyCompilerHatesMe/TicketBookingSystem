import { useState } from 'react'
import Panel from './Panel'
import Field from './Field'
import ResponseBox from './ResponseBox'
import DataTable from './DataTable'
import { apiPost } from '../api'
import styles from './FormPanel.module.css'

const BUS_COLUMNS = [
  { key: 'busId',      label: 'ID' },
  { key: 'busNumber',  label: 'Bus Number' },
  { key: 'busName',    label: 'Name' },
  { key: 'busType',    label: 'Type' },
  { key: 'totalSeats', label: 'Seats' },
]

const INIT = { busNumber: '', busName: '', busType: '', totalSeats: '' }

export default function BusPanel() {
  const [form, setForm] = useState(INIT)
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)

  const handle = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async e => {
    e.preventDefault()
    setLoading(true)
    setResponse(null)
    try {
      const res = await apiPost('/admin/buses', {
        ...form,
        totalSeats: parseInt(form.totalSeats, 10),
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
        <h1 className={styles.pageTitle}>Bus Management</h1>
        <p className={styles.pageDesc}>Register physical bus vehicles and generate their seat inventory.</p>
      </div>

      <Panel
        title="Register New Bus"
        endpoint="/admin/buses"
        description="Registers a new bus vehicle and automatically generates its full seat layout grid. Maximum seat capacity is 52."
      >
        <form onSubmit={submit}>
          <div className={styles.grid2}>
            <Field label="Bus Number" name="busNumber" value={form.busNumber} onChange={handle}
              placeholder="TS-09-UB-1234" required />
            <Field label="Bus Name" name="busName" value={form.busName} onChange={handle}
              placeholder="Garuda Plus" required />
          </div>
          <div className={styles.grid2} style={{ marginTop: 16 }}>
            <Field label="Bus Type" name="busType" value={form.busType} onChange={handle}
              placeholder="AC Multi-Axle Sleeper" required />
            <Field label="Total Seats" name="totalSeats" type="number" value={form.totalSeats} onChange={handle}
              placeholder="40" min="1" max="52" required />
          </div>
          <div className={styles.actions}>
            <button type="submit" className={styles.btn} disabled={loading}>
              {loading ? 'Registering…' : 'Register Bus'}
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
        endpoint="/admin/buses"
        columns={BUS_COLUMNS}
        title="All Registered Buses"
      />
    </>
  )
}
