import styles from './Field.module.css'

export default function Field({ label, name, type = 'text', value, onChange, placeholder, required, min, max }) {
  return (
    <div className={styles.field}>
      <label className={styles.label} htmlFor={name}>
        {label}
        {required && <span className={styles.req}>*</span>}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        required={required}
        min={min}
        max={max}
        className={styles.input}
        autoComplete="off"
      />
    </div>
  )
}
