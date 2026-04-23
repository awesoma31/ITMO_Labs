import { useState, useEffect, useCallback } from 'react'
import Logo from './Logo'
import styles from './Nav.module.css'

const SECTIONS = {
  ru: [
    { id: 'features',    label: 'Возможности' },
    { id: 'screenshots', label: 'Приложение' },
    { id: 'support',     label: 'Поддержка' },
  ],
  en: [
    { id: 'features',    label: 'Features' },
    { id: 'screenshots', label: 'App' },
    { id: 'support',     label: 'Support' },
  ],
}

function scrollTo(id) {
  const el = document.getElementById(id)
  if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

export default function Nav({ lang, setLang }) {
  const [scrolled, setScrolled] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)
  const sections = SECTIONS[lang]

  useEffect(() => {
    const fn = () => setScrolled(window.scrollY > 20)
    window.addEventListener('scroll', fn, { passive: true })
    return () => window.removeEventListener('scroll', fn)
  }, [])

  const toggleLang = useCallback((e) => {
    e.preventDefault()
    e.stopPropagation()
    setLang(l => l === 'ru' ? 'en' : 'ru')
  }, [setLang])

  const handleNavClick = useCallback((e, id) => {
    e.preventDefault()
    setMenuOpen(false)
    scrollTo(id)
  }, [])

  return (
    <nav className={`${styles.nav} ${scrolled ? styles.scrolled : ''}`}>
      <div className={`container ${styles.inner}`}>

        <button className={styles.logoBtn} onClick={() => window.scrollTo({ top: 0, behavior: 'smooth' })}>
          <Logo size={34} />
          <span className={styles.logoText}>CryptoTerm</span>
        </button>

        <ul className={styles.links}>
          {sections.map(s => (
            <li key={s.id}>
              <button className={styles.link} onClick={(e) => handleNavClick(e, s.id)}>
                {s.label}
              </button>
            </li>
          ))}
        </ul>

        <div className={styles.right}>
          <button className={styles.langBtn} onClick={toggleLang} type="button">
            <span className={lang === 'ru' ? styles.langActive : styles.langInactive}>RU</span>
            <span className={styles.langSep}>/</span>
            <span className={lang === 'en' ? styles.langActive : styles.langInactive}>EN</span>
          </button>
        </div>

        <button className={styles.burger} onClick={() => setMenuOpen(v => !v)} type="button" aria-label="Menu">
          <span className={`${styles.bl} ${menuOpen ? styles.blOpen1 : ''}`} />
          <span className={`${styles.bl} ${menuOpen ? styles.blOpen2 : ''}`} />
          <span className={`${styles.bl} ${menuOpen ? styles.blOpen3 : ''}`} />
        </button>
      </div>

      {menuOpen && (
        <div className={styles.mobile}>
          {sections.map(s => (
            <button key={s.id} className={styles.mobileLink} onClick={(e) => handleNavClick(e, s.id)}>
              {s.label}
            </button>
          ))}
          <div className={styles.mobileBottom}>
            <button className={styles.langBtn} onClick={toggleLang} type="button">
              <span className={lang === 'ru' ? styles.langActive : styles.langInactive}>RU</span>
              <span className={styles.langSep}>/</span>
              <span className={lang === 'en' ? styles.langActive : styles.langInactive}>EN</span>
            </button>
          </div>
        </div>
      )}
    </nav>
  )
}
