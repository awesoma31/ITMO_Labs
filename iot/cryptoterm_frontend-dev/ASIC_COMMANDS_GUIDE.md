# Руководство по созданию команд для ASIC майнеров

## API функции

### 1. Создание произвольной команды

```javascript
import { api } from './api';

// Создать произвольную команду
const command = {
  deviceId: "rpi-001",
  asic: {
    ip: "192.168.3.20",
    port: 80,
    scheme: "http",
    firmware: "anthill"
  },
  steps: [
    {
      id: "unlock",
      request: {
        method: "POST",
        path: "/api/v1/unlock",
        headers: {
          "Content-Type": "application/json"
        },
        body: {
          pw: "admin"
        },
        timeoutMs: 10000
      },
      extract: {
        token: "$.token"
      }
    }
  ],
  policy: {
    maxRetries: 2,
    retryDelayMs: 2000
  }
};

const result = await api.createAsicCommand(command);
console.log('Command created:', result.cmdId);
```

### 2. Смена режима мощности (упрощенный метод)

```javascript
import { api } from './api';

// Сменить режим мощности на 3495W (132 TH/s)
try {
  const result = await api.createPowerModeCommand(
    "rpi-001",           // Device ID
    "192.168.3.20",      // ASIC IP
    3495,                // Watts
    "admin",             // Password (optional)
    80,                  // Port (optional)
    "http"               // Scheme (optional)
  );
  
  console.log('Power mode command created:', result.cmdId);
  console.log('Status:', result.status);
} catch (error) {
  console.error('Failed to create command:', error.message);
}
```

### 3. Получение команд устройства

```javascript
import { api } from './api';
import { auth } from './auth';

const token = auth.getToken();
const commands = await api.getAsicCommandsByDevice(token, "rpi-001");

commands.forEach(cmd => {
  console.log(`Command ${cmd.cmdId}: ${cmd.status}`);
});
```

### 4. Управление командами

```javascript
import { api } from './api';
import { auth } from './auth';

const token = auth.getToken();

// Получить статус команды
const command = await api.getAsicCommand(token, "cmd-123");

// Отменить команду
await api.cancelAsicCommand(token, "cmd-123");

// Повторно отправить команду
await api.resendAsicCommand(token, "cmd-123");
```

## Доступные режимы мощности для Antminer S19 Pro Hydro

| Мощность (W) | Хэшрейт (TH/s) | Профиль |
|--------------|----------------|---------|
| 3495 | 132 | 3495W_132TH |
| 3635 | 136 | 3635W_136TH |
| 3865 | 143 | 3865W_143TH |
| 4200 | 149 | 4200W_149TH |
| 4250 | 154 | 4250W_154TH |
| 4400 | 160 | 4400W_160TH |
| 4650 | 166 | 4650W_166TH |
| 4855 | 171 | 4855W_171TH |
| 5150 | 177 | 5150W_177TH |
| 5415 | 183 | 5415W_183TH |
| 5560 | 188 | 5560W_188TH |
| 5725 | 193 | 5725W_193TH |
| 6000 | 199 | 6000W_199TH |
| 6400 | 205 | 6400W_205TH |
| 6700 | 210 | 6700W_210TH |
| 6850 | 215 | 6850W_215TH |
| 7000 | 220 | 7000W_220TH |
| 7150 | 225 | 7150W_225TH |
| 7250 | 230 | 7250W_230TH |
| 7400 | 235 | 7400W_235TH |
| 7500 | 240 | 7500W_240TH |
| 7600 | 245 | 7600W_245TH |
| 7700 | 250 | 7700W_250TH |

## Пример React компонента

