from src.metricProviders.AbsVirtualMetricProvider import AbstractMetricProviderWrapper
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProvider
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProviderCacheDroppable
from src.exceptions.DeveloperError import DeveloperError

class VirtualMetricProviderProxy(AbstractMetricProviderWrapper):

    """!
    @brief Documentation for the class VirtualMetricProvider.

    this is a wrapper for the PhysicalMetricProvider class.
    it caches last recieved metrics. To recieve new once, you have
    to drop the cached metrics first.
    """

    def __init__(self, pmp: AbstractVirtualMetricProvider, id: str):
        self.pmp: AbstractVirtualMetricProvider = pmp

    def getId(self) -> str:
        return self.pmp.getId()

    def getCacheDroppableVirtualMetricProviderInstances(self) -> list[AbstractVirtualMetricProviderCacheDroppable]:
        if isinstance(self.pmp, AbstractMetricProviderWrapper):
            return self.pmp.getCacheDroppableVirtualMetricProviderInstances()
        elif isinstance(self.pmp, AbstractVirtualMetricProviderCacheDroppable):
            return [self.pmp]
        raise DeveloperError("got something strange in VirtualMetricProviderProxy")

    def dropCache(self):
        raise DeveloperError("Do not drop cache from wrappers!")

    def recieveMetric(self):
        return self.pmp.recieveMetric()