import { useState } from 'react'
import Header from './components/Header'
import Sidebar from './components/Sidebar'
import BusPanel from './components/BusPanel'
import RoutePanel from './components/RoutePanel'
import TripPanel from './components/TripPanel'
import UserView from './components/UserView'
import LoginPage from './components/LoginPage'
import { getToken, getUser, clearToken } from './auth'
import styles from './App.module.css'

const ADMIN_SECTIONS = [
  { id: 'buses',  label: 'Bus Management',   icon: '🚌' },
  { id: 'routes', label: 'Route Management',  icon: '🗺️' },
  { id: 'trips',  label: 'Trip Scheduling',   icon: '📅' },
]

const USER_SECTIONS = [
  { id: 'search', label: 'Find & Book Buses', icon: '🔍' },
]

export default function App() {
  const [, setTokenState] = useState(() => getToken())
  const [user, setUserState] = useState(() => getUser())
  const [showLogin, setShowLogin] = useState(false)
  
  const [active, setActive] = useState(() => {
    const initialUser = getUser()
    return initialUser?.roles.includes('ROLE_ADMIN') ? 'buses' : 'search'
  })

  const isAdmin = user?.roles.includes('ROLE_ADMIN')

  const handleLoginSuccess = () => {
    setTokenState(getToken())
    const newUser = getUser()
    setUserState(newUser)
    setShowLogin(false)
    setActive(newUser?.roles.includes('ROLE_ADMIN') ? 'buses' : 'search')
  }

  const handleLogout = () => {
    clearToken()
    setTokenState(null)
    setUserState(null)
    setActive('search')
  }

  if (showLogin) {
    return <LoginPage onLoginSuccess={handleLoginSuccess} onCancel={() => setShowLogin(false)} />
  }

  const sections = isAdmin ? [...ADMIN_SECTIONS, ...USER_SECTIONS] : USER_SECTIONS

  return (
    <div className={styles.root}>
      <Header user={user} onLogout={handleLogout} onLogin={() => setShowLogin(true)} />
      <div className={styles.body}>
        <Sidebar sections={sections} active={active} onSelect={setActive} />
        <main className={styles.main}>
          {active === 'buses' && isAdmin && <BusPanel />}
          {active === 'routes' && isAdmin && <RoutePanel />}
          {active === 'trips' && isAdmin && <TripPanel />}
          {active === 'search' && <UserView key="user" />}
        </main>
      </div>
    </div>
  )
}
