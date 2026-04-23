# CryptoTerm Backend

> REST API для мониторинга и управления криптовалютными ASIC-майнерами на базе Raspberry Pi

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

## 📋 Содержание

- [О проекте](#о-проекте)
- [Архитектура](#архитектура)
- [Технологии](#технологии)
- [Быстрый старт](#быстрый-старт)
- [Конфигурация](#конфигурация)
- [API документация](#api-документация)
- [MQTT Topics](#mqtt-topics)
- [База данных](#база-данных)
- [Бэкапы](#бэкапы)
- [Безопасность](#безопасность)

## 🎯 О проекте

CryptoTerm - это система мониторинга и управления ASIC-майнерами, подключенными к Raspberry Pi. Система позволяет:

- 📊 **Мониторить метрики** - температура, хешрейт, кастомные метрики в реальном времени
- 🎛️ **Управлять майнерами** - переключение режимов работы (ECO/STANDARD/OVERCLOCK)
- ⚡ **Шаблоны команд** - переиспользуемые профили мощности/охлаждения для ASIC
- 📈 **Анализировать данные** - агрегация временных рядов через TimescaleDB
- 💰 **Рассчитывать профит** - анализ доходности на основе данных сети Bitcoin
- 📡 **Real-time коммуникация** - двусторонняя связь через MQTT
- 🔐 **Безопасность** - JWT аутентификация и HTTPS

## 🏗️ Архитектура

```
┌─────────────────┐
│  Raspberry Pi   │ ──── MQTT ───┐
│   + Miners      │              │
└─────────────────┘              │
                                 ▼
┌─────────────────┐      ┌──────────────┐
│   Web Client    │◄────►│    Nginx     │
│   (React SPA)   │      │   (Proxy)    │
└─────────────────┘      └──────┬───────┘
                                │
                         ┌──────▼───────┐
                         │  Spring Boot │
                         │   Backend    │
                         └──┬────┬────┬─┘
                            │    │    │
              ┌─────────────┘    │    └─────────────┐
              ▼                  ▼                  ▼
      ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
      │ TimescaleDB  │   │   MongoDB    │   │  Mosquitto   │
      │ (PostgreSQL) │   │  (Configs)   │   │    (MQTT)    │
      └──────────────┘   └──────────────┘   └──────────────┘
```

### Компоненты

- **Backend (Spring Boot)** - REST API, MQTT клиент, бизнес-логика
- **TimescaleDB** - хранение временных рядов (метрики, логи)
- **MongoDB** - хранение конфигураций майнеров
- **Mosquitto** - MQTT брокер для real-time коммуникации
- **Nginx** - reverse proxy, SSL терминация
- **Certbot** - автоматическое обновление SSL сертификатов

## 🛠️ Технологии

### Backend
- **Java 21** - язык программирования
- **Spring Boot 3.3.4** - фреймворк
- **Spring Security** - аутентификация и авторизация
- **JWT (jjwt)** - токены доступа
- **Spring Data JPA** - работа с реляционными БД
- **Spring Data MongoDB** - работа с документной БД
- **Eclipse Paho** - MQTT клиент
- **Flyway** - миграции БД
- **OpenAPI 3 (Swagger)** - документация API

### Databases
- **TimescaleDB (PostgreSQL 15)** - time-series БД
- **MongoDB** - документная БД

### Infrastructure
- **Docker & Docker Compose** - контейнеризация
- **Nginx** - reverse proxy
- **Let's Encrypt** - SSL сертификаты
- **DuckDNS** - динамический DNS

## 🚀 Быстрый старт

### Требования

- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM минимум
- Открыты порты: 80, 443, 1883

### Установка

1. **Клонировать репозиторий**
   ```bash
   git clone <repository-url>
   cd cryptoterm_backend
   ```

2. **Настроить переменные окружения**
   ```bash
   # Создать .env файл (опционально)
   cp .env.example .env
   
   # Отредактировать docker-compose.yml
   # Обязательно изменить JWT_SECRET на production!
   ```

3. **Запустить все сервисы**
   ```bash
   docker-compose up -d
   ```

4. **Проверить статус**
   ```bash
   docker-compose ps
   docker-compose logs -f backend
   ```

5. **Открыть Swagger UI**
   ```
   https://cryptoterm.ru/swagger-ui.html
   или
   http://localhost:8080/swagger-ui.html
   ```

### Разработка локально

1. **Запустить только зависимости**
   ```bash
   docker-compose up -d timescaledb mongodb mosquitto
   ```

2. **Собрать проект**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Запустить приложение**
   ```bash
   java -jar target/cryptoterm-backend-0.0.1-SNAPSHOT.jar
   ```

4. **Или через Maven**
   ```bash
   mvn spring-boot:run
   ```

## ⚙️ Конфигурация

### Переменные окружения

| Переменная | Описание | По умолчанию |
|-----------|----------|--------------|
| `DB_HOST` | Хост PostgreSQL | `timescaledb` |
| `DB_PORT` | Порт PostgreSQL | `5432` |
| `DB_NAME` | Имя БД PostgreSQL | `cryptoterm` |
| `DB_USER` | Пользователь PostgreSQL | `cryptoterm` |
| `DB_PASSWORD` | Пароль PostgreSQL | `cryptoterm` |
| `MONGODB_HOST` | Хост MongoDB | `mongodb` |
| `MONGODB_PORT` | Порт MongoDB | `27017` |
| `MONGODB_DATABASE` | Имя БД MongoDB | `cryptoterm` |
| `MONGODB_USERNAME` | Пользователь MongoDB | `cryptoterm` |
| `MONGODB_PASSWORD` | Пароль MongoDB | `cryptoterm` |
| `MQTT_BROKER` | URL MQTT брокера | `tcp://mosquitto:1883` |
| `JWT_SECRET` | Секретный ключ для JWT (мин. 32 символа) | ⚠️ **Обязательно изменить!** |
| `JWT_EXPIRATION_SECONDS` | Время жизни access токена | `3600` (1 час) |
| `JWT_REFRESH_EXPIRATION_SECONDS` | Время жизни refresh токена | `648000` (7 дней) |
| `DEVICE_REGISTRATION_API_KEY` | API ключ для регистрации устройств | ⚠️ **Обязательно изменить!** |
| `SERVER_PORT` | Порт приложения | `8080` |

### Конфигурационные файлы

- `src/main/resources/application.yml` - основная конфигурация
- `src/main/resources/application-test.yml` - конфигурация для тестов
- `docker-compose.yml` - оркестрация контейнеров
- `nginx/nginx.conf` - конфигурация Nginx
- `mosquitto.conf` - конфигурация MQTT брокера

## 📚 API документация

### Swagger UI

Интерактивная документация доступна по адресу:
- **Production**: https://cryptoterm.ru/swagger-ui.html
- **Local**: http://localhost:8080/swagger-ui.html

### Основные эндпоинты

#### 🔐 Аутентификация (`/api/auth`)

- `POST /api/auth/register` - регистрация нового пользователя
- `POST /api/auth/login` - вход пользователя
- `POST /api/auth/refresh` - обновление токена

#### 📱 Устройства (`/api/deviceAuth`)

- `POST /api/deviceAuth/signup` - регистрация Raspberry Pi устройства

#### 👥 Управление устройствами (`/api/users/{userId}`)

- `GET /api/users/{userId}/devices` - список устройств пользователя
- `GET /api/users/{userId}/devices/detailed` - детальная информация об устройствах
- `GET /api/devices/{deviceId}` - информация об устройстве

#### 📊 Метрики

- `GET /api/hashrate/device/{deviceId}` - метрики хешрейта
- `GET /api/temperature/device/{deviceId}` - метрики температуры
- `GET /api/other-metrics/{metricName}/device/{deviceId}` - кастомные метрики

#### 🎛️ Управление (`/api/control`)

- `POST /api/control/miner/{minerId}/mode` - изменить режим работы майнера
- `POST /api/control/miner/{minerId}/restart` - перезапустить майнер

#### 🔧 Конфигурация майнеров (`/api/miner-config`)

- `GET /api/miner-config/miner/{minerId}` - получить конфигурацию майнера
- `PUT /api/miner-config/{configId}` - обновить конфигурацию

#### 💰 Профит (`/api/profit`)

- `GET /api/profit/{deviceId}` - рассчитать профит устройства

#### 📋 Логи (`/api/logs`)

- `GET /api/logs/device/{deviceId}` - получить логи устройства

#### 👨‍💼 Админ (`/api/admin`)

- `GET /api/admin/device-api-key` - получить API ключ для регистрации устройств
- `POST /api/admin/asic-command-templates` - создать шаблон команды для ASIC
- `GET /api/admin/asic-command-templates` - список всех шаблонов команд
- `DELETE /api/admin/asic-command-templates/{name}` - удалить шаблон

#### ⚡ Шаблоны команд ASIC (`/api/v1/asic-command-templates`)

- `GET /api/v1/asic-command-templates` - список доступных шаблонов
- `GET /api/v1/asic-command-templates/model/{model}` - шаблоны для конкретной модели
- `POST /api/v1/miners/{minerId}/execute-template/{templateName}` - выполнить шаблон на майнере
- `POST /api/v1/commands/scheduled` - отложенная команда (тот же JSON, что и для команды + время исполнения `scheduledAt`)

  Пример запроса на исполнение команды **29 января 2026 в 22:50 по МСК** (тело как для немедленной команды + `scheduledAt`):

  ```json
  {
    "command": {
      "deviceId": "rp-001",
      "asic": {
        "ip": "192.168.88.42",
        "firmware": "anthill",
        "port": 80,
        "scheme": "http"
      },
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
            "headers": {"Content-Type": "application/json", "Authorization": "Bearer ${token}"},
            "body": {"miner": {"overclock": {"preset": "profile_800_77T"}}},
            "timeoutMs": 15000
          }
        }
      ],
      "policy": {"maxRetries": 2, "retryDelayMs": 2000}
    },
    "scheduledAt": "2026-01-29T22:50:00+03:00"
  }
  ```

  **Если на защищённые эндпоинты (например `/api/v1/commands/scheduled`) приходит 403 Forbidden:** используйте access token из ответа логина (поле `accessToken`), заголовок `Authorization: Bearer <токен>`. В Postman: Authorization → Type «Bearer Token» → в поле Token — только значение токена. После обновления бэкенда выполните логин заново и используйте новый токен.

> 📖 **Подробная документация**: [ASIC_COMMAND_TEMPLATES_RU.md](ASIC_COMMAND_TEMPLATES_RU.md)

## 📡 MQTT Topics

### Device → Server

#### `rp/metrics`
Отправка метрик с устройства
    ```json
{
  "deviceId": "uuid",
  "minerId": "uuid",
  "temperatureC": 45.2,
  "hashRateThs": 100.3,
  "timestamp": 1710000000000
}
```

#### `rp/logs`
Отправка логов с устройства
    ```json
{
  "deviceId": "uuid",
  "level": "INFO",
  "message": "Miner started successfully",
  "timestamp": 1710000000000
}
```

#### `rp/registration`
Первичная регистрация устройства
    ```json
{
  "email": "user@example.com",
  "telegram": "@username",
  "ipAddress": "192.168.1.100"
}
```

### Server → Device

#### `rp/command/{deviceId}`
Команды для устройства
    ```json
{
  "deviceId": "uuid",
  "minerId": "uuid",
  "command": "SET_MODE",
  "mode": "OVERCLOCK"
}
```

Доступные режимы:
- `ECO` - экономичный режим
- `STANDARD` - стандартный режим
- `OVERCLOCK` - режим разгона

## 🗄️ База данных

### TimescaleDB (PostgreSQL)

#### Таблицы

- `app_user` - пользователи системы
- `device` - зарегистрированные устройства
- `miner` - майнеры на устройствах
- `metric_type` - типы метрик
- `metric` - метрики (hypertable)
- `other_metric` - кастомные метрики (hypertable)
- `device_log` - логи устройств (hypertable)

#### Миграции

Миграции управляются через Flyway:
- `V1__init.sql` - начальная схема
- `V2__add_username_and_refresh_token.sql` - добавление refresh токенов
- `V3__add_other_metric_table.sql` - таблица кастомных метрик
- `V4__metric_type_integer.sql` - изменение типа метрик
- `V5__nullable_miner_id_in_other_metric.sql` - nullable miner_id

### MongoDB

#### Коллекции

- `miner_config` - конфигурации майнеров
- `asic_command_templates` - шаблоны команд для ASIC
- `asic_commands` - история выполненных команд

## 💾 Бэкапы

Система автоматических ежедневных бэкапов настроена через Docker контейнер.

### Автоматические бэкапы

- Выполняются каждый день в **2:00 AM UTC**
- Автоматическое удаление бэкапов старше **7 дней**
- Бэкапируются и PostgreSQL, и MongoDB

### Ручной бэкап

```bash
docker exec cryptoterm_backup /app/backup-manual.sh
```

### Просмотр бэкапов

```bash
docker exec cryptoterm_backup ls -lh /var/backups/cryptoterm
```

### Восстановление

См. подробную инструкцию в [BACKUP_README.md](BACKUP_README.md)

## 🔒 Безопасность

### Реализованные меры безопасности

- ✅ **JWT аутентификация** - stateless токены
- ✅ **HTTPS** - SSL сертификаты от Let's Encrypt
- ✅ **CORS** - настроенные политики для разных эндпоинтов
- ✅ **Rate limiting** - защита от DDoS
- ✅ **API Key** - для регистрации устройств
- ✅ **Валидация паролей** - требования к сложности
- ✅ **Bcrypt** - хеширование паролей
- ✅ **Изолированная сеть** - БД недоступны извне

### Рекомендации для production

1. **Обязательно измените секреты:**
   ```bash
   # Сгенерировать JWT_SECRET
   openssl rand -hex 32
   
   # Сгенерировать DEVICE_REGISTRATION_API_KEY
   openssl rand -base64 24
   ```

2. **Измените пароли БД** в docker-compose.yml

3. **Настройте firewall:**
   ```bash
   ufw allow 80/tcp
   ufw allow 443/tcp
   ufw allow 1883/tcp
   ufw enable
   ```

4. **Регулярно обновляйте зависимости:**
   ```bash
   mvn versions:display-dependency-updates
   ```

5. **Мониторинг логов:**
   ```bash
   docker-compose logs -f --tail=100
   ```

## 🧪 Тестирование

### Запуск тестов

```bash
# Все тесты
mvn test

# Конкретный тест
mvn test -Dtest=AuthServiceTest

# С покрытием
mvn test jacoco:report
```

### Интеграционные тесты

```bash
mvn verify
```

## 📦 Сборка

### JAR файл

```bash
mvn clean package
# Результат: target/cryptoterm-backend-0.0.1-SNAPSHOT.jar
```

### Docker образ

```bash
docker build -t cryptoterm-backend:latest .
```

## 🤝 Разработка

### Структура проекта

```
src/
├── main/
│   ├── java/com/cryptoterm/backend/
│   │   ├── application/       # Use cases, порты
│   │   ├── domain/            # Доменные модели
│   │   ├── infrastructure/    # Адаптеры
│   │   ├── presentation/      # REST контроллеры (новая структура)
│   │   ├── web/              # REST контроллеры (старая структура)
│   │   ├── security/          # Конфигурация безопасности
│   │   ├── config/            # Конфигурация приложения
│   │   └── mqtt/              # MQTT обработчики
│   └── resources/
│       ├── application.yml    # Конфигурация
│       └── db/migration/      # Flyway миграции
└── test/                      # Тесты
```

### Добавление нового эндпоинта

1. Создать контроллер в `web/` или `presentation/api/`
2. Добавить аннотации Swagger
3. Реализовать бизнес-логику в `service/`
4. Добавить тесты
5. Обновить документацию

## 📄 Лицензия

MIT License - см. [LICENSE](LICENSE)

## 👥 Команда

- Backend разработка: CryptoTerm Team
- Email: support@cryptoterm.com

## 📖 Дополнительная документация

### ASIC Command Templates (Шаблоны команд)

- [ASIC_COMMAND_TEMPLATES_RU.md](ASIC_COMMAND_TEMPLATES_RU.md) - полное руководство на русском
- [ASIC_COMMAND_TEMPLATES_QUICKSTART.md](ASIC_COMMAND_TEMPLATES_QUICKSTART.md) - быстрый старт
- [ASIC_COMMAND_TEMPLATES_EXAMPLES.md](ASIC_COMMAND_TEMPLATES_EXAMPLES.md) - примеры шаблонов
- [ASIC_COMMAND_TEMPLATES_IMPLEMENTATION.md](ASIC_COMMAND_TEMPLATES_IMPLEMENTATION.md) - детали реализации
- [test-asic-templates.sh](test-asic-templates.sh) - скрипт для тестирования API

### ASIC HTTP Proxy

- [ASIC_PROXY_QUICKSTART.md](ASIC_PROXY_QUICKSTART.md) - быстрый старт
- [ASIC_PROXY_EXAMPLES.md](ASIC_PROXY_EXAMPLES.md) - примеры команд
- [ASIC_PROXY_DETAILED_EXPLANATION.md](ASIC_PROXY_DETAILED_EXPLANATION.md) - подробное описание

### Другие документы

- [BACKUP_README.md](BACKUP_README.md) - система бэкапов
- [DEPLOYMENT.md](DEPLOYMENT.md) - деплой в production
- [SECURITY_SUMMARY.md](SECURITY_SUMMARY.md) - безопасность системы
- [TEST_SUMMARY.md](TEST_SUMMARY.md) - тестирование

## 🔗 Полезные ссылки

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [TimescaleDB Documentation](https://docs.timescale.com/)
- [Eclipse Paho](https://www.eclipse.org/paho/)
- [OpenAPI Specification](https://swagger.io/specification/)

---

Made with ❤️ by CryptoTerm Team
