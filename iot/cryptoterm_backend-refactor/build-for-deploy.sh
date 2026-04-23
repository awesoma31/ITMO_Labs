#!/bin/bash

# Full Backend Deploy Script
# Собирает JAR локально и показывает команды для деплоя

set -e

echo "🔨 Сборка backend..."
cd /Users/kirilllesniak/projects/cryptoterm_backend

# Компилируем и запускаем быстрые тесты
echo "✅ Компиляция..."
mvn clean compile

echo "🧪 Запуск тестов..."
mvn test -Dtest=AntminerS19ProHydroCommandFactoryTest,CommandTypeTest,PowerModeTest

# Собираем JAR
echo "📦 Сборка JAR..."
mvn package -DskipTests

JAR_FILE=$(ls -t target/backend-*.jar | head -1)
echo "✅ JAR собран: $JAR_FILE"

# Показываем размер
ls -lh "$JAR_FILE"

echo ""
echo "========================================="
echo "📋 ИНСТРУКЦИИ ДЛЯ ДЕПЛОЯ НА СЕРВЕР"
echo "========================================="
echo ""
echo "1️⃣  Скопируйте JAR на сервер:"
echo "scp $JAR_FILE your-server:/path/to/cryptoterm_backend/"
echo ""
echo "2️⃣  На сервере перезапустите контейнер:"
echo "ssh your-server 'cd /path/to/cryptoterm_backend && docker-compose restart backend'"
echo ""
echo "3️⃣  Проверьте логи:"
echo "ssh your-server 'docker logs -f cryptoterm-backend --tail=100 | grep -i \"пропускаем проверку производителя\"'"
echo ""
echo "========================================="
echo "✅ Локальная сборка завершена!"
echo "========================================="
