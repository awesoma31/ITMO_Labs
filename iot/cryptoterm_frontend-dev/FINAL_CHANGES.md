# Финальные изменения - Режимы ASIC и исправление токенов

## 1. ✅ Исправление проблемы с токенами

### Проблема
Пользователя выкидывало при входе на следующий день, хотя refresh токен живет 7 дней.

### Причина
Фронтенд проверял истечение access токена и сразу удалял его, не пытаясь обновить через refresh token.

### Решение

#### Frontend (auth.jsx):
1. **Добавлен буфер проверки токена** - токен считается истекшим за 5 минут до реального истечения
2. **Улучшена логика getToken()** - не удаляет токен сразу при истечении
3. **Улучшена проверка refresh токена** - без буфера для refresh токена

```javascript
const isTokenExpired = (token) => {
  const currentTime = Date.now() / 1000;
  const bufferSeconds = 300; // 5 минут
  return decoded.exp < (currentTime + bufferSeconds);
};
```

#### Frontend (api.js):
1. **Добавлен механизм автоматического обновления токена**:
   - `refreshAccessToken()` - функция для обновления токена
   - `fetchWithAuth()` - обертка для fetch с автоматическим обновлением
   - Предотвращение множественных одновременных запросов на refresh

2. **Логика работы**:
   - При 401 ошибке автоматически обновляет токен
   - Повторяет запрос с новым токеном
   - При неудаче перенаправляет на /login

```javascript
// Флаг для предотвращения множественных запросов
let isRefreshing = false;
let refreshPromise = null;

const fetchWithAuth = async (url, options) => {
  // Если токена нет, обновляем
  if (!token) {
    token = await refreshAccessToken();
  }
  
  // При 401 обновляем и повторяем
  if (response.status === 401) {
    token = await refreshAccessToken();
    return fetch(url, { ...options, headers: retryHeaders });
  }
};
```

### Результат
- ✅ Токен автоматически обновляется при истечении
- ✅ Пользователь остается авторизованным 7 дней
- ✅ Плавная работа без внезапных logout

## 2. ✅ Система режимов ASIC через шаблоны

### Архитектура

#### Backend:

1. **AsicMode.java** (enum):
   ```java
   public enum AsicMode {
       ECO,      // Экономичный режим
       STANDARD, // Стандартный режим  
       OVERCLOCK // Режим разгона
   }
   ```

2. **AsicCommandService.java** (обновлен):
   - Метод `setMinerMode(UUID minerId, AsicMode mode)`
   - Загружает шаблоны для модели майнера
   - Парсит названия: `3495W_132TH` → watts: 3495, hashrate: 132
   - Сортирует по мощности (watts)
   - Выбирает профиль по режиму:
     - **ECO** → первый профиль (минимальная мощность)
     - **STANDARD** → средний профиль (size / 2)
     - **OVERCLOCK** → последний минус 2 (Math.max(0, size - 3))
   - Обновляет режим в БД
   - Отправляет команду через MQTT

3. **ControlController.java** (обновлен):
   - ModeRequest теперь принимает `AsicMode mode` (enum)
   - Использует `asicCommandService.setMinerMode()`
   - Проверка прав доступа (owner)

4. **CommandPublisher.java** (обновлен):
   - Добавлен метод `sendAsicTemplateCommand()`
   - Отправляет команду с шаблоном на RP через MQTT

### Алгоритм работы

```
1. Фронтенд → POST /api/control/miner/{minerId}/mode
   body: { "mode": "ECO" }

2. Backend:
   - Проверяет права доступа
   - Загружает шаблоны для модели майнера
   - Парсит: 3495W_132TH, 3635W_136TH, 4200W_149TH, ...
   - Сортирует по watts: [3495, 3635, 4200, ...]
   - Выбирает:
     * ECO → 3495W_132TH (первый)
     * STANDARD → средний (size/2)
     * OVERCLOCK → size-3 (но не последний)
   
3. Обновляет miner.mode в PostgreSQL

4. Отправляет команду на RP через MQTT:
   topic: device/{deviceId}/command
   payload: {
     "deviceId": "...",
     "command": "asic_template",
     "minerId": "...",
     "templateName": "3495W_132TH",
     "steps": [...],
     "policy": {...}
   }

5. RP выполняет шаблон команд на ASIC
```

### Формат шаблонов

Название шаблона: `{watts}W_{hashrate}TH`

Примеры:
- `3495W_132TH` - 3495 Ватт, 132 TH/s
- `4200W_149TH` - 4200 Ватт, 149 TH/s
- `7700W_250TH` - 7700 Ватт, 250 TH/s

### Скрипт создания профилей

