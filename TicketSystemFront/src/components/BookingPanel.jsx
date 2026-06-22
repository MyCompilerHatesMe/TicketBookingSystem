import { useState, useEffect } from 'react'
import { apiGet, apiPost } from '../api'
import { getUser } from '../auth'
import styles from './BookingPanel.module.css'

const STATUS_COLOR = { AVAILABLE: 'available', PENDING: 'pending', BOOKED: 'booked' }

export default function BookingPanel({ trip, onBack }) {
  const [seats, setSeats] = useState([])
  const [seatsLoading, setSeatsLoading] = useState(true)
  const [selected, setSelected] = useState([])

  const [isGuest, setIsGuest] = useState(() => !getUser())
  const [guestEmail, setGuestEmail] = useState('')
  const [guestPhone, setGuestPhone] = useState('')

  const [response, setResponse] = useState(null)
  const [loading, setLoading] = useState(false)
  const [accountExists, setAccountExists] = useState(false)

  useEffect(() => {
    apiGet(`/trips/${trip.tripId}/seats`, {}).then(res => {
      if (res.status === 200) setSeats(res.data)
      setSeatsLoading(false)
    })
  }, [trip.tripId])

  const toggleSeat = seat => {
    if (seat.status !== 'AVAILABLE') return
    setSelected(sel =>
      sel.includes(seat.seatId)
        ? sel.filter(id => id !== seat.seatId)
        : [...sel, seat.seatId]
    )
  }

  const book = async (bypass = false) => {
    if (selected.length === 0) return
    setLoading(true)
    setResponse(null)
    setAccountExists(false)

    const body = isGuest
      ? { tripId: trip.tripId, seatIds: selected, isGuest: true, userId: null,
          guestInfo: { email: guestEmail, number: guestPhone }, bypassAccountCheck: bypass }
      : { tripId: trip.tripId, seatIds: selected, isGuest: false,
          userId: null, guestInfo: null, bypassAccountCheck: false }

    const res = await apiPost('/bookings', body)
    setLoading(false)

    if (res.status === 303) {
      setAccountExists(true)
    } else {
      setResponse(res)
    }
  }

  const fmt = iso => new Date(iso).toLocaleString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
  })

  const totalFare = selected.length * trip.fare

  return (
    <div>
      <div className={styles.backRow}>
        <button className={styles.back} onClick={onBack}>← Back to results</button>
      </div>

      <div className={styles.tripSummary}>
        <div className={styles.summaryRoute}>
          <span className={styles.summaryCity}>{trip.sourceCity}</span>
          <span className={styles.summaryArrow}>→</span>
          <span className={styles.summaryCity}>{trip.destinationCity}</span>
        </div>
        <div className={styles.summaryMeta}>
          <span>{fmt(trip.startTime)} - {fmt(trip.arrivalTime)}</span>
          <span className={styles.dot}>·</span>
          <span>{trip.busName}</span>
          <span className={styles.dot}>·</span>
          <span>₹{trip.fare.toLocaleString('en-IN')} / seat</span>
        </div>
      </div>

      <div className={styles.panels}>
        {/* Seat map */}
        <div className={styles.card}>
          <div className={styles.cardTitle}>Select Seats</div>
          {seatsLoading ? (
            <div className={styles.loadingSeats}>Loading seat layout…</div>
          ) : (
            <>
              <div className={styles.legend}>
                <span className={`${styles.legendDot} ${styles.available}`} /> Available
                <span className={`${styles.legendDot} ${styles.selected}`} /> Selected
                <span className={`${styles.legendDot} ${styles.pending}`} /> Held
                <span className={`${styles.legendDot} ${styles.booked}`} /> Booked
              </div>
              <div className={styles.seatGrid}>
                {seats.map(seat => (
                  <button
                    key={seat.seatId}
                    className={`${styles.seat} ${styles[STATUS_COLOR[seat.status]]} ${selected.includes(seat.seatId) ? styles.seatSelected : ''}`}
                    onClick={() => toggleSeat(seat)}
                    disabled={seat.status !== 'AVAILABLE'}
                    title={`${seat.seatNumber} - ${seat.status}`}
                  >
                    {seat.seatNumber}
                  </button>
                ))}
              </div>
              {selected.length > 0 && (
                <div className={styles.selectionSummary}>
                  {selected.length} seat{selected.length !== 1 ? 's' : ''} selected
                  <span className={styles.totalFare}>Total: ₹{totalFare.toLocaleString('en-IN')}</span>
                </div>
              )}
            </>
          )}
        </div>

        {/* Checkout */}
        <div className={styles.card}>
          <div className={styles.cardTitle}>Checkout</div>

          <div className={styles.checkoutType}>
            <button
              className={`${styles.typeBtn} ${!isGuest ? styles.typeBtnActive : ''}`}
              onClick={() => setIsGuest(false)}
              disabled={!getUser()}
              style={!getUser() ? { opacity: 0.5, cursor: 'not-allowed' } : {}}
              title={!getUser() ? 'Please sign in to book as a registered user' : ''}
            >Registered User</button>
            <button
              className={`${styles.typeBtn} ${isGuest ? styles.typeBtnActive : ''}`}
              onClick={() => setIsGuest(true)}
            >Guest</button>
          </div>

          {!isGuest ? (
            <div className={styles.checkoutForm}>
              <p style={{ fontSize: '13px', color: 'var(--gray-700)', margin: '10px 0' }}>
                You are logged in as <strong>{getUser()?.username || 'user'}</strong>. Your booking will be registered under your profile.
              </p>
            </div>
          ) : (
            <div className={styles.checkoutForm}>
              <label className={styles.fieldLabel}>Email</label>
              <input className={styles.input} type="email" value={guestEmail}
                onChange={e => setGuestEmail(e.target.value)} placeholder="traveler@example.com" />
              <label className={styles.fieldLabel} style={{ marginTop: 12 }}>Phone</label>
              <input className={styles.input} type="tel" value={guestPhone}
                onChange={e => setGuestPhone(e.target.value)} placeholder="9876543210" />
            </div>
          )}

          {accountExists && (
            <div className={styles.accountExistsBox}>
              <div className={styles.accountExistsTitle}>Account already exists</div>
              <p className={styles.accountExistsMsg}>
                An account is registered with this email. You can log in, or continue as a guest anyway.
              </p>
              <div className={styles.accountExistsActions}>
                <button className={styles.bypassBtn} onClick={() => book(true)}>Continue as Guest</button>
              </div>
            </div>
          )}

          {response && (
            <div className={`${styles.responseBox} ${response.status >= 400 ? styles.responseError : styles.responseSuccess}`}>
              <div className={styles.responseStatus}>HTTP {response.status} {response.statusText}</div>
              <pre className={styles.responseBody}>{JSON.stringify(response.data, null, 2)}</pre>
            </div>
          )}

          <button
            className={styles.bookBtn}
            onClick={() => book(false)}
            disabled={loading || selected.length === 0}
          >
            {loading ? 'Processing…' : `Confirm & Hold ${selected.length > 0 ? `(${selected.length} seat${selected.length !== 1 ? 's' : ''})` : ''}`}
          </button>
          {selected.length === 0 && (
            <p className={styles.hint}>Select at least one available seat to proceed.</p>
          )}
        </div>
      </div>
    </div>
  )
}
