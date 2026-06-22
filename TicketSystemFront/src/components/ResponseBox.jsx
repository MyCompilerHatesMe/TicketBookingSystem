import styles from './ResponseBox.module.css'

export default function ResponseBox({ response }) {
  if (!response) return null

  const isError = response.status >= 400

  return (
    <div className={`${styles.box} ${isError ? styles.error : styles.success}`}>
      <div className={styles.header}>
        <span className={styles.status}>
          <span className={styles.dot} />
          HTTP {response.status} {response.statusText}
        </span>
        <span className={styles.label}>{isError ? 'Error Response' : 'Success Response'}</span>
      </div>
      <pre className={styles.body}>{JSON.stringify(response.data, null, 2)}</pre>
    </div>
  )
}
