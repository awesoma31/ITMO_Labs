#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# Выпуск SSL-сертификата Let's Encrypt для cryptoterm.ru
#
# Запускать ОДИН РАЗ после того, как:
#   1. DNS-запись A для cryptoterm.ru указывает на сервер
#   2. Порт 80 открыт
#   3. docker-compose.yml и nginx.conf уже обновлены на cryptoterm.ru
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

DOMAIN="cryptoterm.ru"
EMAIL="kirill_lesnyak@mail.ru"
CERT_DIR="./certbot/conf/live/$DOMAIN"

echo "=== SSL-инициализация для $DOMAIN ==="
echo ""

# 1. Создаём временный самоподписанный сертификат, чтобы nginx мог стартовать
if [ ! -f "$CERT_DIR/fullchain.pem" ]; then
    echo "==> Создаём временный самоподписанный сертификат..."
    mkdir -p "$CERT_DIR"
    openssl req -x509 -nodes -newkey rsa:2048 -days 1 \
        -keyout "$CERT_DIR/privkey.pem" \
        -out "$CERT_DIR/fullchain.pem" \
        -subj "/CN=$DOMAIN" 2>/dev/null
    echo "    Временный сертификат создан."
else
    echo "==> Сертификат уже существует, пропускаем создание временного."
fi

# 2. Запускаем nginx (с временным сертификатом)
echo ""
echo "==> Запускаем nginx-proxy..."
docker-compose up -d nginx-proxy
sleep 3

# 3. Проверяем, что HTTP-challenge доступен
echo ""
echo "==> Проверяем доступность домена..."
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "http://$DOMAIN/.well-known/acme-challenge/" 2>/dev/null || echo "000")
if [ "$HTTP_CODE" = "000" ]; then
    echo "    ВНИМАНИЕ: Не удалось подключиться к http://$DOMAIN"
    echo "    Убедитесь, что DNS настроен и порт 80 открыт."
    echo "    Продолжаем попытку..."
fi

# 4. Удаляем временный сертификат перед запросом настоящего
echo ""
echo "==> Удаляем временный сертификат..."
rm -rf "./certbot/conf/live/$DOMAIN"
rm -rf "./certbot/conf/archive/$DOMAIN"
rm -f "./certbot/conf/renewal/$DOMAIN.conf"

# 5. Выпускаем настоящий сертификат
echo ""
echo "==> Выпускаем сертификат Let's Encrypt для $DOMAIN..."
docker-compose run --rm certbot certonly \
    --webroot \
    --webroot-path /var/www/certbot \
    --email "$EMAIL" \
    --agree-tos \
    --no-eff-email \
    -d "$DOMAIN"

# 6. Перезагружаем nginx с настоящим сертификатом
echo ""
echo "==> Перезагружаем nginx с новым сертификатом..."
docker-compose exec nginx-proxy nginx -s reload

echo ""
echo "✓ Готово! Сайт доступен по https://$DOMAIN"
echo "  Сертификат будет действителен 90 дней."
echo "  Для автопродления добавьте в crontab:"
echo "  0 3 * * 1 cd $(pwd) && docker-compose run --rm certbot renew && docker-compose exec nginx-proxy nginx -s reload"
