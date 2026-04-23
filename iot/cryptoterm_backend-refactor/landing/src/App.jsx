import { useState } from 'react'
import Nav from './components/Nav'
import Hero from './components/Hero'
import Screenshots from './components/Screenshots'
import Features from './components/Features'
import Support from './components/Support'
import Footer from './components/Footer'

export default function App() {
  const [lang, setLang] = useState('ru')

  return (
    <>
      <div className="bg-grid" />
      <div className="bg-radial" />
      <Nav lang={lang} setLang={setLang} />
      <main>
        <Hero lang={lang} />
        <Screenshots lang={lang} />
        <Features lang={lang} />
        <Support lang={lang} />
      </main>
      <Footer lang={lang} />
    </>
  )
}
