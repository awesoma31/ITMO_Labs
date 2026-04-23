import pytest
from src.condition.Condition import MetricCondition

# just make sure, I ain't fucked up with interface

class MetricConditionMock(MetricCondition):

    def __init__(self):
        self.requestId = "asd"
        self.id = "asd1"
        self.lastMetric = {}

    def checkMetricCondition(self, metrics: dict[str, float]) -> bool:
        self.lastMetric = metrics
        return True

    def getId(self):
        return self.id

    def getRequestId(self):
        return self.requestId

def test_straigthForwardConditionInterfaceTest():
    mc = MetricConditionMock()
    _id = mc.getRequestId()
    _id = mc.getId()
    mc.checkMetricCondition({"metric": 1})
    assert mc.lastMetric["metric"] == 1


if __name__ == "__main__":
    pytest.main([__file__])

