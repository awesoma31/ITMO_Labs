from src.floatCondition.floatConditionImpl import FloatConditionFabric
from src.condition.Condition import MetricCondition
from src.registrys.CommonRegistry import CommonRegistry

class MetricConditionImpl(MetricCondition):
    def __init__(self, reg: CommonRegistry, id_: str) -> None:
        self.reg = reg
        self.id = id_
        desc = self.__getDescription()
        self.strMethod = desc["type"]
        self.condition = FloatConditionFabric(self.strMethod)

    def getRequestId(self):
        desc = self.__getDescription()
        return desc["input"]

    def getId(self):
        return self.id

    def checkMetricCondition(self, metrics: dict[str, float]) -> bool:
        desc = self.__getDescription()
        if desc["type"] != self.strMethod:
            self.condition = FloatConditionFabric(self.strMethod)
        range_ = float(desc["value"])
        return self.condition.eval(metrics[desc["metricName"]], range_)

    def __getDescription(self) -> dict[str, str]:
        return self.reg.getParameterByPath(f"conditions.{self.id}")