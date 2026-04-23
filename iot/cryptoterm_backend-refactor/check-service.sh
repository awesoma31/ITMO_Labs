#!/bin/bash

# Цвета для вывода
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

SERVER="${1:-http://158.0.0.162}"

echo "========================================="
echo "  Проверка CryptoTerm Backend"
echo "  Сервер: $SERVER"
echo "========================================="
echo ""

# Функция для проверки endpoint
check_endpoint() {
    local name="$1"
    local url="$2"
    local expected_code="${3:-200}"
    
    echo -n "[$name] "
    
    http_code=$(curl -s -o /dev/null -w "%{http_code}" "$url" 2>/dev/null)
    
    if [ $? -eq 0 ]; then
        if [ "$http_code" = "$expected_code" ]; then
            echo -e "${GREEN}✓${NC} HTTP $http_code"
            return 0
        else
            echo -e "${YELLOW}⚠${NC} HTTP $http_code (ожидался $expected_code)"
            return 1
        fi
    else
        echo -e "${RED}✗${NC} Connection failed"
        return 2
    fi
}

# Проверка доступности
echo "1. Базовая доступность:"
check_endpoint "Health Check" "${SERVER}/actuator/health"
check_endpoint "Swagger UI" "${SERVER}/swagger-ui.html"
check_endpoint "API Docs (JSON)" "${SERVER}/v3/api-docs"
echo ""

# Проверка через прямой порт 8080 (если доступен)
echo "2. Прямой доступ к backend (порт 8080):"
check_endpoint "Health Check :8080" "${SERVER}:8080/actuator/health"
check_endpoint "Swagger UI :8080" "${SERVER}:8080/swagger-ui.html"
echo ""

# Детальная проверка health
echo "3. Детальная информация Health:"
health_response=$(curl -s "${SERVER}/actuator/health" 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "$health_response" | python3 -m json.tool 2>/dev/null || echo "$health_response"
else
    echo -e "${RED}Не удалось получить health check${NC}"
fi
echo ""

# Проверка API endpoints
echo "4. API Endpoints (требуется авторизация - ожидается 401/403):"
check_endpoint "Users API" "${SERVER}/api/users/test" "401"
check_endpoint "Devices API" "${SERVER}/api/devices/test" "401"
echo ""

# Проверка публичных endpoints
echo "5. Публичные Endpoints:"
echo "   Auth Register:"
curl -s -X POST "${SERVER}/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"test":"test"}' | head -c 200
echo ""
echo ""

# MQTT
echo "6. MQTT Broker (порт 1883):"
server_ip=$(echo $SERVER | sed 's/http:\/\///' | sed 's/https:\/\///' | cut -d':' -f1)
nc -zv $server_ip 1883 2>&1 | grep -q succeeded && echo -e "${GREEN}✓${NC} MQTT доступен" || echo -e "${YELLOW}⚠${NC} MQTT недоступен (возможно, firewall)"
echo ""

echo "========================================="
echo "Проверка завершена!"
echo ""
echo "Для полного тестирования откройте:"
echo "  ${SERVER}/swagger-ui.html"
echo "========================================="
