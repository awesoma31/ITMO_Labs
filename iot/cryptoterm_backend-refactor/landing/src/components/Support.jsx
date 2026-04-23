import styles from './Support.module.css'

const DOCS_URL = 'https://cryptoterm.ru/swagger-ui/index.html#/'

const T = {
  ru: {
    tag:  'Поддержка',
    h2:   'Мы на связи',
    sub:  'Вопросы по подключению, настройке или работе приложения — напишите нам.',
    contacts: [
      {
        type:    'email',
        label:   'Почта',
        handle:  'kirill_lesnyak@mail.ru',
        href:    'mailto:kirill_lesnyak@mail.ru',
        note:    'Ответ в течение суток',
      },
      {
        type:    'tg',
        label:   'Telegram',
        handle:  '@cracycot',
        href:    'https://t.me/cracycot',
        note:    'Отвечаем быстро',
      },
    ],
    docs:    'Документация API',
    docsNote: 'Все эндпоинты и схемы данных',
  },
  en: {
    tag:  'Support',
    h2:   "We're here to help",
    sub:  'Questions about setup, connection, or app usage — just write to us.',
    contacts: [
      {
        type:    'email',
        label:   'Email',
        handle:  'kirill_lesnyak@mail.ru',
        href:    'mailto:kirill_lesnyak@mail.ru',
        note:    'Reply within 24 hours',
      },
      {
        type:    'tg',
        label:   'Telegram',
        handle:  '@cracycot',
        href:    'https://t.me/cracycot',
        note:    'Fast responses',
      },
    ],
    docs:    'API Documentation',
    docsNote: 'All endpoints and data schemas',
  },
}

const MailSvg = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="4" width="20" height="16" rx="2"/>
    <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/>
  </svg>
)

const TgSvg = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
    <path d="M21.5 4.5 2.5 11l7 2.5 2.5 7 4-5.5 5 3z"/>
    <path d="M9.5 13.5 15 8"/>
  </svg>
)

const DocsSvg = () => (
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
    <polyline points="14 2 14 8 20 8"/>
    <line x1="16" y1="13" x2="8" y2="13"/>
    <line x1="16" y1="17" x2="8" y2="17"/>
    <line x1="10" y1="9"  x2="8"  y2="9"/>
  </svg>
)

const ArrowSvg = () => (
  <svg viewBox="0 0 20 20" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" style={{ width: 16, height: 16, flexShrink: 0 }}>
    <path d="M4 10h12M11 5l5 5-5 5"/>
  </svg>
)

const iconMap = { email: MailSvg, tg: TgSvg }

export default function Support({ lang }) {
  const t = T[lang]
  return (
    <section id="support" className={styles.section}>
      <div className="container">
        <div className="section-header">
          <div className="section-tag">{t.tag}</div>
          <h2>{t.h2}</h2>
          <p>{t.sub}</p>
        </div>

        <div className={styles.layout}>
          {/* Contact cards */}
          <div className={styles.contacts}>
            {t.contacts.map(c => {
              const Icon = iconMap[c.type]
              return (
                <a
                  key={c.type}
                  href={c.href}
                  target={c.href.startsWith('http') ? '_blank' : undefined}
                  rel="noopener noreferrer"
                  className={`${styles.card} ${styles[`card_${c.type}`]}`}
                >
                  <div className={styles.cardTop}>
                    <div className={`${styles.iconBox} ${styles[`icon_${c.type}`]}`}>
                      <Icon />
                    </div>
                    <div className={styles.arrow}><ArrowSvg /></div>
                  </div>
                  <div className={styles.cardLabel}>{c.label}</div>
                  <div className={styles.cardHandle}>{c.handle}</div>
                  <div className={styles.cardNote}>
                    <span className={styles.noteDot} />
                    {c.note}
                  </div>
                </a>
              )
            })}
          </div>

          {/* Docs block */}
          <a
            href={DOCS_URL}
            target="_blank"
            rel="noopener noreferrer"
            className={styles.docsCard}
          >
            <div className={styles.docsLeft}>
              <div className={`${styles.iconBox} ${styles.icon_docs}`}>
                <DocsSvg />
              </div>
              <div>
                <div className={styles.docsTitle}>{t.docs}</div>
                <div className={styles.docsNote}>{t.docsNote}</div>
              </div>
            </div>
            <div className={styles.arrow}><ArrowSvg /></div>
          </a>
        </div>
      </div>
    </section>
  )
}
