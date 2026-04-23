# Быстрый старт: Управление ASIC командами на фронтенде

## Что было добавлено

### 1. API функции в `src/api.js`

#### Основные методы:

```javascript
// Создать произвольную команду
api.createAsicCommand(command)

// Быстрая смена режима мощности
api.createPowerModeCommand(deviceId, asicIp, watts, password, asicPort, asicScheme)

// Получить команду по ID
api.getAsicCommand(token, cmdId)

// Получить все команды устройства
api.getAsicCommandsByDevice(token, deviceId)

// Отменить команду
api.cancelAsicCommand(token, cmdId)

// Повторить команду
api.resendAsicCommand(token, cmdId)
```

### 2. React компонент `AsicPowerControl.jsx`

Готовый компонент с UI для:
- Выбора режима мощности из 23 доступных профилей
- Отправки команды на ASIC
- Просмотра истории команд
- Отмены/повтора команд

### 3. Документация и примеры

- `ASIC_COMMANDS_GUIDE.md` - полное руководство
- `example_power_mode_config.json` - готовый конфиг для 3495W режима

## Простой пример использования

```javascript
import { api } from './api';

// Изменить режим на 3495W (132 TH/s)
const result = await api.createPowerModeCommand(
  "rpi-001",          // Device ID
  "192.168.3.20",     // ASIC IP
  3495                // Watts
);

console.log('Command ID:', result.cmdId);
```

## Использование React компонента

```jsx
import AsicPowerControl from './components/AsicPowerControl';

function MyPage() {
  return (
    <AsicPowerControl 
      deviceId="rpi-001"
      asicIp="192.168.3.20"
      onCommandCreated={(cmd) => {
        console.log('Command created:', cmd.cmdId);
      }}
    />
  );
}
```

## Конфиг для одного режима (3495W / 132 TH/s)

```json
{
  "deviceId": "rpi-001",
  "asic": {
    "ip": "192.168.3.20",
    "port": 80,
    "scheme": "http",
    "firmware": "anthill"
  },
  "steps": [
    {
      "id": "unlock",
      "request": {
        "method": "POST",
        "path": "/api/v1/unlock",
        "headers": { "Content-Type": "application/json" },
        "body": { "pw": "admin" },
        "timeoutMs": 10000
      },
      "extract": { "token": "$.token" }
    },
    {
      "id": "set_profile_3495",
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

## Доступные режимы мощности

| Мощность | Хэшрейт | Режим |
|----------|---------|-------|
| 3495W | 132 TH/s | Экономичный |
| 4200W | 149 TH/s | Сбалансированный |
| 5150W | 177 TH/s | Производительный |
| 6000W | 199 TH/s | Очень высокий |
| 7000W | 220 TH/s | Экстремальный |
| 7700W | 250 TH/s | Максимум |

Полный список 23 режимов см. в `ASIC_COMMANDS_GUIDE.md`

## Тестирование

Сначала пересоберите и запустите бэкенд:

```bash
cd cryptoterm_backend
sudo ./rebuild-backend.sh
```

Затем проверьте работу:

```bash
./check-backend.sh
```

Создайте команду для устройства:

```bash
ASIC_IP=192.168.3.20 ./create-antminer-s19-profiles.sh rpi-001
```

## Структура команды

1. **unlock** - Авторизация, получение токена
2. **set_profile_XXXX** - Установка профиля мощности  
3. **restart_mining** - Перезапуск для применения

Токен из шага 1 автоматически подставляется в шаги 2-3 через `${token}`.

## Следующие шаги

1. Интегрируйте `AsicPowerControl` в Dashboard
2. Добавьте уведомления о статусе команд
3. Реализуйте автообновление статусов через WebSocket/polling
4. Добавьте валидацию IP адресов
5. Сохраняйте последние настройки в localStorage
