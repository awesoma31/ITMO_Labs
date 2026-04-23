// src/components/Signup.jsx
import { useState } from "react";
import { api } from "../api";
import { auth } from "../auth";
import { useNavigate, Link } from "react-router-dom";

export default function Signup() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [error, setError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const [showRepeatPassword, setShowRepeatPassword] = useState(false);
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    setError(null);

    if (password !== repeatPassword) {
      setError("Пароли не совпадают");
      return;
    }

    try {
      const res = await api.signup(email, password);
      console.log('Signup response:', res); // Для отладки
      
      // После конвертации через keysToCamel получаем camelCase
      if (res && res.accessToken && res.userId) {
        auth.saveToken(res.accessToken);
        auth.saveRefreshToken(res.refreshToken);
        auth.saveUserId(res.userId);
        navigate("/");
      } else {
        console.error('Invalid response format:', res);
        setError("Ошибка ответа сервера: отсутствует токен или userId");
      }
    } catch (err) {
      console.error('Signup error:', err);
      setError(err.message || "Ошибка регистрации");
    }
  };

  return (
    <div className="auth">
      <div className="auth-form">
        <h2>Hello!</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={submit}>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <div style={{ position: 'relative' }}>
            <input
              type={showPassword ? "text" : "password"}
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              style={{ paddingRight: '45px' }}
            />
            <button
              type="button"
              onClick={() => setShowPassword(!showPassword)}
              style={{
                position: 'absolute',
                right: '8px',
                top: '50%',
                transform: 'translateY(-50%)',
                background: 'transparent',
                padding: '8px',
                minHeight: 'auto',
                fontSize: '18px'
              }}
            >
              {showPassword ? '👁️' : '👁️‍🗨️'}
            </button>
          </div>
          <div style={{ position: 'relative' }}>
            <input
              type={showRepeatPassword ? "text" : "password"}
              placeholder="Repeat password"
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              required
              style={{ paddingRight: '45px' }}
            />
            <button
              type="button"
              onClick={() => setShowRepeatPassword(!showRepeatPassword)}
              style={{
                position: 'absolute',
                right: '8px',
                top: '50%',
                transform: 'translateY(-50%)',
                background: 'transparent',
                padding: '8px',
                minHeight: 'auto',
                fontSize: '18px'
              }}
            >
              {showRepeatPassword ? '👁️' : '👁️‍🗨️'}
            </button>
          </div>
          <button type="submit" style={{ width: '100%', marginTop: '16px' }}>
            Log In
          </button>
        </form>
        <div style={{ marginTop: '20px', textAlign: 'center', color: '#999' }}>
          Already have an account? <Link to="/login">Log in</Link>
        </div>
        <div style={{ marginTop: '20px', textAlign: 'center' }}>
          <Link to="/privacy" style={{ fontSize: '12px', color: '#7c6dd8' }}>
            Privacy policy
          </Link>
        </div>
      </div>
    </div>
  );
}
