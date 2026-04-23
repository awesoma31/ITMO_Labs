// src/api.js
import { auth } from './auth';
import { keysToCamel, keysToSnake } from './utils/caseConverter';

// Получаем BASE_URL из переменных окружения
// Для production используйте /api (через nginx proxy)
// Для development используйте http://localhost:8080/api
const BASE_URL = import.meta.env.VITE_API_BASE_URL || "/api";

// Логирование для отладки (удалите в production)
console.log('API Base URL:', BASE_URL);

// Флаг для предотвращения множественных запросов на обновление токена
let isRefreshing = false;
let refreshPromise = null;

// Функция для обновления токена
const refreshAccessToken = async () => {
    if (isRefreshing) {
        return refreshPromise;
    }
    
    isRefreshing = true;
    refreshPromise = (async () => {
        try {
            const refreshToken = auth.getRefreshToken();
            if (!refreshToken) {
                throw new Error('No refresh token available');
            }
            
            console.log('Access token expired, attempting refresh...');
            
            const res = await fetch(`${BASE_URL}/auth/refresh`, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(keysToSnake({ refreshToken })),
            });
            
            if (!res.ok) {
                throw new Error('Failed to refresh token');
            }
            
            const data = await parseJsonResponse(res);
            
            // Сохраняем новые токены (теперь в camelCase после конвертации)
            if (data.accessToken) {
                auth.saveToken(data.accessToken);
            }
            if (data.refreshToken) {
                auth.saveRefreshToken(data.refreshToken);
            }
            
            return data.accessToken;
        } catch (error) {
            console.error('Token refresh failed:', error);
            // Удаляем токены и перенаправляем на логин
            auth.removeToken();
            window.location.href = '/admin/login';
            throw error;
        } finally {
            isRefreshing = false;
            refreshPromise = null;
        }
    })();
    
    return refreshPromise;
};

// Обертка для fetch с автоматическим обновлением токена и конвертацией case
const fetchWithAuth = async (url, options = {}) => {
    let token = auth.getToken();
    
    // Если токена нет, пытаемся обновить
    if (!token) {
        token = await refreshAccessToken();
    }
    
    // Конвертируем body в snake_case если есть
    if (options.body && typeof options.body === 'string') {
        try {
            const bodyObj = JSON.parse(options.body);
            const snakeCaseBody = keysToSnake(bodyObj);
            options.body = JSON.stringify(snakeCaseBody);
        } catch (e) {
            // Если body не JSON, оставляем как есть
        }
    }
    
    // Добавляем токен в заголовки
    const headers = {
        ...options.headers,
        'Authorization': `Bearer ${token}`
    };
    
    // ВАЖНО: credentials для CORS с allowCredentials: true
    const response = await fetch(url, { 
        ...options, 
        headers,
        credentials: 'include' // Необходимо для CORS с allowCredentials
    });
    
    // Если получили 401, пытаемся обновить токен и повторить запрос
    if (response.status === 401) {
        console.log('Got 401, attempting to refresh token...');
        token = await refreshAccessToken();
        
        // Повторяем запрос с новым токеном
        const retryHeaders = {
            ...options.headers,
            'Authorization': `Bearer ${token}`
        };
        
        return fetch(url, { 
            ...options, 
            headers: retryHeaders,
            credentials: 'include'
        });
    }
    
    return response;
};

// Обертка для парсинга JSON с конвертацией в camelCase
const parseJsonResponse = async (response) => {
    const data = await response.json();
    return keysToCamel(data);
};

// --- Вспомогательные функции ---

/**
 * 💡 Форматирует дату в строку ISO 8601 без миллисекунд (2025-11-28T01:23:11Z)
 * Это повышает совместимость с бэкендом на OffsetDateTime.
 */
const formatISOTime = (date) => {
    const isoString = date.toISOString(); 
    // Обрезаем миллисекунды и добавляем 'Z'
    const withoutMillis = isoString.slice(0, isoString.lastIndexOf('.')) + 'Z'; 
    return withoutMillis;
};


/**
 * 💡 Определяет оптимальный интервал агрегации (bucket) для метрик.
 * Увеличенная частота для коротких периодов (10 сек для 15 мин).
 */
