from abc import ABC, abstractmethod

class AggregationMethod(ABC):
    """!
    @brief AggregationMethod
    basic class for metric aggregation metrics like avg, min, max and etc
    """
    @abstractmethod
    def AggregateMetrics(self, metrics: list[float]) -> float:
        pass