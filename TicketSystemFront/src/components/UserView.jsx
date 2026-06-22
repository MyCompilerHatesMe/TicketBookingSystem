import { useState } from 'react'
import TripSearch from './TripSearch'
import BookingPanel from './BookingPanel'

export default function UserView() {
  const [selectedTrip, setSelectedTrip] = useState(null)

  if (selectedTrip) {
    return <BookingPanel trip={selectedTrip} onBack={() => setSelectedTrip(null)} />
  }

  return <TripSearch onSelectTrip={setSelectedTrip} />
}
