// src/auth.jsx
import jwtDecode from "jwt-decode";

// --- Константы для хранения ключей ---
const TOKEN_KEY = "ct_token"; 
const REFRESH_TOKEN_KEY = "ct_refreshToken";
const USER_ID_KEY = "ct_userId";

// --- Выбор хранилища: sessionStorage безопаснее (данные удаляются при закрытии вкладки) ---
// localStorage - данные сохраняются между сессиями (удобнее для пользователя)
// sessionStorage - данные удаляются при закрытии вкладки (безопаснее)
const USE_SESSION_STORAGE = false; // Измените на true для большей безопасности
const storage = USE_SESSION_STORAGE ? sessionStorage : localStorage;

// 🔒 Улучшенная безопасность: проверка токена при загрузке
const sanitizeToken = (token) => {
  if (!token || typeof token !== 'string') return null;
  
  // Базовая проверка формата JWT (3 части, разделенные точками)
  const parts = token.split('.');
  if (parts.length !== 3) return null;
  
  // Проверка на отсутствие потенциально опасных символов
  if (!/^[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+\.[A-Za-z0-9_-]+$/.test(token)) {
    return null;
  }
  
  return token;
};

// 🔒 Проверка срока действия токена
const isTokenExpired = (token) => {
  try {
    const decoded = jwtDecode(token);
    if (!decoded.exp) return false; // Если нет exp, считаем валидным
    
    const currentTime = Date.now() / 1000;
    // Добавляем буфер в 5 минут перед истечением
    const bufferSeconds = 300;
    return decoded.exp < (currentTime + bufferSeconds);
  } catch (e) {
    console.error('Failed to decode token for expiration check:', e);
    return true; // Если не удается декодировать, считаем истекшим
  }
};

export const auth = {
  
  // --- Сохранение данных с валидацией ---
  saveToken: (token) => {
    const sanitized = sanitizeToken(token);
    if (sanitized) {
      try {
        storage.setItem(TOKEN_KEY, sanitized);
      } catch (e) {
        console.error('Failed to save token:', e);
        // Возможные причины: квота хранилища превышена, режим приватного просмотра
      }
    } else {
      console.error('Invalid token format');
    }
  },
  
  saveRefreshToken: (refreshToken) => {
    const sanitized = sanitizeToken(refreshToken);
    if (sanitized) {
      try {
        storage.setItem(REFRESH_TOKEN_KEY, sanitized);
      } catch (e) {
        console.error('Failed to save refresh token:', e);
      }
    }
  },
  
  saveUserId: (userId) => {
    // Проверка на безопасность userId (должен быть числом или безопасной строкой)
    if (userId && (typeof userId === 'number' || /^[a-zA-Z0-9-_]+$/.test(userId))) {
      try {
        storage.setItem(USER_ID_KEY, userId.toString());
      } catch (e) {
        console.error('Failed to save userId:', e);
      }
    }
  },
  
  // --- Получение данных с проверкой ---
  getToken: () => {
    try {
      const token = storage.getItem(TOKEN_KEY);
      if (!token) return null;
      
      const sanitized = sanitizeToken(token);
      if (!sanitized) {
        console.error('Token failed sanitization');
        auth.removeToken();
        return null;
      }
      
      // Проверяем срок действия
      if (isTokenExpired(sanitized)) {
        console.log('Access token expired, attempting refresh...');
        // Не удаляем токен сразу, попытаемся обновить через refresh token
        return null;
      }
      
      return sanitized;
    } catch (e) {
      console.error('Failed to get token:', e);
      return null;
    }
  },
  
  getRefreshToken: () => {
    try {
      const refreshToken = storage.getItem(REFRESH_TOKEN_KEY);
      if (!refreshToken) return null;
      
      const sanitized = sanitizeToken(refreshToken);
      if (!sanitized) {
        console.error('Refresh token failed sanitization');
        storage.removeItem(REFRESH_TOKEN_KEY);
        return null;
      }
      
      // Для refresh токена НЕ используем буфер
      try {
        const decoded = jwtDecode(sanitized);
        if (decoded.exp) {
          const currentTime = Date.now() / 1000;
          if (decoded.exp < currentTime) {
            console.log('Refresh token expired');
            storage.removeItem(REFRESH_TOKEN_KEY);
            return null;
          }
        }
      } catch (e) {
        console.error('Failed to decode refresh token:', e);
        storage.removeItem(REFRESH_TOKEN_KEY);
        return null;
      }
      
      return sanitized;
    } catch (e) {
      console.error('Failed to get refresh token:', e);
      return null;
    }
  },
  
  getUserId: () => {
    try {
      const userId = storage.getItem(USER_ID_KEY);
      // Проверка безопасности
      if (userId && /^[a-zA-Z0-9-_]+$/.test(userId)) {
        return userId;
      }
      return null;
    } catch (e) {
      console.error('Failed to get userId:', e);
      return null;
    }
  },
  
  // --- Удаление данных при выходе ---
  removeToken: () => {
    try {
      storage.removeItem(TOKEN_KEY);
      storage.removeItem(REFRESH_TOKEN_KEY);
      storage.removeItem(USER_ID_KEY);
    } catch (e) {
      console.error('Failed to remove tokens:', e);
    }
  },
  
  // --- Проверка авторизации ---
  isAuthenticated: () => {
    const token = auth.getToken();
    return token !== null;
  },
  
  // --- Декодирование токена ---
  decodeUserIdFromToken: () => {
    try {
      const token = auth.getToken(); // Используем безопасный метод
      if (!token) return null;
      
      const decoded = jwtDecode(token);
      return decoded.sub || decoded.id || decoded.userId;
    } catch (e) { 
      console.error('Failed to decode token:', e);
      return null; 
    }
  },
  
  // --- Получение username из токена ---
  getUsernameFromToken: () => {
    try {
      const token = auth.getToken();
      if (!token) return null;
      
      const decoded = jwtDecode(token);
      return decoded.username || decoded.name || null;
    } catch (e) {
      console.error('Failed to decode username from token:', e);
      return null;
    }
  },
  
  // --- Получение email из токена ---
  getEmailFromToken: () => {
    try {
      const token = auth.getToken();
      if (!token) return null;
      
      const decoded = jwtDecode(token);
      return decoded.email || null;
    } catch (e) {
      console.error('Failed to decode email from token:', e);
      return null;
    }
  },
  
  // --- Проверка роли администратора ---
  isAdmin: () => {
    try {
      const token = auth.getToken();
      if (!token) return false;
      
      const decoded = jwtDecode(token);
      const roles = decoded.roles || decoded.authorities || [];
      
      // Проверяем наличие роли ADMIN или ROLE_ADMIN
      return roles.some(role => 
        role === 'ADMIN' || 
        role === 'ROLE_ADMIN' || 
        role.authority === 'ROLE_ADMIN'
      );
    } catch (e) {
      console.error('Failed to check admin role:', e);
      return false;
    }
  }
};
