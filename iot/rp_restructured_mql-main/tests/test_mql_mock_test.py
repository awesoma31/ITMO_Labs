import pytest
from typing import Callable

from src.alerts.AlertChecker import AlertChecker
from src.booleanConditionGraph.BooleanGraph import BooleanGraph
from src.booleanConditionGraph.BooleanNode import BooleanNode
from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.condition.Condition import MetricCondition
from src.actuators.PhysicalActuator import PhysicalActuator
from src.metricProviders.VirtualMetricProviderAggregator import VirtualMetricProviderAggregator
from src.metricProviders.VirtualMetricProviderImpl import VirtualMetricProviderImpl
from src.mql import MQL

class PhysicalMetricProviderMock(PhysicalMetricProvider):
    def __init__(self, testData: list[dict[str, float]]):
        self.testData = testData
        self.ptr = 0

    def provideMetric(self) -> dict[str, float]:
        if self.ptr > (len(self.testData) - 1):
            raise ValueError("provider metric error")
        metric = self.testData[self.ptr]
        self.ptr += 1
        return metric

class AlertMock(AlertChecker):

    def __init__(self, reqId: str, condition: Callable[[dict[str, float]], bool]):
        self.reqId = reqId
        self.condition = condition
        self.alertMetrics = []

    def getRequestId(self) -> str:
        return self.reqId

    def check(self, metric: dict[str, float]):
        if self.condition(metric):
            self.alertMetrics.append(metric.copy())

class ConditionMock(MetricCondition):
    def __init__(
            self,
            reqId: str,
            id: str,
            condition: Callable[[dict[str, float]], bool],
    ):
        self.reqId = reqId
        self.id = id
        self.condition = condition
        self.workedMetrics = []

    def getRequestId(self) -> str:
        return self.reqId

    def getId(self) -> str:
        return self.id

    def checkMetricCondition(self, metric: dict[str, float]) -> bool:
        if self.condition(metric):
            self.workedMetrics.append(metric.copy())
            return True
        return False

class ActuatorMock(PhysicalActuator):

    def __init__(self, reqId: str):
        self.reqId = reqId
        self.states = []

    def getRequestId(self) -> str:
        return self.reqId

    def setState(self, metric: bool) -> None:
        self.states.append(metric)

def buildGraph() -> BooleanGraph:
    andNode = BooleanNode("and", ["cd1", "cd2"], "g1")
    noneNode = BooleanNode("none", ["cd3"], "g2")
    orNode = BooleanNode("or", ["casic", "cagg"], "g3")
    return BooleanGraph([andNode, noneNode, orNode])

def buildCondGraph() -> BooleanGraph:
    andNode = BooleanNode("and", ["cd1", "cd2"], "g1")
    return BooleanGraph([andNode])

def condLT10(metric: dict[str, float]) -> bool: return metric["tempC"] < 10
def condMT70(metric: dict[str, float]) -> bool: return metric["tempC"] > 70
def condLT100(metric: dict[str, float]) -> bool: return metric["tempC"] < 100
def condMT100(metric: dict[str, float]) -> bool: return metric["tempC"] > 100
def condMT10(metric: dict[str, float]) -> bool: return metric["tempC"] > 10
def condMT80(metric: dict[str, float]) -> bool: return metric["tempC"] > 80
def condEQ0(metric: dict[str, float]) -> bool: return metric["hr"] == 0
def condMT60(metric: dict[str, float]) -> bool: return metric["tempC"] > 60
def condMT30(metric: dict[str, float]) -> bool: return metric["tempC"] > 30
def condMT0(metric: dict[str, float]) -> bool: return metric["tempC"] > 0

