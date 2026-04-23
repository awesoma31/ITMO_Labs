#!/bin/bash

# Тест создания команды на production сервере
# Usage: ./test-command-creation.sh

BASE_URL="${1:-https://cryptoterm.ru}"

echo "=== Тестирование создания команды ==="
echo "URL: $BASE_URL"
echo ""

# Минимальная команда для теста
TEST_PAYLOAD='{
  "device_id": "test-device",
  "asic": {
    "ip": "192.168.3.20",
    "port": 80,
    "scheme": "http",
    "firmware": "anthill"
  },
  "steps": [
    {
      "id": "test_step",
      "request": {
        "method": "GET",
        "path": "/api/v1/status",
        "headers": {
          "Content-Type": "application/json"
        },
        "body": {},
        "timeout_ms": 5000
      }
    }
  ],
  "policy": {
    "max_retries": 0,
    "retry_delay_ms": 1000
  }
}'

echo "Отправка запроса..."
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -X POST "$BASE_URL/api/v1/commands" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d "$TEST_PAYLOAD" 2>&1)

HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo ""
echo "HTTP Code: $HTTP_CODE"
echo ""
echo "Response Body:"
echo "$BODY" | python3 -m json.tool 2>/dev/null || echo "$BODY"
echo ""

if [ "$HTTP_CODE" = "201" ] || [ "$HTTP_CODE" = "200" ]; then
    echo "✅ SUCCESS - Команда создана!"
elif [ "$HTTP_CODE" = "403" ]; then
    echo "❌ FORBIDDEN (403) - Сервер блокирует запрос"
    echo ""
    echo "Возможные причины:"
    echo "1. Backend на сервере не обновлен после изменений"
    echo "2. Nginx блокирует запросы"
    echo "3. SecurityConfig не применен"
    echo ""
    echo "Решение:"
    echo "  1. Зайдите на сервер: ssh user@cryptoterm.ru"
    echo "  2. Перейдите в папку проекта: cd /path/to/cryptoterm_backend"
    echo "  3. Пересоберите: sudo ./rebuild-backend.sh"
    echo "  4. Проверьте логи: sudo docker logs cryptoterm_backend -f"
elif [ "$HTTP_CODE" = "400" ]; then
    echo "⚠️  BAD REQUEST (400) - Ошибка валидации (это нормально для теста)"
else
    echo "❌ ERROR - Неожиданный код ответа"
fi
