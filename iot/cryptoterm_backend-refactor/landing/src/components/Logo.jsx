export default function Logo({ size = 36 }) {
  return (
    <img
      src="/logo.png"
      alt="CryptoTerm"
      width={size}
      height={size}
      style={{ borderRadius: size * 0.22, display: 'block', objectFit: 'cover' }}
      draggable={false}
    />
  )
}
