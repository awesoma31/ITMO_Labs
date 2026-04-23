# Остановите приложение, которое использует базу (если есть)
sudo docker stop cryptoterm_app_container

# Восстановите в новую временную базу для проверки
sudo docker exec cryptoterm_timescaledb createdb -U cryptoterm test_restore

# Восстановите бекап
sudo docker exec -i cryptoterm_timescaledb pg_restore \
  -U cryptoterm \
  -d test_restore \
  --verbose < $1

# Проверьте восстановленные таблицы
sudo docker exec cryptoterm_timescaledb psql -U cryptoterm -d test_restore -c "\dt"