```jsx
import React, { useState } from 'react';
import { api } from '../api';

function PowerModeControl({ deviceId, asicIp }) {
  const [watts, setWatts] = useState(3495);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  const handleChangePowerMode = async () => {
    setLoading(true);
    setError(null);
    setResult(null);

    try {
      const response = await api.createPowerModeCommand(
        deviceId,
        asicIp,
        watts
      );
      setResult(response);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const powerModes = [
    { watts: 3495, hashrate: 132 },
    { watts: 3635, hashrate: 136 },
    { watts: 3865, hashrate: 143 },
    { watts: 4200, hashrate: 149 },
    { watts: 4250, hashrate: 154 },
    { watts: 4400, hashrate: 160 },
    { watts: 4650, hashrate: 166 },
    { watts: 4855, hashrate: 171 },
    { watts: 5150, hashrate: 177 },
    { watts: 5415, hashrate: 183 },
    { watts: 5560, hashrate: 188 },
    { watts: 5725, hashrate: 193 },
    { watts: 6000, hashrate: 199 },
    { watts: 6400, hashrate: 205 },
    { watts: 6700, hashrate: 210 },
    { watts: 6850, hashrate: 215 },
    { watts: 7000, hashrate: 220 },
    { watts: 7150, hashrate: 225 },
    { watts: 7250, hashrate: 230 },
    { watts: 7400, hashrate: 235 },
    { watts: 7500, hashrate: 240 },
    { watts: 7600, hashrate: 245 },
    { watts: 7700, hashrate: 250 },
  ];

  return (
    <div className="power-mode-control">
      <h3>Смена режима мощности</h3>
      
      <select 
        value={watts} 
        onChange={(e) => setWatts(Number(e.target.value))}
        disabled={loading}
      >
        {powerModes.map(mode => (
          <option key={mode.watts} value={mode.watts}>
            {mode.watts}W - {mode.hashrate} TH/s
          </option>
        ))}
      </select>

      <button 
        onClick={handleChangePowerMode}
        disabled={loading}
      >
        {loading ? 'Отправка...' : 'Применить режим'}
      </button>

      {result && (
        <div className="success">
          ✓ Команда создана: {result.cmdId}
          <br />
          Статус: {result.status}
        </div>
      )}

      {error && (
        <div className="error">
          ✗ Ошибка: {error}
        </div>
      )}
    </div>
  );
}

export default PowerModeControl;
```

## Структура конфига для одного режима (3495W)

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
        "headers": {
          "Content-Type": "application/json"
        },
        "body": {
          "pw": "admin"
        },
        "timeoutMs": 10000
      },
      "extract": {
        "token": "$.token"
      }
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

## Как это работает

1. **Шаг 1: Unlock** - Авторизация на ASIC с паролем, получение токена
2. **Шаг 2: Set Profile** - Установка профиля мощности (используется токен из шага 1)
3. **Шаг 3: Restart Mining** - Перезапуск майнинга для применения изменений

## Переменные

В шагах можно использовать переменные, извлеченные из предыдущих шагов:
- `${token}` - токен из шага "unlock"
- Формат JSONPath для извлечения: `$.token` означает взять поле "token" из JSON ответа

## Политика повторов

```javascript
policy: {
  maxRetries: 2,           // Максимум 2 повтора при ошибке
  retryDelayMs: 2000       // Задержка 2 секунды между повторами
}
```

## Статусы команд

- `PENDING` - Команда создана, ожидает отправки
- `SENT` - Команда отправлена на устройство
- `EXECUTING` - Команда выполняется
- `SUCCESS` - Команда выполнена успешно
- `FAILED` - Команда завершилась с ошибкой
- `CANCELLED` - Команда отменена

## Curl команда для теста

```bash
curl -X POST http://localhost:8080/api/v1/commands \
  -H "Content-Type: application/json" \
  -d '{
    "device_id": "rpi-001",
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
          "headers": {
            "Content-Type": "application/json"
          },
          "body": {
            "pw": "admin"
          },
          "timeout_ms": 10000
        },
        "extract": {
          "token": "$.token"
        }
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
          "timeout_ms": 15000
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
          "timeout_ms": 10000
        }
      }
    ],
    "policy": {
      "max_retries": 2,
      "retry_delay_ms": 2000
    }
  }'
```
