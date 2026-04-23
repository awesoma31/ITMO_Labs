import styles from './Screenshots.module.css'

const T = {
  ru: {
    h2:  'Мобильный интерфейс',
    sub: 'Весь контроль над майнером — в кармане. Графики, статус устройств и история прямо с телефона.',
    caps: [
      'Хэшрейт и температура в реальном времени, история за любой период',
      'Список устройств, статус и быстрый расчёт прибыли',
    ],
  },
  en: {
    h2:  'Mobile interface',
    sub: 'Full miner control in your pocket. Charts, device status, and history right from your phone.',
    caps: [
      'Hashrate and temperature in real time, history for any period',
      'Device list, status, and quick profit calculation',
    ],
  },
}

function Phone({ src, caption, delay }) {
  return (
    <div className={styles.phoneWrap} style={{ '--delay': delay }}>
      <div className={styles.phone}>
        <div className={styles.island} />
        <img src={src} alt={caption} className={styles.screen} draggable={false} />
      </div>
      <p className={styles.caption}>{caption}</p>
    </div>
  )
}

export default function Screenshots({ lang }) {
  const t = T[lang]
  return (
    <section id="screenshots" className={styles.section}>
      <div className="container">
        <div className={styles.header}>
          <h2 className={styles.h2}>{t.h2}</h2>
          <p className={styles.sub}>{t.sub}</p>
        </div>

        <div className={styles.scene}>
          <div className={styles.glow} />
          <Phone src="/screen1.png" caption={t.caps[0]} delay="0s" />
          <Phone src="/screen2.png" caption={t.caps[1]} delay="0.12s" />
        </div>
      </div>
    </section>
  )
}
