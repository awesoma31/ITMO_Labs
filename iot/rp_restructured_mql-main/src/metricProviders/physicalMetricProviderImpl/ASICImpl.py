from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.registrys.CommonRegistry import CommonRegistry
from src.metricProviders.physicalMetricProviderImpl.ASICApiImpl import getMetric

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class ASICImpl(PhysicalMetricProvider):
    def __init__(self, id: str, reg: CommonRegistry):
        self.id = id
        self.reg = reg
        self.__logger = get_logger()

    def getId(self) -> str:
        return self.id

    def provideMetric(self) -> dict[str, float]:
        desc = self.reg.getParameterByPath(f"metricProviders.{self.id}")
        address = desc["address"]
        pw = desc["password"]
        url = f"http://{address}/api/v1"

        if self.__logger:
            self.__logger.debug(f"Fetching ASIC metrics from {address}")

        try:
            metric = getMetric(url, pw)
            if self.__logger:
                self.__logger.debug(f"ASIC {self.id} metrics: {metric}")
            return metric
        except Exception as e:
            if self.__logger:
                self.__logger.error(f"Failed to get ASIC metrics from {address}: {e}")
            raise