import Logo from './Logo'
import styles from './Footer.module.css'

const DOCS_URL = 'https://cryptoterm.ru/swagger-ui/index.html#/'

const T = {
  ru: {
    links: [
      { id: 'features', label: 'Возможности' },
      { id: 'support',  label: 'Поддержка' },
    ],
    docs: 'Документация',
    privacy: 'Конфиденциальность',
    copy: '2025 CryptoTerm',
  },
  en: {
    links: [
      { id: 'features', label: 'Features' },
      { id: 'support',  label: 'Support' },
    ],
    docs: 'API Docs',
    privacy: 'Privacy Policy',
    copy: '2025 CryptoTerm',
  },
}

function scrollTo(id) {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

export default function Footer({ lang }) {
  const t = T[lang]
  return (
    <footer className={styles.footer}>
      <div className={`container ${styles.inner}`}>
        <div className={styles.brand}>
          <Logo size={28} />
          CryptoTerm
        </div>

        <nav className={styles.links}>
          {t.links.map(l => (
            <button key={l.id} className={styles.link} onClick={() => scrollTo(l.id)}>
              {l.label}
            </button>
          ))}
          <a href={DOCS_URL} target="_blank" rel="noopener noreferrer" className={styles.link}>
            {t.docs}
          </a>
          <a href="/privacy-policy/" className={styles.link}>
            {t.privacy}
          </a>
        </nav>

        <div className={styles.copy}>© {t.copy}</div>
      </div>
    </footer>
  )
}
