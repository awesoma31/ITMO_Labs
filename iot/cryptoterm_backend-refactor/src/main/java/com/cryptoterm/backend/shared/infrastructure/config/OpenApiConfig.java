package com.cryptoterm.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "CryptoTerm Backend API",
        version = "2.0.0",
        description = """
            REST API для мониторинга и управления майнерами на базе Raspberry Pi.
            
            ## Возможности
            - 🔐 JWT аутентификация пользователей
            - 📊 Сбор метрик (температура, хешрейт, кастомные метрики)
            - 🎛️ Управление режимами работы майнеров
            - 📈 Агрегированные данные с TimescaleDB
            - 📡 MQTT для real-time коммуникации
            - 🔧 Управление конфигурацией майнеров
            - 💰 Расчет профита от майнинга
            
            ## Аутентификация
            Большинство эндпоинтов требуют JWT токен в заголовке:
            ```
            Authorization: Bearer <your-jwt-token>
            ```
            
            Получить токен можно через `/api/auth/login` или `/api/auth/register`
            
            ## Публичные эндпоинты (без токена)
            - `/api/auth/**` - регистрация и вход
            - `/api/device-auth/**` - регистрация устройств (Raspberry Pi)
            - `/swagger-ui.html` - документация API
            """,
        contact = @Contact(
            name = "CryptoTerm Team",
            email = "support@cryptoterm.com",
            url = "https://cryptoterm.ru"
        ),
        license = @License(
            name = "MIT License",
            url = "https://opensource.org/licenses/MIT"
        )
    ),
    servers = {
        @Server(
            url = "https://cryptoterm.ru",
            description = "Production Server"
        ),
        @Server(
            url = "http://localhost:8080",
            description = "Local Development Server"
        )
    },
    tags = {
        @Tag(name = "Authentication", description = "API для аутентификации и регистрации пользователей"),
        @Tag(name = "Аутентификация устройств", description = "API для регистрации устройств (Raspberry Pi с майнерами)"),
        @Tag(name = "Устройства пользователей", description = "API для управления устройствами пользователей"),
        @Tag(name = "Команды ASIC устройств", description = "Упрощенный API для управления и выполнения команд ASIC"),
        @Tag(name = "Шаблоны команд ASIC", description = "API для управления шаблонами команд ASIC"),
        @Tag(name = "Управление майнерами", description = "Упрощенный API управления майнерами"),
        @Tag(name = "Метрики хэшрейта", description = "API для получения метрик хэшрейта устройств"),
        @Tag(name = "Метрики температуры", description = "API для получения метрик температуры устройств"),
        @Tag(name = "Метрики потребления энергии", description = "API для получения метрик потребления энергии"),
        @Tag(name = "Instance Metrics", description = "API для получения метрик конкретных instances (ASIC или датчиков)"),
        @Tag(name = "Прочие метрики", description = "API для универсальных численных метрик (температура окружения и т.д.)"),
        @Tag(name = "Типы метрик", description = "Управление типами численных метрик"),
        @Tag(name = "Логи", description = "API для работы с логами устройств"),
        @Tag(name = "Прибыль от майнинга", description = "API для расчета прибыли от майнинга"),
        @Tag(name = "Конфигурации майнеров", description = "API для управления конфигурациями майнеров"),
        @Tag(name = "Условия", description = "API для управления условиями"),
        @Tag(name = "Температурные датчики", description = "API для управления температурными датчиками"),
        @Tag(name = "Администратор", description = "API для администраторов")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT токен для аутентификации. Получите его через /api/auth/login",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
@SecurityScheme(
    name = "apiKey",
    description = "API ключ для регистрации устройств. Получите через /api/admin/device-api-key (требуется роль ADMIN)",
    type = SecuritySchemeType.APIKEY,
    in = SecuritySchemeIn.HEADER,
    paramName = "X-API-Key"
)
public class OpenApiConfig {
}

