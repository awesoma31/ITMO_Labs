# Интеграция фронтенда с бэкендом - Сводка изменений

## Общее описание

Фронтенд приложение полностью интегрировано с бэкендом API. Все моки заменены на реальные данные из API.

## Backend изменения

### Новые DTO

1. **MinerDto.java** - DTO для представления майнера
   - id: UUID
   - label: String
   - vendor: String
   - model: String
   - mode: String (ECO, STANDARD, OVERCLOCK)

2. **DeviceMinersDto.java** (обновлен) - DTO для устройства с майнерами
   - id: UUID
   - name: String
   - description: String (nullable)
   - ipAddress: String
   - registeredAt: OffsetDateTime
   - miners: List<MinerDto>

### Обновленные сервисы

1. **UserDevicesService.java**
   - Метод `getUserDevicesWithMiners()` теперь возвращает полную информацию о майнерах, а не только их ID

## Frontend изменения

### API модуль (src/api.js)

Добавлены новые методы:

1. **Устройства**
   - `getUserDevices(token, userId)` - получение устройств с майнерами
   - `getUserDevicesDetailed(token, userId)` - детальная информация об устройствах
   - `getDeviceById(token, deviceId)` - информация о конкретном устройстве

2. **Метрики хэшрейта**
   - `getHashRateByDevice(token, deviceId, from, to, bucket)` - хэшрейт устройства
   - `getHashRateByUser(token, userId, from, to, bucket)` - хэшрейт пользователя

3. **Метрики температуры**
   - `getTemperatureByDevice(token, deviceId, from, to, bucket)` - температура устройства
   - `getTemperatureByUser(token, userId, from, to, bucket)` - температура пользователя

4. **Другие метрики**
   - `getOtherMetricByDevice(token, metricName, deviceId, from, to, bucket)` - универсальный метод

5. **Логи**
   - `getDeviceLogs(token, deviceId, from, to)` - логи устройства

6. **Управление**
   - `setMinerMode(token, minerId, mode)` - установка режима работы (ECO, STANDARD, OVERCLOCK)
   - `restartMiner(token, minerId)` - перезагрузка майнера

7. **Калькулятор прибыли**
   - `getMiningProfit(token, userId, from, to)` - расчет прибыли

### Компоненты

#### 1. Dashboard.jsx
- Загружает реальные устройства через API
- Извлекает userId из токена
- Передает данные в дочерние компоненты
- Обрабатывает состояния загрузки и ошибок

#### 2. HashRateChart.jsx
- Загружает реальные метрики хэшрейта через API
- Поддерживает временные диапазоны: Day, Week, Month, Year
- Автоматически выбирает оптимальный bucket для агрегации
- Показывает состояние загрузки и ошибки
- Добавлен кастомный Tooltip для отображения значений

#### 3. ProfitCalculator.jsx
- Загружает данные о прибыли через API
- Отображает реальный хэшрейт и потребление энергии
- Рассчитывает прибыль с учетом затрат на электричество
- Поддерживает временные диапазоны: Hour, Day, Week

#### 4. MinerCard.jsx
- Отображает информацию об устройстве и его майнерах
- Позволяет менять режим работы майнера (ECO, STANDARD, OVERCLOCK)
- Позволяет перезагружать майнер
- Показывает текущий режим работы
- Обрабатывает ошибки и состояние загрузки

#### 5. MinersTable.jsx
- Отображает таблицу устройств и майнеров
- Загружает реальные метрики (хэшрейт и температура) для каждого устройства
- Группирует майнеры по устройствам
- Показывает текущий режим работы с цветовой индикацией:
  - ECO - зеленый
  - STANDARD - синий
  - OVERCLOCK - красный
- Индикация состояния по температуре

### Auth модуль (src/auth.jsx)

Добавлена поддержка refresh токена:
- `saveRefreshToken(refreshToken)` - сохранение refresh токена
- `getRefreshToken()` - получение refresh токена
- `removeToken()` - удаление всех токенов включая refresh

## Endpoints используемые фронтендом

### Аутентификация
- POST `/api/auth/register` - регистрация
- POST `/api/auth/login` - вход
- POST `/api/auth/refresh` - обновление токена

### Устройства
- GET `/api/users/{userId}/devices` - список устройств с майнерами
- GET `/api/users/{userId}/devices/detailed` - детальная информация
- GET `/api/users/devices/{deviceId}` - информация о конкретном устройстве

### Метрики
- GET `/api/metrics/hashrate/device/{deviceId}` - хэшрейт устройства
- GET `/api/metrics/hashrate/user/{userId}` - хэшрейт пользователя
- GET `/api/metrics/temperature/device/{deviceId}` - температура устройства
- GET `/api/metrics/temperature/user/{userId}` - температура пользователя
- GET `/api/metrics/other/{metricName}/device/{deviceId}` - другие метрики

### Логи
- GET `/api/logs/device/{deviceId}` - логи устройства

### Управление
- POST `/api/control/miner/{minerId}/mode` - установка режима работы
- POST `/api/control/miner/{minerId}/restart` - перезагрузка майнера

### Калькулятор
- GET `/api/calculator/getProfit/{userId}` - расчет прибыли

## Структура данных

### Device (из API)
```json
{
  "id": "uuid",
  "name": "string",
  "description": "string",
  "ipAddress": "string",
  "registeredAt": "2025-01-23T10:00:00Z",
  "miners": [...]
}
```

### Miner (из API)
```json
{
  "id": "uuid",
  "label": "string",
  "vendor": "string",
  "model": "string",
  "mode": "STANDARD"
}
```

### Metric Point
```json
{
  "timestamp": "2025-01-23T10:00:00Z",
  "avgValue": 123.45,
  "minValue": 120.0,
  "maxValue": 125.0
}
```

## Что осталось сделать (опционально)

1. Добавить WebSocket подключение для real-time обновлений метрик
2. Добавить графики для других метрик (power consumption, fan speed и т.д.)
3. Добавить фильтрацию и поиск устройств/майнеров
4. Добавить экспорт данных (CSV, JSON)
5. Добавить уведомления о критических событиях (перегрев, отключение и т.д.)
6. Добавить историю команд управления
7. Добавить групповое управление майнерами

## Тестирование

Для тестирования приложения:

1. Убедитесь, что бэкенд запущен на `http://localhost:8080`
2. Запустите фронтенд: `npm run dev`
3. Зарегистрируйтесь или войдите в систему
4. Проверьте отображение устройств и майнеров
5. Проверьте графики метрик
6. Проверьте калькулятор прибыли
7. Попробуйте изменить режим работы майнера
8. Проверьте таблицу майнеров

## Примечания

- Все временные метки используют ISO 8601 формат с UTC timezone
- Для агрегации метрик используется автоматический bucket в зависимости от временного диапазона
- Все API вызовы требуют JWT токен в заголовке Authorization
- Refresh токен сохраняется в localStorage для автоматического обновления access токена
