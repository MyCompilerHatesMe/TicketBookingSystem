import { useState } from 'react'
import Panel from './Panel'
import Field from './Field'
import ResponseBox from './ResponseBox'
import DataTable from './DataTable'
import { apiPost } from '../api'
import styles from './FormPanel.module.css'

const INIT = { sourceCity: '', destinationCity: '', distanceKm: '' }

export default function RoutePanel() {
  const [form, setForm] = useState(INIT)
  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)

  // Stops management state
  const [selectedRoute, setSelectedRoute] = useState(null)
  const [stops, setStops] = useState([])
  const [stopsResponse, setStopsResponse] = useState(null)
  const [stopsLoading, setStopsLoading] = useState(false)
  const [refreshTableKey, setRefreshTableKey] = useState(0) // increment to refresh table

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
      if (res.status === 201) {
        setForm(INIT)
        setRefreshTableKey(k => k + 1)
      }
    } catch (err) {
      setResponse({ status: 0, statusText: 'Network Error', data: { error: err.message } })
    } finally {
      setLoading(false)
    }
  }

  const handleManageStops = (route) => {
    setSelectedRoute(route)
    setStops(route.stops || [])
    setStopsResponse(null)
  }

  const handleAddStopRow = () => {
    setStops([
      ...stops,
      { stopName: '', stopType: 'BOARDING', minutesOffset: 0, sequence: stops.length + 1 }
    ])
  }

  const handleRemoveStopRow = (index) => {
    setStops(stops.filter((_, i) => i !== index))
  }

  const handleStopChange = (index, field, value) => {
    const updated = [...stops]
    if (field === 'minutesOffset' || field === 'sequence') {
      updated[index][field] = value === '' ? '' : parseInt(value, 10)
    } else {
      updated[index][field] = value
    }
    setStops(updated)
  }

  const saveStops = async (e) => {
    e.preventDefault()
    setStopsLoading(true)
    setStopsResponse(null)
    try {
      const res = await apiPost(`/admin/routes/${selectedRoute.routeId}/stops`, stops)
      setStopsResponse(res)
      if (res.status === 201) {
        // Refresh the table to get latest stops
        setRefreshTableKey(k => k + 1)
      }
    } catch (err) {
      setStopsResponse({ status: 0, statusText: 'Network Error', data: { error: err.message } })
    } finally {
      setStopsLoading(false)
    }
  }

  const ROUTE_COLUMNS = [
    { key: 'routeId',         label: 'ID' },
    { key: 'sourceCity',      label: 'From' },
    { key: 'destinationCity', label: 'To' },
    { key: 'distanceKm',      label: 'Distance (km)' },
    {
      key: 'stopsCount',
      label: 'Stops',
      render: (val, row) => row.stops ? `${row.stops.length} stop(s)` : '0 stop(s)'
    },
    {
      key: 'actions',
      label: 'Actions',
      render: (val, row) => (
        <button
          onClick={() => handleManageStops(row)}
          style={{
            background: 'none',
            border: '1px solid var(--navy)',
            color: 'var(--navy)',
            padding: '5px 12px',
            borderRadius: '4px',
            cursor: 'pointer',
            fontWeight: '600',
            fontSize: '12px',
            transition: 'all 0.15s'
          }}
          onMouseOver={e => {
            e.currentTarget.style.background = 'var(--navy)'
            e.currentTarget.style.color = 'var(--white)'
          }}
          onMouseOut={e => {
            e.currentTarget.style.background = 'none'
            e.currentTarget.style.color = 'var(--navy)'
          }}
        >
          Manage Stops
        </button>
      )
    }
  ]

  return (
    <>
      <div className={styles.pageHeader}>
        <h1 className={styles.pageTitle}>Route Management</h1>
        <p className={styles.pageDesc}>Define point-to-point travel paths between cities and configure their boarding/dropping stops.</p>
      </div>

      {!selectedRoute ? (
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
      ) : (
        <Panel
          title={`Configure Stops: ${selectedRoute.sourceCity} ➔ ${selectedRoute.destinationCity}`}
          endpoint={`/admin/routes/${selectedRoute.routeId}/stops`}
          description="Define sequence of pickup/dropoff points along this route with timing offsets in minutes from start."
        >
          <form onSubmit={saveStops}>
            <div style={{ overflowX: 'auto', marginBottom: 20 }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '14px' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid var(--gray-200)', textAlign: 'left' }}>
                    <th style={{ padding: '8px 12px', color: 'var(--gray-600)' }}>Stop Name</th>
                    <th style={{ padding: '8px 12px', color: 'var(--gray-600)' }}>Type</th>
                    <th style={{ padding: '8px 12px', color: 'var(--gray-600)' }}>Minutes Offset</th>
                    <th style={{ padding: '8px 12px', color: 'var(--gray-600)' }}>Sequence</th>
                    <th style={{ padding: '8px 12px', width: '50px' }}></th>
                  </tr>
                </thead>
                <tbody>
                  {stops.map((stop, index) => (
                    <tr key={index} style={{ borderBottom: '1px solid var(--gray-100)' }}>
                      <td style={{ padding: '8px 12px' }}>
                        <input
                          required
                          style={{ width: '100%', padding: '6px', border: '1px solid var(--gray-300)', borderRadius: '4px', outline: 'none' }}
                          value={stop.stopName}
                          onChange={e => handleStopChange(index, 'stopName', e.target.value)}
                          placeholder="e.g. Gachibowli"
                        />
                      </td>
                      <td style={{ padding: '8px 12px' }}>
                        <select
                          style={{ width: '100%', padding: '6px', border: '1px solid var(--gray-300)', borderRadius: '4px', outline: 'none' }}
                          value={stop.stopType}
                          onChange={e => handleStopChange(index, 'stopType', e.target.value)}
                        >
                          <option value="BOARDING">Boarding Only</option>
                          <option value="DROPPING">Dropping Only</option>
                          <option value="BOTH">Both</option>
                        </select>
                      </td>
                      <td style={{ padding: '8px 12px' }}>
                        <input
                          type="number"
                          required
                          min="0"
                          style={{ width: '100%', padding: '6px', border: '1px solid var(--gray-300)', borderRadius: '4px', outline: 'none' }}
                          value={stop.minutesOffset}
                          onChange={e => handleStopChange(index, 'minutesOffset', e.target.value)}
                          placeholder="0"
                        />
                      </td>
                      <td style={{ padding: '8px 12px' }}>
                        <input
                          type="number"
                          required
                          min="1"
                          style={{ width: '100%', padding: '6px', border: '1px solid var(--gray-300)', borderRadius: '4px', outline: 'none' }}
                          value={stop.sequence}
                          onChange={e => handleStopChange(index, 'sequence', e.target.value)}
                          placeholder="1"
                        />
                      </td>
                      <td style={{ padding: '8px 12px', textAlign: 'center' }}>
                        <button
                          type="button"
                          onClick={() => handleRemoveStopRow(index)}
                          style={{
                            background: '#fee2e2',
                            color: '#ef4444',
                            border: 'none',
                            borderRadius: '4px',
                            padding: '6px 10px',
                            cursor: 'pointer',
                            fontWeight: 'bold'
                          }}
                        >
                          ✕
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <button
              type="button"
              onClick={handleAddStopRow}
              style={{
                background: 'white',
                border: '1px dashed var(--gray-400)',
                color: 'var(--gray-600)',
                padding: '6px 12px',
                borderRadius: '4px',
                cursor: 'pointer',
                marginBottom: 20
              }}
            >
              + Add Stop Point
            </button>

            <div className={styles.actions}>
              <button type="submit" className={styles.btn} disabled={stopsLoading}>
                {stopsLoading ? 'Saving…' : 'Save Stops'}
              </button>
              <button
                type="button"
                className={styles.btnSecondary}
                onClick={() => setSelectedRoute(null)}
              >
                Back to Create Route
              </button>
            </div>
            <ResponseBox response={stopsResponse} />
          </form>
        </Panel>
      )}

      <DataTable
        key={refreshTableKey}
        endpoint="/admin/routes"
        columns={ROUTE_COLUMNS}
        title="All Registered Routes"
      />
    </>
  )
}