def test_complex_mql_test():
    physPin1 = PhysicalMetricProviderMock([{"tempC": 80}, {"tempC": 5}])
    physPin2 = PhysicalMetricProviderMock([{"tempC": 90}, {"tempC": 110}])
    physPin3 = PhysicalMetricProviderMock([{"tempC": 100}, {"tempC": 5}])
    asicMock = PhysicalMetricProviderMock([{"tempC": 90, "hr": 10}, {"tempC": 50, "hr": 0}])

    d4 = PhysicalMetricProviderMock([{"tempC": 30}, {"tempC": 4}])
    d5 = PhysicalMetricProviderMock([{"tempC": 51}, {"tempC": 5}])
    d6 = PhysicalMetricProviderMock([{"tempC": 10}, {"tempC": 6}])

    v1 = VirtualMetricProviderImpl(physPin1, "d1")
    v2 = VirtualMetricProviderImpl(physPin2, "d2")
    v3 = VirtualMetricProviderImpl(physPin3, "d3")
    asicV = VirtualMetricProviderImpl(asicMock, "asic")
    vagg = VirtualMetricProviderAggregator([
        VirtualMetricProviderImpl(d4, "d4"),
        VirtualMetricProviderImpl(d5, "d5"),
        VirtualMetricProviderImpl(d6, "d6"),
    ], "avg", "tempC", "dagg")

    a1 = AlertMock("d1", condLT10)
    a2 = AlertMock("d2", condMT100)
    aAsic = AlertMock("asic", condMT80)
    aAsicHr0 = AlertMock("asic", condEQ0)

    cd1 = ConditionMock("d1", "cd1", condMT70)
    cd2 = ConditionMock("d2", "cd2", condLT100)
    cd3 = ConditionMock("d3", "cd3", condMT10)
    asicC = ConditionMock("asic", "casic", condMT60)
    aggC = ConditionMock("dagg", "cagg", condMT30)

    act1 = ActuatorMock("g1")
    act2 = ActuatorMock("g2")
    act3 = ActuatorMock("g3")

    graph = buildGraph()

    mql_ = MQL(
        [v1, v2, v3, vagg, asicV],
        [a1, a2, aAsic, aAsicHr0],
        [cd1, cd2, cd3, asicC, aggC],
        graph,
        [act1, act2, act3]
    )

    mql_.run(0, 2)

    assert len(a1.alertMetrics) == 1
    assert len(a2.alertMetrics) == 1
    assert len(aAsic.alertMetrics) == 1
    assert len(aAsicHr0.alertMetrics) == 1

    assert a1.alertMetrics[0]["tempC"] == 5
    assert a2.alertMetrics[0]["tempC"] == 110
    assert aAsic.alertMetrics[0]["tempC"] == 90
    assert aAsicHr0.alertMetrics[0]["hr"] == 0

    assert len(cd1.workedMetrics) == 1
    assert len(cd2.workedMetrics) == 1
    assert len(cd3.workedMetrics) == 1
    assert len(asicC.workedMetrics) == 1
    assert len(aggC.workedMetrics) == 1

    assert cd1.workedMetrics[0]["tempC"] == 80
    assert cd2.workedMetrics[0]["tempC"] == 90
    assert cd3.workedMetrics[0]["tempC"] == 100
    assert asicC.workedMetrics[0]["tempC"] == 90
    assert aggC.workedMetrics[0]["tempC"] == 30.333333333333332

    assert act1.states[0] == True
    assert act1.states[1] == False
    assert act2.states[0] == True
    assert act2.states[1] == False
    assert act3.states[0] == True
    assert act3.states[1] == False

def test_range_condition():
    d1 = PhysicalMetricProviderMock([{"tempC": 5}, {"tempC": 15}])
    v1 = VirtualMetricProviderImpl(d1, "d1")
    c1 = ConditionMock("d1", "cd1", condLT10)
    c2 = ConditionMock("d1", "cd2", condMT0)

    act1 = ActuatorMock("g1")

    graph = buildCondGraph()

    mql = MQL(
        [v1],
        [],
        [c1, c2],
        graph,
        [act1]
    )

    mql.run(0, 2)

    assert len(c1.workedMetrics) == 1
    assert len(c2.workedMetrics) == 2
    assert c1.workedMetrics[0]["tempC"] == 5
    assert c2.workedMetrics[0]["tempC"] == 5
    assert c2.workedMetrics[1]["tempC"] == 15

    assert act1.states[0] == True
    assert act1.states[1] == False

def test_empty_graph():
    d1 = PhysicalMetricProviderMock([{"tempC": 5}, {"tempC": 15}])
    v1 = VirtualMetricProviderImpl(d1, "d1")
    c1 = ConditionMock("d1", "cd1", condLT10)
    act1 = ActuatorMock("cd1")

    emptyGraph = BooleanGraph([])

    mql = MQL(
        [v1],
        [],
        [c1],
        emptyGraph,
        [act1]
    )

    mql.run(0, 2)

    assert len(c1.workedMetrics) == 1
    assert c1.workedMetrics[0]["tempC"] == 5

    assert act1.states[0] == True
    assert act1.states[1] == False

if __name__ == "__main__":
    pytest.main([__file__])