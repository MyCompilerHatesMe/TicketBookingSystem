import styles from './Panel.module.css'

export default function Panel({ title, description, endpoint, method = 'POST', children }) {
  return (
    <div className={styles.panel}>
      <div className={styles.header}>
        <div className={styles.titleRow}>
          <h2 className={styles.title}>{title}</h2>
          <span className={styles.endpointBadge}>
            <span className={styles.method}>{method}</span>
            <code className={styles.path}>{endpoint}</code>
          </span>
        </div>
        {description && <p className={styles.desc}>{description}</p>}
      </div>
      <div className={styles.body}>{children}</div>
    </div>
  )
}
