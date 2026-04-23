# Frontend Implementation Notes

**Дата**: 23 января 2026  
**Версия**: 2.0.0

---

## Реализованный функционал

### ✅ 1. Новый Dashboard UI

**Компоненты**:
- `Dashboard.jsx` - Главный компонент дашборда
- `HashRateChart.jsx` - График хэшрейта с фильтрами (Day, Week, Month, Year)
- `MinerCard.jsx` - Карточка майнера с профилями (ECONOMY, STANDARD, MAXIMAL)
- `MinersTable.jsx` - Таблица майнеров с статусами
- `ProfitCalculator.jsx` - Калькулятор прибыли

### ✅ 2. Username из JWT токена

```javascript
import jwtDecode from 'jwt-decode';

const token = localStorage.getItem('accessToken');
const decoded = jwtDecode(token);
console.log(decoded.username); // "Matvei"
```

### ✅ 3. Профили майнеров

**API Integration**:
- `GET /api/v1/asic-command-templates/model/{model}` - Получить профили
- `POST /api/v1/miners/{id}/execute-template/{name}` - Применить профиль

**Категории**:
- ECONOMY - Низкое энергопотребление (3495W - 4400W)
- STANDARD - Сбалансированные (4650W - 6400W)
- MAXIMAL - Максимальная производительность (6700W - 7700W)

---

## Структура проекта

```
cryptoterm_frontend/
├── src/
│   ├── components/
│   │   ├── Dashboard.jsx          ✅ NEW
│   │   ├── HashRateChart.jsx      ✅ NEW
│   │   ├── MinerCard.jsx          ✅ NEW
│   │   ├── MinersTable.jsx        ✅ NEW
│   │   ├── ProfitCalculator.jsx   ✅ NEW
│   │   ├── Home.jsx               ✅ UPDATED
│   │   ├── Login.jsx
│   │   └── Signup.jsx
│   ├── dashboard.css              ✅ NEW
│   ├── styles.css
│   ├── App.jsx
│   └── main.jsx
└── package.json
```

---

## Установка и запуск

### 1. Установка зависимостей

```bash
cd /Users/kirilllesniak/projects/cryptoterm_frontend
npm install
```

### 2. Запуск dev сервера

```bash
npm run dev
```

Приложение будет доступно по адресу: `http://localhost:5173`

### 3. Сборка для production

```bash
npm run build
```

---

## Дизайн согласно эскизам

### Экран 1: Hardware View

**Элементы**:
- Заголовок "Hey, Matvei!"
- График Hash rate с датой 28.11.2025
- Фильтры времени: Day, Week, Month, Year
- Список майнеров (P99)
- При раскрытии:
  - Категории профилей: ECONOMY, STANDARD, MAXIMAL
  - Consumption: ~ 999 Kw/h
  - Hash rate: ~ 999 Th/h
  - Кнопки: Start, Schedule, Delete

**Компоненты**:
```jsx
<Dashboard>
  <HashRateChart />
  <MinerCard miner={miner} />
</Dashboard>
```

### Экран 2: Mining View (Table)

**Элементы**:
- Тот же заголовок и график
- Таблица майнеров:
  - Name (с checkbox)
  - Th/r (Terahash rate)
  - kW (Киловатты)
  - t°C (Температура)
  - Status (индикатор)
- Статусы:
  - Зеленый: < 90°C
  - Желтый: 90-150°C
  - Красный: > 150°C

**Компоненты**:
```jsx
<Dashboard>
  <HashRateChart />
  <MinersTable miners={miners} />
</Dashboard>
```

### Экран 3: Profit Calculator

**Элементы**:
- График Hash rate
- Секция "Calculate profit"
- Выбор криптовалюты (Bitcoin)
- Поле "Price per kW/h"
- Фильтры: Hour, Day, Week
- Результаты:
  - Exchange rate
  - Without expenses (зеленый)
  - With expenses (зеленый)

**Компоненты**:
```jsx
<Dashboard>
  <HashRateChart />
  <ProfitCalculator />
</Dashboard>
```

---

## Цветовая схема

```css
/* Background */
--bg-primary: #000;
--bg-secondary: #1a1a1a;
--bg-tertiary: #0a0a0a;

/* Accent */
--accent-primary: #5865f2;
--accent-hover: #6b76fa;

/* Text */
--text-primary: #fff;
--text-secondary: #999;
--text-tertiary: #666;

/* Status */
--status-active: #4caf50;
--status-warning: #ff9800;
--status-error: #f44336;

/* Borders */
--border-primary: #2a2a2a;
--border-secondary: #333;
```

