import { useEffect, useRef, useState } from 'react'
import styles from './Features.module.css'

const IconMonitor = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="3" width="20" height="14" rx="2"/>
    <path d="M8 21h8M12 17v4"/>
    <polyline points="7 9 12 14 17 9"/>
  </svg>
)

const IconChart = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
  </svg>
)

const IconControl = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="3"/>
    <path d="M19.07 4.93a10 10 0 0 1 0 14.14M4.93 4.93a10 10 0 0 0 0 14.14"/>
    <path d="M16.24 7.76a6 6 0 0 1 0 8.49M7.76 7.76a6 6 0 0 0 0 8.49"/>
  </svg>
)

const T = {
  ru: {
    h2:  'Всё необходимое\nдля комфортного обогрева',
    sub: 'CryptoTerm даёт полный контроль над майнером как источником тепла — без лишних сложностей.',
    features: [
      {
        Icon: IconMonitor,
        iconClass: 'icon-purple',
        title: 'Мониторинг',
        desc:  'Температура, хэшрейт и состояние устройств — в реальном времени. Вы всегда знаете, что происходит с майнерами.',
        points: ['Температура платы и чипов', 'Хэшрейт и нагрузка', 'Состояние вентиляторов'],
      },
      {
        Icon: IconChart,
        iconClass: 'icon-teal',
        title: 'История и графики',
        desc:  'Анализируйте работу устройства за любой период: выявляйте просадки, диагностируйте сбои, отслеживайте динамику.',
        points: ['Графики за любой период', 'Диагностика отклонений', 'Экспорт данных'],
      },
      {
        Icon: IconControl,
        iconClass: 'icon-green',
        title: 'Удалённое управление',
        desc:  'Переключайте режимы работы прямо из приложения. Снизьте шум ночью или повысьте мощность в мороз.',
        points: ['Режим ECO — тихо и экономно', 'Standard — баланс', 'Overclock — максимальный нагрев'],
      },
    ],
  },
  en: {
    h2:  'Everything you need\nfor comfortable heating',
    sub: 'CryptoTerm gives you full control over your miner as a heat source — without unnecessary complexity.',
    features: [
      {
        Icon: IconMonitor,
        iconClass: 'icon-purple',
        title: 'Live Monitoring',
        desc:  'Temperature, hashrate, and device status — in real time. Always know what your miner is doing.',
        points: ['Board and chip temperature', 'Hashrate and load', 'Fan status'],
      },
      {
        Icon: IconChart,
        iconClass: 'icon-teal',
        title: 'History and Charts',
        desc:  'Analyze device performance over any period: spot drops, diagnose issues, track trends over time.',
        points: ['Charts for any period', 'Anomaly detection', 'Data export'],
      },
      {
        Icon: IconControl,
        iconClass: 'icon-green',
        title: 'Remote Control',
        desc:  'Switch operating modes right from the app. Reduce noise at night or boost power in cold weather.',
        points: ['ECO mode — quiet and efficient', 'Standard — balanced', 'Overclock — maximum heat output'],
      },
    ],
  },
}

function FeatureCard({ Icon, iconClass, title, desc, points, index }) {
  const ref = useRef(null)
  const [visible, setVisible] = useState(false)

  useEffect(() => {
    const el = ref.current
    if (!el) return
    const obs = new IntersectionObserver(([e]) => {
      if (e.isIntersecting) { setVisible(true); obs.disconnect() }
    }, { threshold: 0.12 })
    obs.observe(el)
    return () => obs.disconnect()
  }, [])

  return (
    <div
      ref={ref}
      className={`${styles.card} ${visible ? styles.visible : ''}`}
      style={{ transitionDelay: `${index * 100}ms` }}
    >
      <div className={`${styles.iconWrap} ${iconClass}`}>
        <Icon />
      </div>
      <h3 className={styles.cardTitle}>{title}</h3>
      <p className={styles.cardDesc}>{desc}</p>
      <ul className={styles.points}>
        {points.map(p => (
          <li key={p} className={styles.point}>
            <span className={styles.pointDot} />
            <span>{p}</span>
          </li>
        ))}
      </ul>
    </div>
  )
}

export default function Features({ lang }) {
  const t = T[lang]
  return (
    <section id="features">
      <div className="container">
        <div className={styles.header}>
          <h2 className={styles.h2}>
            {t.h2.split('\n').map((line, i) => <span key={i}>{line}{i === 0 && <br />}</span>)}
          </h2>
          <p className={styles.sub}>{t.sub}</p>
        </div>
        <div className={styles.grid}>
          {t.features.map((f, i) => <FeatureCard key={f.title} {...f} index={i} />)}
        </div>
      </div>
    </section>
  )
}
