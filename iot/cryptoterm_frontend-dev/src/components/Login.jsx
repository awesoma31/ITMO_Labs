// src/components/Login.jsx
import { useState } from "react";
import { api } from "../api";
import { useNavigate, Link } from "react-router-dom";
import { auth } from "../auth";

export default function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(null);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    try {
      const res = await api.login(email, password);
      console.log('Login response:', res); // Для отладки
      
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
      console.error('Login error:', err);
      setError(err.message || "Ошибка входа");
    }
  };

  return (
    <div className="auth">
      <div className="auth-form">
        <h2>Hello!</h2>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit}>
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
          <button type="submit" style={{ width: '100%', marginTop: '16px' }}>
            Sign in
          </button>
        </form>
        <div style={{ marginTop: '20px', textAlign: 'center', color: '#999' }}>
          Don't have an account? <Link to="/signup">Sign up</Link>
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
