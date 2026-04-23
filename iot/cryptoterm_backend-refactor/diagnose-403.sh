#!/bin/bash

# Диагностика 403 ошибки
# Usage: ./diagnose-403.sh

BASE_URL="https://cryptoterm.ru"

echo "=== Диагностика 403 Forbidden ==="
echo ""

# 1. Проверка actuator/health
echo "1. Проверка health endpoint..."
curl -s "$BASE_URL/actuator/health" | jq '.' 2>/dev/null || echo "Health endpoint недоступен"
echo ""

# 2. Попытка создать команду без токена
echo "2. Тест без токена (должен вернуть 401)..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$BASE_URL/api/v1/commands" \
  -H "Content-Type: application/json" \
  -d '{"device_id":"test","asic":{"ip":"192.168.3.20","port":80,"scheme":"http","firmware":"anthill"},"steps":[],"policy":{"max_retries":0,"retry_delay_ms":1000}}')

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Code: $HTTP_CODE"
echo "Response: $BODY"
echo ""

if [ "$HTTP_CODE" = "403" ]; then
    echo "❌ ПРОБЛЕМА: Получили 403 вместо 401"
    echo "   Это значит что Security фильтр блокирует запрос ДО проверки токена"
    echo ""
fi

# 3. Тест с неправильным токеном
echo "3. Тест с неправильным токеном (должен вернуть 401)..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$BASE_URL/api/v1/commands" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer fake-token-123" \
  -d '{"device_id":"test","asic":{"ip":"192.168.3.20","port":80,"scheme":"http","firmware":"anthill"},"steps":[],"policy":{"max_retries":0,"retry_delay_ms":1000}}')

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "HTTP Code: $HTTP_CODE"
echo "Response: $BODY"
echo ""

if [ "$HTTP_CODE" = "403" ]; then
    echo "❌ ПРОБЛЕМА: Получили 403 вместо 401"
    echo "   Security не проверяет JWT токен правильно"
    echo ""
fi

# 4. Проверка CORS preflight
echo "4. Проверка CORS preflight (OPTIONS)..."
CORS_RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X OPTIONS "$BASE_URL/api/v1/commands" \
  -H "Origin: https://cryptoterm.ru" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: authorization,content-type" \
  -v 2>&1)

echo "$CORS_RESPONSE" | grep -i "access-control"
HTTP_CODE=$(echo "$CORS_RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
echo "HTTP Code: $HTTP_CODE"
echo ""

if [ "$HTTP_CODE" != "200" ]; then
    echo "❌ ПРОБЛЕМА: CORS preflight не работает"
    echo ""
fi

# 5. Проверка логов backend
echo "5. Проверка последних логов backend..."
echo "Выполните на сервере:"
echo "  sudo docker logs cryptoterm_backend --tail 50 | grep -i '403\|forbidden\|security'"
echo ""

# 6. Рекомендации
echo "=== Возможные причины 403 ==="
echo ""
echo "1. RateLimitFilter блокирует запросы"
echo "   Решение: Проверьте rate-limit.enabled в application.properties"
echo ""
echo "2. Spring Security фильтр срабатывает ДО JwtAuthFilter"
echo "   Решение: Проверьте порядок фильтров в SecurityConfig"
echo ""
echo "3. PreAuthorize требует роль, которой нет у пользователя"
echo "   Решение: Проверьте роль в токене пользователя"
echo ""
echo "4. CORS блокирует из-за Origin"
echo "   Решение: Добавьте origin в allowedOriginPatterns"
echo ""

# 7. Инструкции для дальнейшей диагностики
echo "=== Следующие шаги ==="
echo ""
echo "1. Проверьте логи на сервере:"
echo "   ssh user@cryptoterm.ru"
echo "   sudo docker logs cryptoterm_backend --tail 100"
echo ""
echo "2. Проверьте что backend пересобран:"
echo "   sudo docker exec cryptoterm_backend ls -la /app/target/classes/com/cryptoterm/backend/security/"
echo ""
echo "3. Проверьте конфигурацию внутри контейнера:"
echo "   sudo docker exec cryptoterm_backend cat /app/src/main/resources/application.properties | grep rate-limit"
echo ""
echo "4. Попробуйте временно отключить rate-limit:"
echo "   В application.properties добавьте: rate-limit.enabled=false"
echo ""
