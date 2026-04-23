from src.aggregator.AggregationMethod import AggregationMethod
from src.exceptions.ConfigError import ConfigError


class AggregationMethodAvg(AggregationMethod):
    def AggregateMetrics(self, metrics: list[float]) -> float:
        return sum(metrics) / float(len(metrics))

class AggregationMethodMin(AggregationMethod):
    def AggregateMetrics(self, metrics: list[float]) -> float:
        return min(metrics)

class AggregationMethodMax(AggregationMethod):
    def AggregateMetrics(self, metrics: list[float]) -> float:
        return max(metrics)
    
def AggregationMethodFabric(method: str) -> AggregationMethod:
    method = method.lower()
    if method == "min":
        return AggregationMethodMin()
    elif method == "max":
        return AggregationMethodMax()
    elif method == "avg":
        return AggregationMethodAvg()
    raise ConfigError("No such aggregation method: {}".format(method))