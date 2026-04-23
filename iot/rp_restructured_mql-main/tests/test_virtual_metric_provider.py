import pytest
from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.metricProviders.VirtualMetricProviderAggregator import VirtualMetricProviderAggregator
from src.metricProviders.VirtualMetricProviderImpl import VirtualMetricProviderImpl

class MetricProviderMock(PhysicalMetricProvider):

    def __init__(self, count: int):
        self.metrics = [i for i in range(count)]
        self.counter = 0
        self.askedMetrics = 0

    def getId(self):
        return "mpv"

    def getAskedMetrics(self) -> int:
        return self.askedMetrics

    # to ignore test setup
    def dropAskedMetrics(self) -> None:
        self.askedMetrics = 0

    def provideMetric(self):
        self.askedMetrics += 1
        if self.counter > (len(self.metrics) - 1):
            raise ValueError("too much accesses for mock")
        metric = self.metrics[self.counter]
        self.counter += 1
        return {"metric": metric}

    def __getId(self):
        return "mpv"

def generateMetricProviders(count: int, metricCount: int) -> list[MetricProviderMock]:
    ret = []
    for i in range(count):
        ret.append(MetricProviderMock(metricCount))
    return ret

def displaceMetrics(mpv: list[MetricProviderMock], disped: int) -> None:
    for ind, prov in enumerate(mpv):
        for j in range(ind):
            prov.provideMetric()
        prov.dropAskedMetrics()

def dropAggCache(virtProvAgg: VirtualMetricProviderAggregator) -> None:
    for prov in virtProvAgg.getCacheDroppableVirtualMetricProviderInstances():
        prov.dropCache()

def runCommonAggregationTest(method: str, expected1: int, expected2: int) -> None:
    providers = generateMetricProviders(5, 20)
    displaceMetrics(providers, 5)
    virtualMetricProviders = [
        VirtualMetricProviderImpl(prov, str(i))
        for i, prov in enumerate(providers)
    ]
    virtualMetricsProvider = VirtualMetricProviderAggregator(
        virtualMetricProviders,
        method,
        "metric",
        "mpv")
    dropAggCache(virtualMetricsProvider)
    metrics = [virtualMetricsProvider.recieveMetric() for _ in range(5)]
    assert [i["metric"] for i in metrics].count(expected1) == 5
    dropAggCache(virtualMetricsProvider)
    metrics = [virtualMetricsProvider.recieveMetric() for _ in range(5)]
    assert [i["metric"] for i in metrics].count(expected2) == 5

    for i in providers:
        assert i.getAskedMetrics() == 2

def test_aggregated_min():
    runCommonAggregationTest("min", 0, 1)

def test_aggregated_max():
    runCommonAggregationTest("max", 4, 5)

def test_aggregated_avg():
    runCommonAggregationTest("avg", 2, 3)

def test_simple_virtual_metric_provider():
    providers = generateMetricProviders(5, 20)
    displaceMetrics(providers, 5)
    virtualMetricProviders = [VirtualMetricProviderImpl(prov, "mpv") for prov in providers]
    expected1 = [0, 1, 2, 3, 4]
    expected2 = [1, 2, 3, 4, 5]
    for provider in virtualMetricProviders:
        provider.dropCache()

    # checking cache
    for i in range(5):
        res = []
        for prov in virtualMetricProviders:
            res.append(prov.recieveMetric())
        assert len(res) == len(expected1)
        for j in range(len(res)):
            assert res[j]["metric"] == expected1[j]

    # recieving new metrics
    for provider in virtualMetricProviders:
        provider.dropCache()

    # checking cache
    for i in range(5):
        res = []
        for prov in virtualMetricProviders:
            res.append(prov.recieveMetric())
        assert len(res) == len(expected2)
        for j in range(len(res)):
            assert res[j]["metric"] == expected2[j]

if __name__ == "__main__":
    pytest.main([__file__])