---

## Компоненты детально

### Dashboard.jsx

**State**:
- `username` - из JWT токена
- `selectedView` - 'hardware' | 'table' | 'profit'
- `miners` - список майнеров
- `selectedMiner` - ID развернутого майнера

**Методы**:
- `loadMiners()` - Загрузка списка майнеров
- `handleLogout()` - Выход из системы

### HashRateChart.jsx

**Props**: Нет

**State**:
- `timeRange` - 'Day' | 'Week' | 'Month' | 'Year'
- `chartData` - Данные для графика
- `currentDate` - Текущая дата

**Features**:
- Динамическая генерация данных
- Recharts для отрисовки
- Responsive дизайн

### MinerCard.jsx

**Props**:
- `miner` - Объект майнера
- `isExpanded` - Развернута ли карточка
- `onToggle` - Callback при клике
- `onProfileApply` - Callback после применения профиля

**State**:
- `profiles` - Список профилей для модели
- `selectedCategory` - ECONOMY | STANDARD | MAXIMAL
- `selectedProfile` - Выбранный профиль
- `loading` - Состояние загрузки

**Методы**:
- `loadProfiles()` - Загрузка профилей из API
- `selectProfileForCategory()` - Выбор профиля по категории
- `handleApply()` - Применение профиля

### MinersTable.jsx

**Props**:
- `miners` - Массив майнеров

**Features**:
- Таблица с сортировкой
- Чекбоксы для выбора
- Цветные индикаторы статуса
- Responsive

### ProfitCalculator.jsx

**State**:
- `cryptocurrency` - Bitcoin | Ethereum | Litecoin
- `pricePerKwh` - Цена за кВт/ч
- `timeRange` - Hour | Day | Week
- `exchangeRate` - Курс
- `withoutExpenses` - Прибыль без расходов
- `withExpenses` - Прибыль с расходами

**Методы**:
- `calculateProfit()` - Расчет прибыли

---

## API Endpoints используемые

### Получение майнеров
```
GET /api/v1/miners
Authorization: Bearer {accessToken}
```

### Получение профилей для модели
```
GET /api/v1/asic-command-templates/model/{minerModel}
Authorization: Bearer {accessToken}
```

### Применение профиля
```
POST /api/v1/miners/{minerId}/execute-template/{templateName}
Authorization: Bearer {accessToken}
Content-Type: application/json

{
  "minerIp": "192.168.1.100"
}
```

---

## Особенности реализации

### 1. JWT Decode

Используется `jwt-decode` версии 3.x:
```javascript
import jwtDecode from 'jwt-decode'; // v3
// НЕ: import { jwtDecode } from 'jwt-decode'; // v4
```

### 2. Recharts для графиков

```javascript
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from 'recharts';
```

### 3. Адаптивность

Медиа-запросы для мобильных устройств:
```css
@media (max-width: 768px) {
  .dashboard {
    padding: 16px 12px;
  }
}
```

### 4. Анимации

CSS transitions и animations:
```css
@keyframes slideDown {
  from { opacity: 0; max-height: 0; }
  to { opacity: 1; max-height: 500px; }
}
```

---

## Troubleshooting

### Ошибка: "jwtDecode is not a function"

**Решение**: Используйте `import jwtDecode from 'jwt-decode'` (без деструктуризации)

### Ошибка: "Failed to load miners"

**Проверьте**:
1. Backend запущен
2. Токен валиден в localStorage
3. CORS настроен правильно

### Профили не загружаются

**Проверьте**:
1. Модель майнера корректна
2. Профили созданы в базе (см. `create-antminer-s19-profiles.sh`)
3. API endpoint `/api/v1/asic-command-templates/model/{model}` доступен

---

## TODO (опционально)

- [ ] WebSocket для real-time обновлений
- [ ] Детальная страница майнера
- [ ] История команд
- [ ] Расписание профилей
- [ ] Экспорт метрик в CSV
- [ ] Dark/Light theme toggle
- [ ] Мультиязычность (i18n)

---

## Версии зависимостей

```json
{
  "react": "^18.2.0",
  "react-dom": "^18.2.0",
  "react-router-dom": "^7.9.6",
  "recharts": "^2.8.0",
  "jwt-decode": "^3.1.2"
}
```

---

**Автор**: AI Assistant  
**Дата**: 23 января 2026  
**Версия**: 2.0.0
