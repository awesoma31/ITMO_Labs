from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProviderCacheDroppable
from src.exceptions.DeveloperError import DeveloperError

class VirtualMetricProviderImpl(AbstractVirtualMetricProviderCacheDroppable):

    """!
    @brief Documentation for the class VirtualMetricProvider.

    this is a wrapper for the PhysicalMetricProvider class.
    it caches last recieved metrics. To recieve new once, you have
    to drop the cached metrics first.
    """

    def __init__(self, pmp: PhysicalMetricProvider, id: str):
        self.pmp: PhysicalMetricProvider = pmp
        self.cachedMetrics: dict[str, float] = {}
        self.metricsValid: bool = False

    def getId(self) -> str:
        return self.pmp.getId()

    def dropCache(self):
        self.metricsValid = True
        self.cachedMetrics.clear()
        self.cachedMetrics = self.pmp.provideMetric()

    def recieveMetric(self):
        if not self.metricsValid:
            raise DeveloperError("cache for virtual metric provider wasn't properly setted")
        return self.cachedMetrics