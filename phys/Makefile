# Название бинарника
BIN := phys-army

# Основной файл
SRC := army.go

# Цель по умолчанию — build and run
.PHONY: all
all: run

# Компиляция
build:
	@echo "==> Building $(BIN)..."
	@go build -o $(BIN) $(SRC)

# Запуск (с пересборкой)
run: build
	@echo "==> Running $(BIN)..."
	@./$(BIN)

# Очистка
clean:
	@echo "==> Cleaning..."
	@rm -f $(BIN)
