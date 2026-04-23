import requests

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class LedNotificator:

    def __init__(self, port):
        self.host = "localhost"
        self.port = port
        self.__logger = get_logger()

    def on(self):
        self.__control_led("/on")

    def off(self):
        self.__control_led("/off")

    def blink(self):
        self.__control_led("/blink")

    def __control_led(self, action):
        base_url = f"http://{self.host}:{self.port}"

        url = base_url + action

        try:
            response = requests.post(url)
            if response.status_code == 200:
                if self.__logger:
                    self.__logger.debug(f"LED {action}: {response.json()}")
            else:
                if self.__logger:
                    self.__logger.warning(f"LED control error: {response.status_code}")
        except Exception as e:
            if self.__logger:
                self.__logger.error(f"Failed to send LED control request: {e}")