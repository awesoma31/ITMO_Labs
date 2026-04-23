# Разница между шаблоном команды и исполняемой командой

## AsicCommandTemplate (Шаблон команды)

**Назначение**: Переиспользуемый шаблон для создания команд. Хранится в базе данных.

**Структура**:
```json
{
  "name": "5150W_177TH",
  "description": "Режим мощности 5150W (~177 TH/s) для Antminer S19 Pro Hydro",
  "minerModel": "antminer s19 pro hydro",
  "minerVendor": "bitmain",
  "firmware": "anthill",
  "commandType": "MODE_CHANGE",
  "steps": [...],
  "policy": {...},
  "createdAt": "2026-01-31T11:58:56.269Z",
  "createdBy": "user-id"
}
```

**Ключевые поля**:
- `name` - уникальное имя шаблона (используется как ID)
- `minerModel` - для какой модели майнера
- `minerVendor` - производитель майнера
- `firmware` - тип прошивки (anthill, vnish, stock и т.д.)
- `commandType` - тип команды (MODE_CHANGE, REBOOT, OTHER)
- `steps` - шаги выполнения команды
- `policy` - политика повторов

---

## AsicHttpProxyCommand (Исполняемая команда)

**Назначение**: Готовая к выполнению команда для конкретного майнера. Создаётся из шаблона.

**Структура**:
```json
{
  "deviceId": "a1b2c3d4-uuid",
  "minerId": "e5f6g7h8-uuid",
  "cmdId": "cmd-unique-uuid",
  "command": "asic_http_proxy",
  "asic": {
    "ip": "192.168.1.100",
    "port": 80,
    "scheme": "http",
    "firmware": "anthill",
    "id": "miner-id"
  },
  "steps": [...],
  "policy": {...},
  "status": "PENDING",
  "createdAt": "2026-01-31T12:00:00Z"
}
```

**Ключевые поля**:
- `deviceId` - ID устройства Raspberry Pi, через которое отправляется команда
- `minerId` - ID конкретного майнера
- `cmdId` - уникальный ID этой команды
- `command` - тип команды (всегда "asic_http_proxy" для HTTP прокси команд)
- `asic.ip` - IP адрес майнера (определяется автоматически устройством)
- `asic.id` - ID майнера (для идентификации)
- `status` - статус выполнения (PENDING, SENT, EXECUTING, SUCCESS, FAILED)

---

## Процесс преобразования

### 1. Пользователь выбирает команду
```javascript
// Фронтенд отправляет запрос
api.executeAsicCommand(rpId, asicId, "5150W_177TH")
```

### 2. Бэкенд получает запрос
```
POST /api/v1/asic-commands/execute?rpId={rpId}&asicId={asicId}&commandId=5150W_177TH
```

### 3. Контроллер загружает шаблон и майнер
```java
// Загружаем шаблон по имени
AsicCommandTemplate template = templateService.getTemplate("5150W_177TH");

// Загружаем информацию о майнере
Miner miner = minerRepository.findById(asicId);
```

### 4. Проверка совместимости
```java
// Проверяем, что шаблон подходит для этого майнера
if (!isCommandCompatibleWithMiner(template, miner)) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "Команда несовместима с майнером"
    );
}
```

### 5. Преобразование шаблона в команду
```java
// Метод templateToCommand создаёт исполняемую команду
AsicHttpProxyCommand command = templateService.templateToCommand(template, miner);
```

**Что происходит в `templateToCommand`**:
- Устанавливается `deviceId` из `miner.getDevice().getId()`
- Устанавливается `minerId` из `miner.getId()`
- Генерируется уникальный `cmdId`
- Копируется `firmware` из шаблона
- Копируются `steps` из шаблона
- Копируется `policy` из шаблона
- **НЕ устанавливается IP** - он будет определён устройством по `minerId`

### 6. Сохранение и отправка команды
```java
// Сохраняем команду в БД
AsicHttpProxyCommand savedCommand = commandService.createCommand(command);

// Отправляем команду через MQTT на устройство
boolean sent = commandPublisher.sendAsicProxyCommand(savedCommand);
```

### 7. Устройство получает команду
```javascript
// Устройство (Raspberry Pi) получает команду через MQTT
// Оно видит minerId и автоматически определяет IP адрес майнера
// из своей локальной конфигурации

const minerIp = deviceConfig.miners[minerId].ip;
command.asic.ip = minerIp;
```

---

## Почему так сделано?

### Преимущества разделения:

1. **Переиспользование**: Один шаблон используется для всех майнеров одной модели
2. **Централизованное управление**: Изменение шаблона влияет на все будущие команды
3. **Безопасность**: IP адреса не хранятся в шаблонах, определяются на устройстве
4. **Гибкость**: Устройство может динамически определять IP адреса
5. **Аудит**: Каждая выполненная команда сохраняется с полным контекстом

### Когда что использовать:

- **Шаблоны** - для создания, редактирования, просмотра доступных команд
- **Команды** - для выполнения, мониторинга статуса, истории выполнения

---

## Пример полного цикла

### Шаг 1: Создание шаблона (ADMIN)
```bash
POST /api/asic-command-templates
{
  "name": "5150W_177TH",
  "minerModel": "antminer s19 pro hydro",
  ...
}
```

### Шаг 2: Просмотр доступных команд (USER)
```bash
GET /api/v1/asic-commands/available?asicModel=antminer%20s19%20pro%20hydro
# Ответ: список шаблонов для этой модели
```

### Шаг 3: Выполнение команды (USER)
```bash
POST /api/v1/asic-commands/execute?rpId={rpId}&asicId={asicId}&commandId=5150W_177TH
# Ответ: созданная команда с cmdId
```

### Шаг 4: Проверка статуса (USER)
```bash
GET /api/v1/commands/{cmdId}
# Ответ: команда со статусом (PENDING, SENT, EXECUTING, SUCCESS, FAILED)
```

---

## API Endpoints

### Работа с шаблонами
- `GET /api/asic-command-templates` - список всех шаблонов
- `GET /api/asic-command-templates/{name}` - получить шаблон
- `POST /api/asic-command-templates` - создать шаблон (ADMIN)
- `PUT /api/asic-command-templates/{name}` - обновить шаблон (ADMIN)
- `DELETE /api/asic-command-templates/{name}` - удалить шаблон (ADMIN)

### Работа с командами
- `GET /api/v1/asic-commands/available` - доступные команды для модели
- `GET /api/v1/asic-commands/{commandId}` - детали шаблона
- `POST /api/v1/asic-commands/execute` - выполнить команду
- `POST /api/v1/asic-commands/schedule` - запланировать команду
- `POST /api/v1/asic-commands/change-mode` - сменить режим мощности
- `DELETE /api/v1/asic-commands/commands/{cmdId}` - удалить команду
- `GET /api/v1/commands/{cmdId}` - статус команды
