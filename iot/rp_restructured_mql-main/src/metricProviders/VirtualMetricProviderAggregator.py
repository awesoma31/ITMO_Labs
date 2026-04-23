from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProvider
from src.metricProviders.AbsVirtualMetricProvider import AbstractMetricProviderWrapper
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProviderCacheDroppable
from src.aggregator.AggregationMethodImpl import AggregationMethodFabric, AggregationMethod
from src.exceptions.DeveloperError import DeveloperError

class VirtualMetricProviderAggregator(AbstractMetricProviderWrapper):
    """!
    @brief Documentation for the class VirtualMetricProvider.

    this is a wrapper for the PhysicalMetricProvider class list.
    it caches last recieved metrics. To recieve new once, you have
    to drop the cached metrics first.
    and gives you aggregated metrics
    """

    def __init__(
            self,
            pmpList: list[AbstractVirtualMetricProvider],
            aggrMethod: str,
            metricName: str,
            id: str
    ):
        self.pmpList: list[AbstractVirtualMetricProvider] = pmpList
        self.aggregator: AggregationMethod = AggregationMethodFabric(aggrMethod)
        self.metricName: str = metricName
        self.id = id

    def getId(self) -> str:
        return self.id

    def dropCache(self):
        raise DeveloperError(f"Do not drop cache from wrapper!")

    def getCacheDroppableVirtualMetricProviderInstances(self) -> list[AbstractVirtualMetricProviderCacheDroppable]:
        result: list[AbstractVirtualMetricProviderCacheDroppable] = []
        for pmp in self.pmpList:
            if isinstance(pmp, AbstractVirtualMetricProviderCacheDroppable):
                result.append(pmp)
            elif isinstance(pmp, AbstractMetricProviderWrapper):
                result += pmp.getCacheDroppableVirtualMetricProviderInstances()
        return result

    def recieveMetric(self):
        return self.__provideMetrics()

    def __provideMetrics(self) -> dict[str, float]:
        metricValues: list[float] = self.__provideMetricsValues__()
        aggregatedMetrics: float = self.aggregator.AggregateMetrics(metricValues)
        return {self.metricName: aggregatedMetrics}

    def __provideMetricsValues__(self) -> list[float]:
        metricValues = []
        for pmp in self.pmpList:
            tmpMetric = pmp.recieveMetric()
            if self.metricName not in tmpMetric:
                raise DeveloperError(f"metric {self.metricName} was not provided")
            metricValues.append(tmpMetric[self.metricName])
        return metricValues
