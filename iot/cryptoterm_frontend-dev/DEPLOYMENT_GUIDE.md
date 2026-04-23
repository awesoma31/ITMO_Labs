# 🚀 Руководство по запуску

## ⚠️ Важная проблема, которую вы видите

**Ошибка:**
```
Access to fetch at 'http://localhost:8080/api/auth/login' from origin 'https://cryptoterm.duckdns.org' 
has been blocked by CORS policy
```

**Причина:**
Вы пытаетесь обратиться с **HTTPS сайта** (`https://cryptoterm.duckdns.org`) к **HTTP API** (`http://localhost:8080`).

Браузер **блокирует** это по политике Mixed Content:
- ❌ HTTPS → HTTP = **заблокировано**
- ✅ HTTPS → HTTPS = **разрешено**
- ✅ HTTP → HTTP = **разрешено**

## 📋 Решения

### Вариант 1: Локальная разработка (рекомендуется)

Запускайте фронтенд локально для разработки:

```bash
cd /Users/kirilllesniak/projects/cryptoterm_frontend

# Убедитесь что .env.development существует
cat .env.development
# Должно быть: VITE_API_BASE_URL=http://localhost:8080/api

# Запустите dev server
npm run dev

# Откройте в браузере
# http://localhost:5173
```

**Важно:** Открывайте именно `http://localhost:5173`, НЕ `https://cryptoterm.duckdns.org`!

### Вариант 2: Production через Docker с nginx proxy

Для production разверните полный стек:

```bash
# 1. Соберите фронтенд
cd /Users/kirilllesniak/projects/cryptoterm_frontend
npm run build

# 2. Используйте .env.production
# VITE_API_BASE_URL=/api (относительный путь через nginx)

# 3. Запустите через Docker Compose
docker-compose up -d
```

В этом случае nginx проксирует `/api/*` → `backend:8080`, и все работает на одном домене.

### Вариант 3: Настройка HTTPS для бэкенда (для production)

Если нужно обращаться к бэкенду напрямую:

1. Настройте SSL сертификат для бэкенда
2. Бэкенд должен работать на HTTPS (порт 443)
3. Используйте: `VITE_API_BASE_URL=https://api.cryptoterm.duckdns.org`

## 🔧 Текущие файлы конфигурации

### `.env.development` (для локальной разработки)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

### `.env.production` (для production build)
```env
VITE_API_BASE_URL=/api
```

### `nginx.conf` (для production)
- Проксирует `/api/*` к `backend:8080`
- Добавлены Security Headers
- CORS настроен правильно

## 🎯 Какой вариант выбрать?

### Сейчас (разработка):
```bash
# 1. Запустите бэкенд локально
cd /Users/kirilllesniak/projects/cryptoterm_backend
./mvnw spring-boot:run

# 2. Запустите фронтенд локально
cd /Users/kirilllesniak/projects/cryptoterm_frontend
npm run dev

# 3. Откройте http://localhost:5173
```

### Production:
```bash
# Используйте Docker Compose с nginx
docker-compose up -d

# Весь стек (frontend + backend) будет на https://cryptoterm.duckdns.org
```

## 🔍 Проверка конфигурации

```bash
# 1. Проверьте какой .env используется
cd /Users/kirilllesniak/projects/cryptoterm_frontend
cat .env.development

# 2. Проверьте что бэкенд доступен
curl http://localhost:8080/actuator/health

# 3. Запустите фронтенд и проверьте console.log
# В браузере F12 → Console
# Должно быть: "API Base URL: http://localhost:8080/api"
```

## ❌ Типичные ошибки

### 1. Mixed Content (ваша текущая проблема)
**Проблема:** HTTPS → HTTP  
**Решение:** Используйте HTTP → HTTP (localhost) или HTTPS → HTTPS (production)

### 2. CORS ошибка
**Проблема:** Origin не разрешен  
**Решение:** SecurityConfig уже настроен правильно

### 3. 404 на API запросах
**Проблема:** Неправильный BASE_URL  
**Решение:** Проверьте .env файл и console.log

## 🔐 Безопасность

### Development (localhost)
- ✅ HTTP допустим
- ✅ CORS разрешен для localhost:5173
- ✅ Токены в localStorage/sessionStorage

### Production
- ✅ HTTPS обязателен
- ✅ Security Headers в nginx
- ✅ CSP настроен
- ⚠️ Рассмотрите HttpOnly cookies вместо localStorage

## 📞 Быстрая помощь

**Если не работает локально:**
```bash
# Проверьте порты
lsof -i :8080  # Бэкенд
lsof -i :5173  # Фронтенд

# Убедитесь что .env.development существует
ls -la /Users/kirilllesniak/projects/cryptoterm_frontend/.env*

# Перезапустите с чистого листа
cd /Users/kirilllesniak/projects/cryptoterm_frontend
rm -rf node_modules/.vite
npm run dev
```

**Если не работает на production:**
```bash
# Проверьте Docker контейнеры
docker-compose ps

# Проверьте логи
docker-compose logs frontend
docker-compose logs backend

# Пересоберите
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

**Текущий статус:** ✅ Код исправлен, готов к запуску  
**Дата:** 2026-01-23
