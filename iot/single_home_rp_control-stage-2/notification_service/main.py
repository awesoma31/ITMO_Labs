import yaml
import argparse
from flask import Flask, jsonify
import time
import threading
import RPi.GPIO as GPIO


class SimpleLEDController:
    PHYSICAL_TO_GPIO = {
        3: 2, 5: 3, 7: 4, 8: 14, 10: 15,
        11: 17, 12: 18, 13: 27, 15: 22, 16: 23,
        18: 24, 19: 10, 21: 9, 22: 25, 23: 11,
        24: 8, 26: 7, 27: 0, 28: 1, 29: 5,
        31: 6, 32: 12, 33: 13, 35: 19, 36: 16,
        37: 26, 38: 20, 40: 21
    }
    def __init__(self, pin: int):
        if pin not in self.PHYSICAL_TO_GPIO:
            valid_pins = sorted(self.PHYSICAL_TO_GPIO.keys())
            raise ValueError(f"Физический пин {pin} не является GPIO. "
                             f"Доступные GPIO пины: {valid_pins}")
        self.pin = self.PHYSICAL_TO_GPIO[pin]
        self.state = "OFF"
        self.blink_thread = None
        self.stop_blinking = False

        # Настройка GPIO
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.pin, GPIO.OUT)
        GPIO.output(self.pin, GPIO.LOW)

        self.blink()

        print(f"🎮 Контроллер светодиода на пине {pin} готов")

    def turn_on(self):
        self._stop_blinking()
        GPIO.output(self.pin, GPIO.HIGH)
        self.state = "ON"
        print("💡 СВЕТОДИОД ВКЛЮЧЕН")
        return {"status": "success", "message": "LED ON", "pin": self.pin}

    def turn_off(self):
        self._stop_blinking()
        GPIO.output(self.pin, GPIO.LOW)
        self.state = "OFF"
        print("🔌 СВЕТОДИОД ВЫКЛЮЧЕН")
        return {"status": "success", "message": "LED OFF", "pin": self.pin}

    def blink(self):
        self._stop_blinking()
        self.state = "BLINKING"
        self.stop_blinking = False

        def blink_forever():
            blink_count = 0
            while not self.stop_blinking:
                blink_count += 1
                GPIO.output(self.pin, GPIO.HIGH)
                print(f"✨ Мигание {blink_count} - ВКЛ")
                time.sleep(0.5)
                if self.stop_blinking:
                    break
                GPIO.output(self.pin, GPIO.LOW)
                print(f"✨ Мигание {blink_count} - ВЫКЛ")
                time.sleep(0.5)
            # Гарантируем выключение при остановке
            GPIO.output(self.pin, GPIO.LOW)
            print("🛑 Мигание остановлено")

        self.blink_thread = threading.Thread(target=blink_forever)
        self.blink_thread.daemon = True
        self.blink_thread.start()

        print("🔁 ЗАПУЩЕНО БЕСКОНЕЧНОЕ МИГАНИЕ")
        return {"status": "success", "message": "LED BLINKING FOREVER", "pin": self.pin}

    def _stop_blinking(self):
        """Останавливает мигание при смене состояния"""
        if self.blink_thread and self.blink_thread.is_alive():
            self.stop_blinking = True
            self.blink_thread.join(timeout=1.0)
            self.stop_blinking = False

    def cleanup(self):
        """Очистка GPIO"""
        self._stop_blinking()
        GPIO.cleanup(self.pin)
        print("🧹 GPIO очищен")


def load_config(config_path: str) -> dict:
    with open(config_path, 'r') as file:
        return yaml.safe_load(file)


def main():
    # parser = argparse.ArgumentParser(description='Сервис управления светодиодом для Raspberry Pi')
    # parser.add_argument('config_path', help='Путь к YAML конфигурационному файлу')
    # args = parser.parse_args()

    # Загружаем конфиг
    config = load_config("/etc/cryptoterm/config/system-config.yaml")
    ns = config['notification_service']

    print("⚙️  Конфиг загружен:")
    print(f"   Пин: {ns['pin']}")
    print(f"   Порт: {ns['port']}")
    print(f"   Эндпоинты: {ns['on_endpoint']}, {ns['off_endpoint']}, {ns['blink_endpoint']}")

    # Создаем контроллер
    led = SimpleLEDController(ns['pin'])

    # Создаем Flask приложение
    app = Flask(__name__)

    @app.route(ns['on_endpoint'], methods=['GET', 'POST', 'PUT'])
    def on_endpoint():
        print("🎯 Запрос на ВКЛЮЧЕНИЕ светодиода")
        result = led.turn_on()
        return jsonify(result)

    @app.route(ns['off_endpoint'], methods=['GET', 'POST', 'PUT'])
    def off_endpoint():
        print("🎯 Запрос на ВЫКЛЮЧЕНИЕ светодиода")
        result = led.turn_off()
        return jsonify(result)

    @app.route(ns['blink_endpoint'], methods=['GET', 'POST', 'PUT'])
    def blink_endpoint():
        print("🎯 Запрос на МИГАНИЕ светодиода")
        result = led.blink()
        return jsonify(result)

    @app.route('/status', methods=['GET'])
    def status():
        return jsonify({"state": led.state, "pin": led.pin})

    @app.route('/')
    def index():
        return f"""
        <h1>Сервис управления светодиодом</h1>
        <p>Текущий статус: <b>{led.state}</b></p>
        <p>GPIO пин: <b>{led.pin}</b></p>
        <p>Доступные команды:</p>
        <ul>
            <li><a href="{ns['on_endpoint']}">ВКЛЮЧИТЬ</a></li>
            <li><a href="{ns['off_endpoint']}">ВЫКЛЮЧИТЬ</a></li>
            <li><a href="{ns['blink_endpoint']}">МИГАТЬ (бесконечно)</a></li>
            <li><a href="/status">СТАТУС</a></li>
        </ul>
        <p><i>Мигание будет продолжаться до вызова ВКЛ/ВЫКЛ</i></p>
        """

    print(f"\n🚀 Сервер запущен: http://localhost:{ns['port']}")
    print("📢 Теперь можно отправлять запросы GET/POST/PUT на эндпоинты:")
    print(f"   {ns['on_endpoint']} - включить")
    print(f"   {ns['off_endpoint']} - выключить")
    print(f"   {ns['blink_endpoint']} - мигать БЕСКОНЕЧНО")
    print("\n💡 Мигание будет продолжаться до вызова включения/выключения")
    print("⏹️  Нажмите Ctrl+C для остановки сервера\n")

    try:
        app.run(host='0.0.0.0', port=ns['port'])
    except KeyboardInterrupt:
        print("\n🛑 Остановка сервера...")
    finally:
        led.cleanup()
        print("✅ Сервер остановлен, GPIO очищен")


if __name__ == "__main__":
    main()