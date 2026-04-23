from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.registrys.CommonRegistry import CommonRegistry
from src.metricProviders.physicalMetricProviderImpl.TempPinApiImpl import read_temperatures

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class TemperatureSensorImpl(PhysicalMetricProvider):

    ARDUINO_AVAILABLE_PINS = ["A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7"]

    def __init__(self, id: str, reg: CommonRegistry):
        self.id = id
        self.reg = reg
        self.__logger = get_logger()

    def getId(self) -> str:
        return self.id

    def provideMetric(self) -> dict[str, float]:
        desc = self.reg.getParameterByPath(f"metricProviders.{self.id}")
        pin = desc["pin"]

        if pin not in TemperatureSensorImpl.ARDUINO_AVAILABLE_PINS:
            error_msg = f"Invalid pin for TemperatureSensorImpl: {pin}"
            if self.__logger:
                self.__logger.error(error_msg)
            raise ConnectionError(error_msg)

        if self.__logger:
            self.__logger.debug(f"Reading temperature from pin {pin}")

        try:
            temperature = {"tempC": read_temperatures(pin)}
            if self.__logger:
                self.__logger.debug(f"Temperature sensor {self.id} reading: {temperature}")
            return temperature
        except Exception as e:
            if self.__logger:
                self.__logger.error(f"Failed to read temperature from pin {pin}: {e}")
            raise