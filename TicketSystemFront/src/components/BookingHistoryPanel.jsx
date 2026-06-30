import { useState, useEffect } from 'react'
import { apiGet } from '../api'
import Panel from './Panel'
import styles from './BookingHistoryPanel.module.css'

export default function BookingHistoryPanel({ user }) {
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [historyList, setHistoryList] = useState([])
  const [step, setStep] = useState(user ? 'list' : 'form')

  const fetchLoggedInHistory = async () => {
    setLoading(true)
    setError('')
    try {
      const res = await apiGet('/bookings/history')
      if (res.status === 200) {
        setHistoryList(res.data || [])
      } else {
        const msg = res.data?.message || res.data?.error || 'Failed to fetch booking history.'
        setError(msg)
      }
    } catch {
      setError('A network error occurred. Please check connection.')
    } finally {
      setLoading(false)
    }
  }

  // ponytail: wrap state updates in timer to prevent synchronous render triggers during effects
  useEffect(() => {
    const timer = setTimeout(() => {
      if (user) {
        fetchLoggedInHistory()
      } else {
        setStep('form')
        setHistoryList([])
        setError('')
      }
    }, 0)
    return () => clearTimeout(timer)
  }, [user])

  const handleGuestSubmit = async e => {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const res = await apiGet('/bookings/history', { email, otp })
      if (res.status === 200) {
        setHistoryList(res.data || [])
        setStep('list')
      } else {
        const msg = res.data?.message || res.data?.error || 'Verification failed.'
        setError(msg)
      }
    } catch {
      setError('A network error occurred. Please check connection.')
    } finally {
      setLoading(false)
    }
  }

  const handleReset = () => {
    setStep('form')
    setHistoryList([])
    setEmail('')
    setOtp('')
    setError('')
  }

  const formatDateTime = dtStr => {
    if (!dtStr) return 'N/A'
    const dt = new Date(dtStr)
    return dt.toLocaleString(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short',
    })
  }

  const getStatusClass = status => {
    switch (status?.toUpperCase()) {
      case 'CONFIRMED':
      case 'SUCCESS':
        return styles.statusConfirmed
      case 'PENDING':
        return styles.statusPending
      default:
        return styles.statusFailed
    }
  }

  const showForm = step === 'form' && !user
  const showList = step === 'list' || !!user

  return (
    <Panel
      title="Passenger Booking History"
      description={
        user
          ? `View booking history for logged-in profile: ${user.username}`
          : "Lookup booking history using your registered email and verification OTP."
      }
      endpoint="/bookings/history"
      method="GET"
    >
      {error && <div className={styles.errorBox}>{error}</div>}

      {showForm && (
        <form className={styles.form} onSubmit={handleGuestSubmit}>
          <div className={styles.formGroup}>
            <label className={styles.label} htmlFor="email">Email Address</label>
            <input
              id="email"
              type="email"
              className={styles.input}
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="Enter email address used for booking"
              required
              disabled={loading}
            />
          </div>

          <div className={styles.formGroup}>
            <label className={styles.label} htmlFor="otp">Verification OTP</label>
            <input
              id="otp"
              type="text"
              className={styles.input}
              value={otp}
              onChange={e => setOtp(e.target.value)}
              placeholder="Enter OTP sent to email (use 000000)"
              required
              disabled={loading}
            />
          </div>

          <button type="submit" className={styles.submitBtn} disabled={loading}>
            {loading ? 'Verifying...' : 'Retrieve Bookings'}
          </button>
        </form>
      )}

      {showList && (
        <div className={styles.historyContainer}>
          {!user && (
            <div className={styles.resetHeader}>
              <button className={styles.resetBtn} onClick={handleReset}>
                ← Lookup Another Email
              </button>
            </div>
          )}

          {loading ? (
            <div className={styles.loadingText}>Fetching history records...</div>
          ) : historyList.length === 0 ? (
            <div className={styles.emptyText}>No booking history records found.</div>
          ) : (
            <div className={styles.list}>
              {historyList.map(b => (
                <div key={b.bookingId} className={styles.card}>
                  <div className={styles.cardHeader}>
                    <div>
                      <span className={styles.cardIdLabel}>Booking ID:</span>{' '}
                      <span className={styles.cardId}>{b.bookingId}</span>
                    </div>
                    <span className={`${styles.statusBadge} ${getStatusClass(b.status)}`}>
                      {b.status}
                    </span>
                  </div>

                  <div className={styles.uuidRow}>
                    <span className={styles.uuidLabel}>Ref UUID:</span>{' '}
                    <code className={styles.uuid}>{b.uuid}</code>
                  </div>

                  <div className={styles.journey}>
                    <div className={styles.cityInfo}>
                      <div className={styles.cityName}>{b.sourceCity}</div>
                      <div className={styles.time}>{formatDateTime(b.startTime)}</div>
                    </div>
                    <div className={styles.arrow}>➔</div>
                    <div className={styles.cityInfo}>
                      <div className={styles.cityName}>{b.destinationCity}</div>
                      <div className={styles.time}>{formatDateTime(b.arrivalTime)}</div>
                    </div>
                  </div>

                  <div className={styles.metaGrid}>
                    <div className={styles.metaItem}>
                      <div className={styles.metaLabel}>Bus Service</div>
                      <div className={styles.metaVal}>{b.busName} ({b.busNumber})</div>
                    </div>

                    <div className={styles.metaItem}>
                      <div className={styles.metaLabel}>Seats Booked</div>
                      <div className={styles.metaVal}>
                        {b.seatNumbers && b.seatNumbers.length > 0 ? (
                          <div className={styles.seatPills}>
                            {b.seatNumbers.map(seat => (
                              <span key={seat} className={styles.seatPill}>{seat}</span>
                            ))}
                          </div>
                        ) : (
                          'N/A'
                        )}
                      </div>
                    </div>

                    <div className={styles.metaItem}>
                      <div className={styles.metaLabel}>Booking Time</div>
                      <div className={styles.metaVal}>{formatDateTime(b.bookingTime)}</div>
                    </div>

                    <div className={styles.metaItem}>
                      <div className={styles.metaLabel}>Fare Total</div>
                      <div className={styles.metaPrice}>₹{b.totalAmount}</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </Panel>
  )
}
