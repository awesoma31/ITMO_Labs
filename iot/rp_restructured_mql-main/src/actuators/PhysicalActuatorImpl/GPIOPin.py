import RPi.GPIO as GPIO
import time

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class GPIOPin:
    # Маппинг физических пинов в GPIO (BCM)
    PHYSICAL_TO_GPIO = {
        3: 2, 5: 3, 7: 4, 8: 14, 10: 15,
        11: 17, 12: 18, 13: 27, 15: 22, 16: 23,
        18: 24, 19: 10, 21: 9, 22: 25, 23: 11,
        24: 8, 26: 7, 27: 0, 28: 1, 29: 5,
        31: 6, 32: 12, 33: 13, 35: 19, 36: 16,
        37: 26, 38: 20, 40: 21
    }

    def __init__(self, physical_pin):
        if physical_pin not in self.PHYSICAL_TO_GPIO:
            valid_pins = sorted(self.PHYSICAL_TO_GPIO.keys())
            raise ValueError(f"Физический пин {physical_pin} не является GPIO. "
                             f"Доступные GPIO пины: {valid_pins}")

        self.physical_pin = physical_pin
        self.gpio_pin = self.PHYSICAL_TO_GPIO[physical_pin]
        self.is_initialized = False
        self.__logger = get_logger()

    def initialize(self):
        if not self.is_initialized:
            GPIO.setmode(GPIO.BCM)
            GPIO.setup(self.gpio_pin, GPIO.OUT)
            GPIO.output(self.gpio_pin, GPIO.LOW)
            self.is_initialized = True
            if self.__logger:
                self.__logger.info(f"Initialized GPIO pin {self.physical_pin} → GPIO {self.gpio_pin}")

    def turn_on(self):
        if not self.is_initialized:
            self.initialize()
        GPIO.output(self.gpio_pin, GPIO.HIGH)

    def turn_off(self):
        if not self.is_initialized:
            self.initialize()
        GPIO.output(self.gpio_pin, GPIO.LOW)

    def set_state(self, state):
        if not self.is_initialized:
            self.initialize()
        GPIO.output(self.gpio_pin, GPIO.HIGH if state else GPIO.LOW)
        if self.__logger:
            self.__logger.debug(f"GPIO pin {self.gpio_pin} set to: {'HIGH' if state else 'LOW'}")

    def blink(self, times=1, on_time=0.5, off_time=0.5):
        if not self.is_initialized:
            self.initialize()

        for _ in range(times):
            self.turn_on()
            time.sleep(on_time)
            self.turn_off()
            time.sleep(off_time)

    def cleanup(self):
        if self.is_initialized:
            GPIO.output(self.gpio_pin, GPIO.LOW)
            GPIO.cleanup(self.gpio_pin)
            self.is_initialized = False

    def __del__(self):
        # ! так пины не сбрасываются и остаются выключенными при вызове диструктора, rp будет жаловаться, что они используются, но схавает
        # self.cleanup()
        pass

    def __enter__(self):
        self.initialize()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        # self.cleanup()
        pass