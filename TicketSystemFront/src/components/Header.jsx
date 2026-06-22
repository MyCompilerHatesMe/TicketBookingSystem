import styles from './Header.module.css'

export default function Header({ user, onLogout, onLogin }) {
  const isAdmin = user?.roles.includes('ROLE_ADMIN')
  return (
    <header className={styles.header}>
      <div className={styles.tricolor}>
        <span className={styles.saffron} />
        <span className={styles.white} />
        <span className={styles.green} />
      </div>
      <div className={styles.inner}>
        <div className={styles.brand}>
          <div className={styles.emblem}>
            <svg width="40" height="40" viewBox="0 0 44 44" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="22" cy="22" r="20" fill="#004080" stroke="#FF9933" strokeWidth="1.5"/>
              <text x="22" y="27" textAnchor="middle" fontSize="13" fontWeight="700" fill="#ffffff" fontFamily="Inter,sans-serif">TBS</text>
            </svg>
          </div>
          <span className={styles.system}>
            Ticket Booking System
            {isAdmin && <span className={styles.consoleSuffix}> - Admin Console</span>}
          </span>
        </div>
        
        {user ? (
          <div className={styles.meta}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <span style={{ color: 'rgba(255,255,255,0.85)', fontSize: '13.5px', fontWeight: '500' }}>
                {user.username}
              </span>
              <span style={{
                background: isAdmin ? 'var(--saffron)' : 'var(--india-green)',
                color: 'var(--white)',
                padding: '2px 8px',
                borderRadius: '12px',
                fontSize: '11px',
                fontWeight: '600',
                textTransform: 'uppercase',
                letterSpacing: '0.05em'
              }}>
                {isAdmin ? 'Admin' : 'User'}
              </span>
              <button 
                onClick={onLogout}
                style={{
                  background: 'rgba(255,255,255,0.1)',
                  color: '#ffffff',
                  border: '1px solid rgba(255,255,255,0.2)',
                  borderRadius: '4px',
                  padding: '5px 12px',
                  fontSize: '12.5px',
                  fontWeight: '600',
                  cursor: 'pointer',
                  fontFamily: 'Inter,sans-serif',
                  transition: 'background 0.2s'
                }}
                onMouseOver={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.2)'}
                onMouseOut={(e) => e.currentTarget.style.background = 'rgba(255,255,255,0.1)'}
              >
                Logout
              </button>
            </div>
            <span className={styles.version}>v1.0.0</span>
          </div>
        ) : (
          <div className={styles.meta}>
            <button 
              onClick={onLogin}
              style={{
                background: 'var(--saffron)',
                color: '#ffffff',
                border: 'none',
                borderRadius: '4px',
                padding: '6px 16px',
                fontSize: '13px',
                fontWeight: '600',
                cursor: 'pointer',
                fontFamily: 'Inter,sans-serif',
                transition: 'background 0.2s'
              }}
              onMouseOver={(e) => e.currentTarget.style.background = '#e68019'}
              onMouseOut={(e) => e.currentTarget.style.background = 'var(--saffron)'}
            >
              Sign In
            </button>
            <span className={styles.version}>v1.0.0</span>
          </div>
        )}
      </div>
    </header>
  )
}
