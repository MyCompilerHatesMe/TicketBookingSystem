import styles from './Sidebar.module.css'

export default function Sidebar({ sections, active, onSelect }) {
  return (
    <aside className={styles.sidebar}>
      <div className={styles.navLabel}>Modules</div>
      <nav className={styles.nav}>
        {sections.map(s => (
          <button
            key={s.id}
            className={`${styles.item} ${active === s.id ? styles.active : ''}`}
            onClick={() => onSelect(s.id)}
          >
            <span className={styles.icon}>{s.icon}</span>
            <span className={styles.label}>{s.label}</span>
            {active === s.id && <span className={styles.indicator} />}
          </button>
        ))}
      </nav>
      <div className={styles.footer}>
        <div className={styles.footerLabel}>Endpoint</div>
        <code className={styles.footerCode}>localhost:8080</code>
      </div>
    </aside>
  )
}
