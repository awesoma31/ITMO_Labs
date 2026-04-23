import styles from './Hero.module.css'

const T = {
  ru: {
    title1:  'Превратите ASIC-майнеры',
    title2:  'в управляемое отопление',
    badge:   'Для тех, кто хочет экономить на отоплении',
    desc:    'CryptoTerm — приложение для тех, кто использует ASIC-майнеры как источник тепла. Следите за температурой и стабильностью работы, анализируйте историю метрик и быстро переключайте режимы, чтобы поддерживать комфортную температуру и снижать риск простоев.',
    stats: [
      { value: 'Live',     label: 'Мониторинг в реальном времени' },
      { value: 'ECO / OC', label: 'Управление режимами' },
      { value: 'История',  label: 'Графики и аналитика' },
    ],
  },
  en: {
    title1:  'Turn ASIC miners into',
    title2:  'controllable home heating',
    badge:   'For those who want to save on heating',
    desc:    'CryptoTerm helps you use ASIC miners as a stable, controllable source of heat. Monitor miner temperature and performance in real time, review historical charts, and quickly switch operating modes to balance comfort, noise, and power usage.',
    stats: [
      { value: 'Live',     label: 'Real-time monitoring' },
      { value: 'ECO / OC', label: 'Mode control' },
      { value: 'History',  label: 'Charts and analytics' },
    ],
  },
}

export default function Hero({ lang }) {
  const t = T[lang]
  return (
    <section className={styles.hero}>
      <div className="container">

        <h1 className={styles.title}>
          {t.title1}<br />
          <span className="gradient-text">{t.title2}</span>
        </h1>

        <p className={styles.desc}>{t.desc}</p>

        <div className={styles.badge}>
          <span className={styles.dot} />
          {t.badge}
        </div>

        <div className={styles.statsRow}>
          {t.stats.map((s, i) => (
            <div key={i} className={styles.statWrap}>
              <div className={styles.stat}>
                <div className={styles.statValue}>{s.value}</div>
                <div className={styles.statLabel}>{s.label}</div>
              </div>
              {i < t.stats.length - 1 && <div className={styles.divider} />}
            </div>
          ))}
        </div>

      </div>
    </section>
  )
}
