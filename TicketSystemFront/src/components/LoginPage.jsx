import { useState } from 'react'
import { apiPost } from '../api'
import { setToken } from '../auth'
import styles from './LoginPage.module.css'

export default function LoginPage({ onLoginSuccess, onCancel }) {
  const [isRegister, setIsRegister] = useState(false)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [number, setNumber] = useState('')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async e => {
    e.preventDefault()
    setError('')
    setSuccess('')
    setLoading(true)

    try {
      if (isRegister) {
        // ponytail: submit all user master fields during registration
        const res = await apiPost('/auth/register', {
          username,
          password,
          name,
          email,
          number,
          roles: ['ROLE_USER'],
        })

        if (res.status === 200 || res.status === 201) {
          setSuccess('Registration successful! Please log in with your credentials.')
          setIsRegister(false)
          setPassword('')
        } else {
          const errMsg = res.data?.message || res.data?.error || (typeof res.data === 'string' ? res.data : 'Registration failed.')
          setError(errMsg)
        }
      } else {
        const res = await apiPost('/auth/login', {
          username,
          password,
        })

        if (res.status === 200) {
          const token = res.data
          if (token) {
            setToken(token)
            onLoginSuccess(token)
          } else {
            setError('Received empty token from server.')
          }
        } else {
          const errMsg = res.data?.message || res.data?.error || (typeof res.data === 'string' ? res.data : 'Authentication failed.')
          setError(errMsg)
        }
      }
    } catch {
      setError('A network error occurred. Please check if the server is running.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <div className={styles.tricolor}>
          <span className={styles.saffron} />
          <span className={styles.white} />
          <span className={styles.green} />
        </div>
        <div className={styles.content}>
          <div className={styles.header}>
            <div className={styles.logo}>TBS</div>
            <h2 className={styles.title}>Ticket Booking System</h2>
            <p className={styles.subtitle}>
              {isRegister ? 'Create an account to book your tickets' : 'Log in to manage bookings and purchase tickets'}
            </p>
          </div>

          <form onSubmit={handleSubmit}>
            {error && <div className={styles.errorBox}>{error}</div>}
            {success && <div className={styles.successBox}>{success}</div>}

            <div className={styles.formGroup}>
              <label className={styles.label} htmlFor="username">Username</label>
              <input
                className={styles.input}
                type="text"
                id="username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="Enter username"
                required
                disabled={loading}
              />
            </div>

            <div className={styles.formGroup}>
              <label className={styles.label} htmlFor="password">Password</label>
              <input
                className={styles.input}
                type="password"
                id="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="Enter password"
                required
                disabled={loading}
              />
            </div>

            {isRegister && (
              <>
                <div className={styles.formGroup}>
                  <label className={styles.label} htmlFor="name">Full Name</label>
                  <input
                    className={styles.input}
                    type="text"
                    id="name"
                    value={name}
                    onChange={e => setName(e.target.value)}
                    placeholder="Enter full name"
                    required
                    disabled={loading}
                  />
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label} htmlFor="email">Email Address</label>
                  <input
                    className={styles.input}
                    type="email"
                    id="email"
                    value={email}
                    onChange={e => setEmail(e.target.value)}
                    placeholder="Enter email address"
                    required
                    disabled={loading}
                  />
                </div>

                <div className={styles.formGroup}>
                  <label className={styles.label} htmlFor="number">Mobile Number</label>
                  <input
                    className={styles.input}
                    type="tel"
                    id="number"
                    value={number}
                    onChange={e => setNumber(e.target.value)}
                    placeholder="Enter mobile number"
                    required
                    disabled={loading}
                  />
                </div>
              </>
            )}

            <button className={styles.submitBtn} type="submit" disabled={loading}>
              {loading ? 'Processing...' : isRegister ? 'Register' : 'Log In'}
            </button>
          </form>

          <p className={styles.footerText}>
            {isRegister ? 'Already have an account?' : "Don't have an account?"}
            <button
              className={styles.toggleLink}
              onClick={() => {
                setIsRegister(!isRegister)
                setError('')
                setSuccess('')
              }}
              disabled={loading}
            >
              {isRegister ? 'Log In' : 'Register'}
            </button>
          </p>

          {onCancel && (
            <div style={{ marginTop: '16px', textAlign: 'center' }}>
              <button
                type="button"
                className={styles.toggleLink}
                style={{ color: 'var(--gray-500)', fontSize: '13px' }}
                onClick={onCancel}
                disabled={loading}
              >
                ← Continue as Guest
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
