from abc import abstractmethod
from src.commonObjects.IIdObject import IIdObject

class AbstractVirtualMetricProvider(IIdObject):

    @abstractmethod
    def recieveMetric(self):
        pass

class AbstractVirtualMetricProviderCacheDroppable(AbstractVirtualMetricProvider):

    @abstractmethod
    def dropCache(self):
        pass

class AbstractMetricProviderWrapper(AbstractVirtualMetricProvider):

    @abstractmethod
    def getCacheDroppableVirtualMetricProviderInstances(self) -> list[AbstractVirtualMetricProviderCacheDroppable]:
        pass