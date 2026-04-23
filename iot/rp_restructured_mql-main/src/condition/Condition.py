from abc import abstractmethod
from src.commonObjects.IRequestIdObject import IRequestIdObject
from src.commonObjects.IIdObject import IIdObject

class MetricCondition(IIdObject, IRequestIdObject):

    @abstractmethod
    def checkMetricCondition(self, metrics: dict[str, float]) -> bool:
        pass