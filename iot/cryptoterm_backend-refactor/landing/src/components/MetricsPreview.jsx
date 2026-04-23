import { useEffect, useRef, useState } from 'react'
import styles from './MetricsPreview.module.css'

const T = {
  ru: {
    tag:    'Дашборд',
    h2:     'Всё на одном экране',
    sub:    'Актуальные показатели устройства, журнал событий и сравнение устройств — без переключений.',
    window: 'CryptoTerm — Antminer S19 Pro',
    chips: [
      { label: 'Хэшрейт',       value: '110.4', unit: 'TH/s', delta: '+2.1% за час',   up: true,  color: 'var(--accent3)' },
      { label: 'Температура',   value: '71',    unit: '°C',   delta: 'стабильно',       up: null,  color: '#fb923c' },
      { label: 'Потребление',   value: '3 250', unit: 'Вт',   delta: '-1.4% за сутки', up: false, color: '#60a5fa' },
      { label: 'Прибыль / сут', value: '$8.42', unit: '',     delta: 'BTC $67 400',     up: true,  color: 'var(--green)' },
    ],
    logs: [
      { time: '14:22:01', type: 'OK',   parts: ['Antminer-S19-01  hashrate=', '110.4 TH/s', '  temp=', '71 °C'] },
      { time: '14:22:05', type: 'MQTT', parts: ['Метрики device_a3f9  fan=', '5100 rpm', ''] },
      { time: '14:22:10', type: 'CMD',  parts: ['Команда set_mode=STANDARD  статус=', 'SUCCESS', ''] },
      { time: '14:22:15', type: 'WARN', parts: ['Antminer-S19-02  temp=', '78 °C', ' — превышение порога'] },
    ],
    bars: [
      { label: 'Antminer S19 Pro', pct: 90, val: '110.4 TH/s', color: 'linear-gradient(90deg,var(--accent),#6d28d9)' },
      { label: 'Antminer S19j',    pct: 78, val: '88.6 TH/s',  color: 'linear-gradient(90deg,var(--teal),#0d9488)' },
      { label: 'Antminer S17+',    pct: 52, val: '62.3 TH/s',  color: 'linear-gradient(90deg,#3b82f6,#60a5fa)' },
    ],
  },
  en: {
    tag:    'Dashboard',
    h2:     'Everything on one screen',
    sub:    'Current device metrics, event log, and device comparison — no tab switching required.',
    window: 'CryptoTerm — Antminer S19 Pro',
    chips: [
      { label: 'Hashrate',       value: '110.4', unit: 'TH/s', delta: '+2.1% this hour',  up: true,  color: 'var(--accent3)' },
      { label: 'Temperature',   value: '71',    unit: '°C',   delta: 'stable',            up: null,  color: '#fb923c' },
      { label: 'Power draw',    value: '3,250', unit: 'W',    delta: '-1.4% today',       up: false, color: '#60a5fa' },
      { label: 'Profit / day',  value: '$8.42', unit: '',     delta: 'BTC $67,400',       up: true,  color: 'var(--green)' },
    ],
    logs: [
      { time: '14:22:01', type: 'OK',   parts: ['Antminer-S19-01  hashrate=', '110.4 TH/s', '  temp=', '71 °C'] },
      { time: '14:22:05', type: 'MQTT', parts: ['Metrics device_a3f9  fan=', '5100 rpm', ''] },
      { time: '14:22:10', type: 'CMD',  parts: ['Command set_mode=STANDARD  status=', 'SUCCESS', ''] },
      { time: '14:22:15', type: 'WARN', parts: ['Antminer-S19-02  temp=', '78 °C', ' — threshold exceeded'] },
    ],
    bars: [
      { label: 'Antminer S19 Pro', pct: 90, val: '110.4 TH/s', color: 'linear-gradient(90deg,var(--accent),#6d28d9)' },
      { label: 'Antminer S19j',    pct: 78, val: '88.6 TH/s',  color: 'linear-gradient(90deg,var(--teal),#0d9488)' },
      { label: 'Antminer S17+',    pct: 52, val: '62.3 TH/s',  color: 'linear-gradient(90deg,#3b82f6,#60a5fa)' },
    ],
  },
}

const typeColor = { OK: 'var(--green)', MQTT: 'var(--teal)', CMD: 'var(--accent3)', WARN: '#fb923c' }

export default function MetricsPreview({ lang }) {
  const t = T[lang]
  const barsRef = useRef(null)
  const [animated, setAnimated] = useState(false)

  useEffect(() => {
    setAnimated(false)
    const timer = setTimeout(() => setAnimated(true), 200)
    return () => clearTimeout(timer)
  }, [lang])

  useEffect(() => {
    const el = barsRef.current
    if (!el) return
    const obs = new IntersectionObserver(([e]) => {
      if (e.isIntersecting) { setAnimated(true); obs.disconnect() }
    }, { threshold: 0.3 })
    obs.observe(el)
    return () => obs.disconnect()
  }, [])

  return (
    <section id="dashboard">
      <div className="container">
        <div className="section-header">
          <div className="section-tag">{t.tag}</div>
          <h2>{t.h2}</h2>
          <p>{t.sub}</p>
        </div>

        <div className={styles.window}>
          {/* Titlebar */}
          <div className={styles.titlebar}>
            <div className={styles.wdots}>
              <span className={styles.dRed} />
              <span className={styles.dYellow} />
              <span className={styles.dGreen} />
            </div>
            <span className={styles.wtitle}>{t.window}</span>
          </div>

          <div className={styles.body}>
            {/* Metric chips */}
            <div className={styles.chips}>
              {t.chips.map(c => (
                <div key={c.label} className={styles.chip}>
                  <div className={styles.chipLabel}>{c.label}</div>
                  <div className={styles.chipValue} style={{ color: c.color }}>
                    {c.value}<span className={styles.chipUnit}> {c.unit}</span>
                  </div>
                  <div className={`${styles.chipDelta} ${c.up === true ? styles.deltaUp : c.up === false ? styles.deltaDown : styles.deltaFlat}`}>
                    {c.up === true ? '+' : c.up === false ? '-' : ''} {c.delta}
                  </div>
                </div>
              ))}
            </div>

            {/* Log */}
            <div className={styles.terminal}>
              {t.logs.map((l, i) => (
                <div key={i} className={styles.logLine}>
                  <span className={styles.logTime}>{l.time}</span>
                  <span className={styles.logType} style={{ color: typeColor[l.type] }}>[{l.type}]</span>
                  <span className={styles.logMsg}>
                    {l.parts[0]}
                    {l.parts[1] && <span className={styles.logVal}>{l.parts[1]}</span>}
                    {l.parts[2]}
                    {l.parts[3] && <span className={styles.logVal}>{l.parts[3]}</span>}
                  </span>
                </div>
              ))}
            </div>

            {/* Bars */}
            <div ref={barsRef} className={styles.bars}>
              {t.bars.map(b => (
                <div key={b.label} className={styles.barRow}>
                  <span className={styles.barLabel}>{b.label}</span>
                  <div className={styles.barTrack}>
                    <div
                      className={styles.barFill}
                      style={{ width: animated ? `${b.pct}%` : '0%', background: b.color, transition: 'width 1.1s cubic-bezier(.4,0,.2,1)' }}
                    />
                  </div>
                  <span className={styles.barVal}>{b.val}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
