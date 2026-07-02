import { useState } from 'react'
import styles from './TravelPlanner.module.css'
import { apiPost, apiGet } from '../api'

export default function TravelPlanner() {
  const [startCity, setStartCity] = useState('')
  const [startDate, setStartDate] = useState('')
  const [destinations, setDestinations] = useState([{ cityName: '', arrivalDate: '' }])
  const [flexibleOrder, setFlexibleOrder] = useState(false)
  const [results, setResults] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  // Modal states
  const [modalCity, setModalCity] = useState(null)
  const [modalSpots, setModalSpots] = useState([])
  const [modalLoading, setModalLoading] = useState(false)
  const [modalError, setModalError] = useState(null)

  const handleCityClick = async (city) => {
    setModalCity(city)
    setModalLoading(true)
    setModalError(null)
    setModalSpots([])

    try {
      const res = await apiGet('/tourism', { city })
      if (res.status === 401 || res.status === 403) {
        setModalError('You must be logged in to view tourism data.')
        return
      }
      if (res.status >= 200 && res.status < 300) {
        if (res.data && res.data.placesData) {
          const parsed = JSON.parse(res.data.placesData)
          if (parsed.results && parsed.results.length > 0) {
            // Shuffle and pick 3-4 spots
            const shuffled = [...parsed.results].sort(() => 0.5 - Math.random())
            const count = Math.floor(Math.random() * 2) + 3 // 3 or 4
            setModalSpots(shuffled.slice(0, count))
          } else {
            setModalError('No spots found for this city.')
          }
        } else {
          setModalError('No data available for this city.')
        }
      } else {
        throw new Error(res.data?.message || 'Failed to fetch tourism data')
      }
    } catch (err) {
      setModalError(err.message)
    } finally {
      setModalLoading(false)
    }
  }

  const closeModal = () => {
    setModalCity(null)
  }

  const handleAddDestination = () => {
    setDestinations([...destinations, { cityName: '', arrivalDate: '' }])
  }

  const handleRemoveDestination = (index) => {
    setDestinations(destinations.filter((_, i) => i !== index))
  }

  const handleDestinationChange = (index, field, value) => {
    const newDestinations = [...destinations]
    newDestinations[index][field] = value
    setDestinations(newDestinations)
  }

  const handleSearch = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError(null)
    setResults(null)

    try {
      const payload = {
        startCity,
        startDate: startDate || null,
        destinations: destinations.map(d => ({
          cityName: d.cityName,
          arrivalDate: d.arrivalDate || null
        })),
        flexibleOrder
      }

      const res = await apiPost('/trips/plan', payload)

      if (res.status >= 200 && res.status < 300) {
        setResults(res.data)
      } else {
        throw new Error(res.data?.message || 'Failed to fetch travel plan')
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const renderItinerary = (option, title) => {
    if (!option || !option.trips || option.trips.length === 0) return null

    return (
      <div className={styles.itineraryCard}>
        <h3 className={styles.itineraryTitle}>{title}</h3>
        <div className={styles.itinerarySummary}>
          <span>Total Fare: ₹{option.totalFare}</span>
          <span>Total Distance: {option.totalDistance} km</span>
        </div>
        <div className={styles.tripList}>
          {option.trips.map((trip, i) => (
            <div key={i} className={styles.tripItem}>
              <div className={styles.tripHeader}>
                <strong>
                  Hop {i + 1}:{' '}
                  <span className={styles.cityLink} onClick={() => handleCityClick(trip.sourceCity)}>
                    {trip.sourceCity}
                  </span>
                  {' '}➔{' '}
                  <span className={styles.cityLink} onClick={() => handleCityClick(trip.destinationCity)}>
                    {trip.destinationCity}
                  </span>
                </strong>
              </div>
              <div className={styles.tripDetails}>
                <span>Bus: {trip.busName} ({trip.busType})</span>
                <span>Departs: {new Date(trip.startTime).toLocaleString()}</span>
                <span>Arrives: {new Date(trip.arrivalTime).toLocaleString()}</span>
                <span>₹{trip.fare} | {trip.distanceKm} km</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className={styles.container}>
      <h2 className={styles.title}>Multi-City Travel Planner</h2>
      
      <form className={styles.form} onSubmit={handleSearch}>
        <div className={styles.startSection}>
          <div className={styles.field}>
            <label>Starting City</label>
            <input 
              required 
              value={startCity} 
              onChange={e => setStartCity(e.target.value)} 
              placeholder="e.g. Hyderabad"
            />
          </div>
          <div className={styles.field}>
            <label>Start Date (Optional)</label>
            <input 
              type="date" 
              value={startDate} 
              onChange={e => setStartDate(e.target.value)} 
            />
          </div>
        </div>

        <div className={styles.destinationsSection}>
          <h3>Destinations</h3>
          {destinations.map((dest, i) => (
            <div key={i} className={styles.destinationRow}>
              <div className={styles.field}>
                <label>City Name</label>
                <input 
                  required 
                  value={dest.cityName} 
                  onChange={e => handleDestinationChange(i, 'cityName', e.target.value)} 
                  placeholder="e.g. Bangalore"
                />
              </div>
              <div className={styles.field}>
                <label>Arrival Date (Optional)</label>
                <input 
                  type="date" 
                  value={dest.arrivalDate} 
                  onChange={e => handleDestinationChange(i, 'arrivalDate', e.target.value)} 
                />
              </div>
              {destinations.length > 1 && (
                <button 
                  type="button" 
                  className={styles.removeBtn} 
                  onClick={() => handleRemoveDestination(i)}
                >
                  ✕
                </button>
              )}
            </div>
          ))}
          <button type="button" className={styles.addBtn} onClick={handleAddDestination}>
            + Add Destination
          </button>
        </div>

        <div className={styles.optionsSection}>
          <label className={styles.checkboxLabel}>
            <input 
              type="checkbox" 
              checked={flexibleOrder} 
              onChange={e => setFlexibleOrder(e.target.checked)} 
            />
            Flexible Order (Find the best path visiting these cities)
          </label>
        </div>

        <button type="submit" className={styles.submitBtn} disabled={loading}>
          {loading ? 'Planning...' : 'Generate Travel Plan'}
        </button>
      </form>

      {error && <div className={styles.error}>{error}</div>}

      {results && (
        <div className={styles.resultsSection}>
          {(!results.cheapestRoute && !results.shortestDistanceRoute) ? (
            <div className={styles.noResults}>No valid routes found connecting these cities.</div>
          ) : (
            <div className={styles.itineraries}>
              {renderItinerary(results.cheapestRoute, "Cheapest Route")}
              {renderItinerary(results.shortestDistanceRoute, "Shortest Route")}
            </div>
          )}
        </div>
      )}

      {modalCity && (
        <div className={styles.modalOverlay} onClick={closeModal}>
          <div className={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <button className={styles.modalClose} onClick={closeModal}>✕</button>
            <h3 className={styles.modalTitle}>Top Spots in {modalCity}</h3>
            
            {modalLoading ? (
              <div className={styles.loadingSpots}>Finding the best spots...</div>
            ) : modalError ? (
              <div className={styles.errorSpots}>{modalError}</div>
            ) : (
              <ul className={styles.spotsList}>
                {modalSpots.map((spot, i) => (
                  <li key={i} className={styles.spotItem}>
                    <strong>{spot.name}</strong>
                    {spot.location && spot.location.formatted_address && (
                      <div style={{ fontSize: '11px', color: '#666', marginTop: '4px' }}>
                        {spot.location.formatted_address}
                      </div>
                    )}
                  </li>
                ))}
              </ul>
            )}
          </div>
        </div>
      )}
    </div>
  )
}