const getMetricsBucket = (from, to) => {
    const durationSeconds = (to.getTime() - from.getTime()) / 1000;

    if (durationSeconds <= 900) { // Меньше или равно 15 минут (900 сек)
        return '10 sec'; 
    } else if (durationSeconds <= 3600) { // Меньше или равно 1 часу
        return '1 min';
    } else if (durationSeconds <= 86400) { // Меньше или равно 1 дню
        return '5 min';
    } else { // Больше 1 дня
        return '1 hour';
    }
};

// --- API Методы ---

export const api = {

    // 1. Авторизация
    login: async (email, password) => {
        try {
            console.log('Attempting login with email:', email);
            
            // Бэкенд ожидает: { username_or_email, password }
            const requestBody = keysToSnake({ 
                usernameOrEmail: email, 
                password 
            });
            
            console.log('Login request body:', requestBody);
            
            const res = await fetch(`${BASE_URL}/auth/login`, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(requestBody),
            });
            
            console.log('Login response status:', res.status);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('Login error response:', errorText);
                
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                    errorData = keysToCamel(errorData);
                } catch (e) {
                    errorData = { error: errorText };
                }
                
                throw new Error(errorData.error || errorData.message || `Ошибка входа: ${res.status}`);
            }
            
            const responseData = await parseJsonResponse(res);
            console.log('Login response:', responseData);
            
            // Проверяем что ответ содержит необходимые поля
            if (!responseData.accessToken || !responseData.refreshToken) {
                console.error('Invalid response format:', responseData);
                throw new Error('Неверный формат ответа сервера');
            }
            
            return responseData;
        } catch (error) {
            console.error('Login error:', error);
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Не удается подключиться к серверу. Проверьте интернет-соединение.');
            }
            throw error;
        }
    },

    signup: async (email, password) => {
        try {
            // Генерируем username из email (часть до @)
            const username = email.split('@')[0];
            
            console.log('Attempting signup with email:', email);
            
            // Бэкенд ожидает: { username, email, password, telegram? }
            // Благодаря глобальной настройке Jackson все поля автоматически конвертируются в snake_case
            const res = await fetch(`${BASE_URL}/auth/register`, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify({ 
                    username, 
                    email, 
                    password 
                }),
            });
            
            console.log('Signup response status:', res.status);
            
            if (!res.ok) {
                const errorText = await res.text();
                console.error('Signup error response:', errorText);
                
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                    errorData = keysToCamel(errorData);
                } catch (e) {
                    errorData = { error: errorText };
                }
                
                throw new Error(errorData.error || errorData.message || `Ошибка регистрации: ${res.status}`);
            }
            
            const responseData = await parseJsonResponse(res);
            console.log('Signup response:', responseData);
            
            // Проверяем что ответ содержит необходимые поля
            if (!responseData.accessToken || !responseData.refreshToken) {
                console.error('Invalid response format:', responseData);
                throw new Error('Неверный формат ответа сервера');
            }
            
            return responseData;
        } catch (error) {
            console.error('Signup error:', error);
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Не удается подключиться к серверу. Проверьте интернет-соединение.');
            }
            throw error;
        }
    },

    // Обновление токена через refreshToken
    refreshToken: async (refreshToken) => {
        try {
            // Конвертируем в snake_case для отправки
            const res = await fetch(`${BASE_URL}/auth/refresh`, {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Accept": "application/json"
                },
                body: JSON.stringify(keysToSnake({ refreshToken })),
            });
            
            if (!res.ok) {
                const errorText = await res.text();
                let errorData;
                try {
                    errorData = JSON.parse(errorText);
                    errorData = keysToCamel(errorData);
                } catch (e) {
                    errorData = { error: errorText };
                }
                throw new Error(errorData.error || errorData.message || `Ошибка обновления токена: ${res.status}`);
            }
            return parseJsonResponse(res);
        } catch (error) {
            if (error.name === 'TypeError' && error.message.includes('fetch')) {
                throw new Error('Не удается подключиться к серверу. Проверьте интернет-соединение.');
            }
            throw error;
        }
    },

    // 2. Получение устройств и майнеров пользователя
    getUserDevices: async (token, userId) => {
        const res = await fetchWithAuth(`${BASE_URL}/users/${userId}/devices`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error('Failed to fetch devices');
        }
        return parseJsonResponse(res);
    },

    // Получение детальной информации об устройствах
    getUserDevicesDetailed: async (token, userId) => {
        const res = await fetchWithAuth(`${BASE_URL}/users/${userId}/devices/detailed`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error('Failed to fetch devices detailed');
        }
        return parseJsonResponse(res);
    },

    // Получение информации о конкретном устройстве
    getDeviceById: async (token, deviceId) => {
        const res = await fetchWithAuth(`${BASE_URL}/users/devices/${deviceId}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error('Failed to fetch device');
        }
        return parseJsonResponse(res);
    },

    // 3. Метрики хэшрейта
    getHashRateByDevice: async (token, deviceId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/hashrate/device/${deviceId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch hashrate metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getHashRateByUser: async (token, userId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/hashrate/user/${userId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch hashrate metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    // Метрики температуры
    getTemperatureByDevice: async (token, deviceId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/temperature/device/${deviceId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch temperature metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getTemperatureByUser: async (token, userId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/temperature/user/${userId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch temperature metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    // Метрики потребления энергии
    getPowerConsumptionByDevice: async (token, deviceId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/power-consumption/device/${deviceId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch power consumption metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getPowerConsumptionByUser: async (token, userId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/power-consumption/user/${userId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch power consumption metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    // Другие метрики (универсальный endpoint)
    getOtherMetricByDevice: async (token, metricName, deviceId, from, to, bucket) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));
        const bucketParam = encodeURIComponent(bucket || getMetricsBucket(from, to));

        const url = `${BASE_URL}/metrics/other/${metricName}/device/${deviceId}?from=${fromParam}&to=${toParam}&bucket=${bucketParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch ${metricName} metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getOtherMetricSensors: async (token, deviceId) => {
        const url = `${BASE_URL}/metrics/other/device/${deviceId}/sensors`;
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error(`Failed to fetch sensors. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getOtherMetricLatest: async (token, deviceId) => {
        const url = `${BASE_URL}/metrics/other/device/${deviceId}/latest`;
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error(`Failed to fetch latest metrics. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    getMetricTypes: async (token) => {
        const url = `${BASE_URL}/metric-types`;
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error(`Failed to fetch metric types. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    // 4. Логи устройства
    getDeviceLogs: async (token, deviceId, from, to) => {
        const fromParam = encodeURIComponent(formatISOTime(from));
        const toParam = encodeURIComponent(formatISOTime(to));

        const url = `${BASE_URL}/devices/${deviceId}/logs?from=${fromParam}&to=${toParam}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });

        if (!res.ok) {
            throw new Error(`Failed to fetch logs. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },
    
    // Alias для обратной совместимости
    getMinerLogs: async (token, deviceId, from, to) => {
        return api.getDeviceLogs(token, deviceId, from, to);
    },

    // 5. Управление майнерами
    setMinerMode: async (token, minerId, mode) => {
        const url = `${BASE_URL}/v1/miners/${minerId}/change-mode`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { 
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ mode })
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Mode change failed. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    scheduleChangeMinerMode: async (token, minerId, mode, scheduledAt, powerWatts = null, hashrate = null) => {
        const url = `${BASE_URL}/v1/miners/${minerId}/schedule-change-mode`;
        
        // Конвертируем scheduledAt в ISO string если это Date
        const scheduledAtISO = scheduledAt instanceof Date 
            ? scheduledAt.toISOString() 
            : scheduledAt;
        
        const requestBody = {
            mode,
            scheduledAt: scheduledAtISO
        };
        
        // Добавляем опциональные параметры, если они указаны
        if (powerWatts !== null) {
            requestBody.powerWatts = powerWatts;
        }
        if (hashrate !== null) {
            requestBody.hashrate = hashrate;
        }
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { 
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Schedule mode change failed. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    restartMiner: async (token, minerId) => {
        const url = `${BASE_URL}/v1/miners/${minerId}/reboot`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Reboot failed. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    pauseMining: async (token, minerId) => {
        const url = `${BASE_URL}/v1/miners/${minerId}/pause-mining`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Pause mining failed. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    continueMining: async (token, minerId) => {
        const url = `${BASE_URL}/v1/miners/${minerId}/continue-mining`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Continue mining failed. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    // 6. Калькулятор прибыли (для всех устройств пользователя)
    getMiningProfit: async (token, userId, from, to) => {
        let url = `${BASE_URL}/users/${userId}/profit`;
        
        if (from && to) {
            const fromParam = encodeURIComponent(formatISOTime(from));
            const toParam = encodeURIComponent(formatISOTime(to));
            url += `?from=${fromParam}&to=${toParam}`;
        }
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to calculate profit. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    // ============ NEW ASIC DEVICE COMMANDS API ============
    
    /**
     * Выполнить команду на ASIC устройстве (новый API)
     * @param {string} rpId - ID Raspberry Pi (UUID)
     * @param {string} asicId - ID ASIC майнера (UUID)
     * @param {string} commandId - ID команды (template name)
     */
    executeAsicCommand: async (rpId, asicId, commandId) => {
        const url = `${BASE_URL}/v1/asic-commands/execute?rpId=${rpId}&asicId=${asicId}&commandId=${encodeURIComponent(commandId)}`;
        
        const res = await fetchWithAuth(url, {
            method: "POST"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to execute command. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Alias для executeAsicCommand (для обратной совместимости)
     */
    executeCommandNew: async (rpId, asicId, commandId) => {
        return api.executeAsicCommand(rpId, asicId, commandId);
    },
    
    /**
     * Получить доступные команды для модели ASIC
     * @param {string} asicModel - Модель ASIC (например, "Antminer S19 Pro Hydro")
     * @param {string} asicVendor - Производитель (опционально, например, "Bitmain")
     */
    getAvailableCommands: async (asicModel, asicVendor = null) => {
        let url = `${BASE_URL}/v1/asic-commands/available?asicModel=${encodeURIComponent(asicModel)}`;
        if (asicVendor) {
            url += `&asicVendor=${encodeURIComponent(asicVendor)}`;
        }
        
        const res = await fetchWithAuth(url);
        
        if (!res.ok) {
            throw new Error(`Failed to fetch available commands. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Получить детали команды по ID
     * @param {string} commandId - ID команды (template name)
     */
    getCommandDetails: async (commandId) => {
        const url = `${BASE_URL}/v1/asic-commands/${encodeURIComponent(commandId)}`;
        
        const res = await fetchWithAuth(url);
        
        if (!res.ok) {
            throw new Error(`Failed to fetch command details. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Запланировать команду на будущее время
     * @param {string} rpId - ID Raspberry Pi
     * @param {string} asicId - ID ASIC майнера
     * @param {string} commandId - ID команды
     * @param {string|Date} scheduledAt - Время выполнения (ISO string или Date)
     */
    scheduleAsicCommandNew: async (rpId, asicId, commandId, scheduledAt) => {
        // Конвертируем scheduledAt в ISO string если это Date
        const scheduledAtISO = scheduledAt instanceof Date 
            ? scheduledAt.toISOString() 
            : scheduledAt;
        
        const url = `${BASE_URL}/v1/asic-commands/schedule?rpId=${rpId}&asicId=${asicId}&commandId=${encodeURIComponent(commandId)}&scheduledAt=${encodeURIComponent(scheduledAtISO)}`;
        
        const res = await fetchWithAuth(url, {
            method: "POST"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to schedule command. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Удалить команду
     * @param {string} cmdId - ID команды
     */
    deleteAsicCommand: async (cmdId) => {
        const url = `${BASE_URL}/v1/asic-commands/commands/${cmdId}`;
        
        const res = await fetchWithAuth(url, {
            method: "DELETE"
        });
        
        if (!res.ok) {
            throw new Error(`Failed to delete command. Status: ${res.status}`);
        }
        // DELETE возвращает 204 No Content, не парсим JSON
        return;
    },
    
    /**
     * Сменить режим работы майнера (ECO, STANDARD, OVERCLOCK)
     * @param {string} rpId - ID Raspberry Pi
     * @param {string} asicId - ID ASIC майнера
     * @param {string} mode - Режим: "ECO", "STANDARD", "OVERCLOCK"
     */
    changeMinerModeNew: async (rpId, asicId, mode) => {
        const url = `${BASE_URL}/v1/asic-commands/change-mode?rpId=${rpId}&asicId=${asicId}&mode=${mode}`;
        
        const res = await fetchWithAuth(url, {
            method: "POST"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to change miner mode. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    // ============ OLD ASIC COMMANDS API (kept for backwards compatibility) ============

    // 7. Управление командами ASIC
    /**
     * Создать команду для ASIC майнера
     * @deprecated Используйте executeAsicCommand вместо этого
     * @param {Object} command - Конфигурация команды
     * @param {string} command.deviceId - ID устройства (Raspberry Pi)
     * @param {Object} command.asic - Информация о ASIC
     * @param {string} command.asic.ip - IP адрес ASIC
     * @param {number} command.asic.port - Порт ASIC (обычно 80)
     * @param {string} command.asic.scheme - http или https
     * @param {string} command.asic.firmware - Тип прошивки (anthill, vnish, etc)
     * @param {Array} command.steps - Шаги команды
     * @param {Object} command.policy - Политика повторов
     */
    createAsicCommand: async (command) => {
        const url = `${BASE_URL}/v1/commands`;
        
        // Создание команд требует аутентификации
        // skipHeaders=true чтобы не конвертировать HTTP заголовки (Content-Type, Authorization)
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(keysToSnake(command, true))
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to create command. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Получить команду по ID
     */
    getAsicCommand: async (token, cmdId) => {
        const url = `${BASE_URL}/v1/commands/${cmdId}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch command. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Получить все команды для устройства
     */
    getAsicCommandsByDevice: async (token, deviceId) => {
        const url = `${BASE_URL}/v1/commands/device/${deviceId}`;
        
        const res = await fetchWithAuth(url, {
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to fetch commands. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Отменить команду
     */
    cancelAsicCommand: async (token, cmdId) => {
        const url = `${BASE_URL}/v1/commands/${cmdId}/cancel`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to cancel command. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Повторно отправить команду
     */
    resendAsicCommand: async (token, cmdId) => {
        const url = `${BASE_URL}/v1/commands/${cmdId}/resend`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
        });
        
        if (!res.ok) {
            throw new Error(`Failed to resend command. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Создать команду для смены режима мощности Antminer S19
     * @param {string} deviceId - ID устройства (Raspberry Pi)
     * @param {string} asicIp - IP адрес ASIC майнера
     * @param {number} watts - Мощность в ваттах (например, 3495)
     * @param {string} password - Пароль ASIC (по умолчанию "admin")
     * @param {number} asicPort - Порт ASIC (по умолчанию 80)
     * @param {string} asicScheme - http или https (по умолчанию "http")
     */
    createPowerModeCommand: async (deviceId, asicIp, watts, password = "admin", asicPort = 80, asicScheme = "http") => {
        const command = {
            deviceId: deviceId,
            asic: {
                ip: asicIp,
                port: asicPort,
                scheme: asicScheme,
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
                            pw: password
                        },
                        timeoutMs: 10000
                    },
                    extract: {
                        token: "$.token"
                    }
                },
                {
                    id: `set_profile_${watts}`,
                    request: {
                        method: "POST",
                        path: "/api/v1/settings",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer ${token}"
                        },
                        body: {
                            miner: {
                                overclock: {
                                    modded_psu: false,
                                    preset: String(watts)
                                }
                            }
                        },
                        timeoutMs: 15000
                    }
                },
                {
                    id: "restart_mining",
                    request: {
                        method: "POST",
                        path: "/api/v1/mining/restart",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer ${token}"
                        },
                        body: {},
                        timeoutMs: 10000
                    }
                }
            ],
            policy: {
                maxRetries: 2,
                retryDelayMs: 2000
            }
        };

        return api.createAsicCommand(command);
    },

    /**
     * Запланировать команду для отложенного выполнения
     * @param {Object} command - Конфигурация команды (такая же как для createAsicCommand)
     * @param {string|Date} scheduledAt - Время выполнения (ISO string или Date объект)
     */
    scheduleAsicCommand: async (command, scheduledAt) => {
        const url = `${BASE_URL}/v1/commands/scheduled`;
        
        // Конвертируем scheduledAt в ISO string если это Date
        const scheduledAtISO = scheduledAt instanceof Date 
            ? scheduledAt.toISOString() 
            : scheduledAt;
        
        const payload = {
            command: command,
            scheduledAt: scheduledAtISO
        };
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(keysToSnake(payload, true))
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to schedule command. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },

    /**
     * Запланировать команду смены режима мощности
     * @param {string} deviceId - ID устройства (Raspberry Pi)
     * @param {string} asicIp - IP адрес ASIC майнера
     * @param {number} watts - Мощность в ваттах
     * @param {string|Date} scheduledAt - Время выполнения
     * @param {string} password - Пароль ASIC
     * @param {number} asicPort - Порт ASIC
     * @param {string} asicScheme - http или https
     */
    schedulePowerModeCommand: async (deviceId, asicIp, watts, scheduledAt, password = "admin", asicPort = 80, asicScheme = "http") => {
        const command = {
            deviceId: deviceId,
            asic: {
                ip: asicIp,
                port: asicPort,
                scheme: asicScheme,
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
                            pw: password
                        },
                        timeoutMs: 10000
                    },
                    extract: {
                        token: "$.token"
                    }
                },
                {
                    id: `set_profile_${watts}`,
                    request: {
                        method: "POST",
                        path: "/api/v1/settings",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer ${token}"
                        },
                        body: {
                            miner: {
                                overclock: {
                                    modded_psu: false,
                                    preset: String(watts)
                                }
                            }
                        },
                        timeoutMs: 15000
                    }
                },
                {
                    id: "restart_mining",
                    request: {
                        method: "POST",
                        path: "/api/v1/mining/restart",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": "Bearer ${token}"
                        },
                        body: {},
                        timeoutMs: 10000
                    }
                }
            ],
            policy: {
                maxRetries: 2,
                retryDelayMs: 2000
            }
        };

        return api.scheduleAsicCommand(command, scheduledAt);
    },

    // ============ ASIC COMMAND TEMPLATES API ============
    
    /**
     * Создать шаблон команды ASIC
     * @param {Object} template - Шаблон команды
     */
    createAsicCommandTemplate: async (template) => {
        const url = `${BASE_URL}/asic-command-templates`;
        
        const res = await fetchWithAuth(url, {
            method: "POST",
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(template)
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to create template. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Получить все шаблоны команд
     */
    getAllCommandTemplates: async () => {
        const url = `${BASE_URL}/asic-command-templates`;
        
        const res = await fetchWithAuth(url);
        
        if (!res.ok) {
            throw new Error(`Failed to fetch templates. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Получить шаблон команды по имени
     * @param {string} templateName - Имя шаблона
     */
    getCommandTemplate: async (templateName) => {
        const url = `${BASE_URL}/asic-command-templates/${encodeURIComponent(templateName)}`;
        
        const res = await fetchWithAuth(url);
        
        if (!res.ok) {
            throw new Error(`Failed to fetch template. Status: ${res.status}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Обновить шаблон команды (только для администратора)
     * @param {string} templateName - Имя шаблона
     * @param {Object} template - Обновлённый шаблон
     */
    updateCommandTemplate: async (templateName, template) => {
        const url = `${BASE_URL}/asic-command-templates/${encodeURIComponent(templateName)}`;
        
        const res = await fetchWithAuth(url, {
            method: "PUT",
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(template)
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to update template. Status: ${res.status}. Response: ${errorText}`);
        }
        return parseJsonResponse(res);
    },
    
    /**
     * Удалить шаблон команды (только для администратора)
     * @param {string} templateName - Имя шаблона
     */
    deleteCommandTemplate: async (templateName) => {
        const url = `${BASE_URL}/asic-command-templates/${encodeURIComponent(templateName)}`;
        
        const res = await fetchWithAuth(url, {
            method: "DELETE"
        });
        
        if (!res.ok) {
            throw new Error(`Failed to delete template. Status: ${res.status}`);
        }
        // DELETE возвращает 204 No Content
        return;
    },

    /**
     * Нормализовать все шаблоны команд в БД (только для администратора).
     * Приводит miner_model и miner_vendor к нижнему регистру.
     * @returns {Promise<{message: string, updatedCount: number}>}
     */
    normalizeAllCommandTemplates: async () => {
        const url = `${BASE_URL}/asic-command-templates/normalize-all`;
        
        const res = await fetchWithAuth(url, {
            method: "POST"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            throw new Error(`Failed to normalize templates. Status: ${res.status}. Response: ${errorText}`);
        }
        
        const data = await parseJsonResponse(res);
        return data;
    }
};