`create-antminer-s19-profiles.sh`:
- Создает 23 профиля для Antminer S19 Pro Hydro
- От 3495W (132TH) до 7700W (250TH)
- Использует admin JWT токен
- Сохраняет в MongoDB

**Использование:**
```bash
export ADMIN_TOKEN=your-jwt-token
./create-antminer-s19-profiles.sh
```

## 3. ✅ Дополнительные улучшения

### Username в токене
- Добавлены методы `getUsernameFromToken()`, `getEmailFromToken()`
- Dashboard показывает username или email вместо UUID

### Курс BTC/USD
- Реальный курс из blockchain.info через BtcNetworkService
- Отображается в калькуляторе прибыли

### График с множественными линиями
- Каждое устройство - своя линия и цвет
- Кликабельная легенда
- Переключение между Hash Rate и Temperature

### Панель логов
- Developer panel внизу Dashboard
- Выбор устройства и временного диапазона
- Цветовая индикация уровней (ERROR, WARN, INFO, DEBUG)

## Тестирование

### 1. Токены:
```bash
# Войдите в систему
# Закройте браузер
# Откройте на следующий день
# Должны остаться авторизованным
```

### 2. Режимы ASIC:
```bash
# Создайте профили
export ADMIN_TOKEN=your-jwt-token
./create-antminer-s19-profiles.sh

# На фронтенде:
# - Откройте устройство
# - Выберите ECO/STANDARD/OVERCLOCK
# - Нажмите Apply Mode
# - Проверьте логи MQTT
```

### 3. График температуры:
```bash
# На Dashboard:
# - Нажмите "🌡️ Temperature"
# - График должен показать температуру
# - Нажмите "📊 Hash Rate"
# - График должен показать хэшрейт
```

## Структура MQTT команды

```json
{
  "deviceId": "device-uuid",
  "command": "asic_template",
  "minerId": "miner-uuid",
  "templateName": "3495W_132TH",
  "steps": [
    {
      "id": "unlock",
      "request": {
        "method": "POST",
        "path": "/api/v1/unlock",
        "headers": {"Content-Type": "application/json"},
        "body": {"pw": "admin"},
        "timeoutMs": 10000
      },
      "extract": {"token": "$.token"}
    },
    {
      "id": "set_profile",
      "request": {
        "method": "POST",
        "path": "/api/v1/settings",
        "headers": {
          "Content-Type": "application/json",
          "Authorization": "Bearer ${token}"
        },
        "body": {
          "miner": {
            "overclock": {
              "modded_psu": false,
              "preset": "3495"
            }
          }
        },
        "timeoutMs": 15000
      }
    },
    {
      "id": "restart_mining",
      "request": {
        "method": "POST",
        "path": "/api/v1/mining/restart",
        "headers": {
          "Content-Type": "application/json",
          "Authorization": "Bearer ${token}"
        },
        "body": {},
        "timeoutMs": 10000
      }
    }
  ],
  "policy": {
    "maxRetries": 2,
    "retryDelayMs": 2000
  }
}
```

## API Endpoints

### Управление режимами
```
POST /api/control/miner/{minerId}/mode
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
  "mode": "ECO" | "STANDARD" | "OVERCLOCK"
}

Response:
{
  "status": "sent",
  "message": "Mode change command sent to miner",
  "mode": "ECO"
}
```

### Перезагрузка
```
POST /api/control/miner/{minerId}/restart
Authorization: Bearer {token}

Response:
{
  "status": "sent",
  "message": "Restart command sent to miner"
}
```

## Файлы изменены

### Backend:
1. `AsicMode.java` - новый enum
2. `AsicCommandService.java` - добавлен метод setMinerMode()
3. `ControlController.java` - обновлен для использования AsicMode enum
4. `CommandPublisher.java` - добавлен sendAsicTemplateCommand()
5. `MiningProfitResult.java` - добавлены btcPriceUsd, totalPowerConsumption
6. `MiningProfitService.java` - возвращает курс BTC

### Frontend:
1. `auth.jsx` - улучшена обработка токенов, добавлены методы username/email
2. `api.js` - добавлен fetchWithAuth с автоматическим refresh
3. `Dashboard.jsx` - добавлено переключение графиков, панель логов
4. `HashRateChart.jsx` - поддержка температуры, множественные линии
5. `MinerCard.jsx` - улучшена иконка restart, визуальная индикация выбора
6. `ProfitCalculator.jsx` - реальный курс BTC из API
7. `MinersTable.jsx` - реальные метрики, цветовая индикация режимов

## След��ющие шаги

1. Перезапустите backend
2. Создайте профили через скрипт
3. Протестируйте смену режимов
4. Проверьте автоматическое обновление токенов
