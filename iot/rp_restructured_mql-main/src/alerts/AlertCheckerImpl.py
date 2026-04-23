from src.floatCondition.floatConditionImpl import FloatConditionFabric
from src.alerts.AlertChecker import AlertChecker
from src.registrys.CommonRegistry import CommonRegistry
from src.alerts.TelegramApiImpl import send_telegram

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None


class AlertCheckerByCondition(AlertChecker):
    def __init__(self, reg: CommonRegistry, id_: str) -> None:
        self.reg = reg
        self.id = id_
        desc = self.__getDescription()
        self.strMethod = desc["type"]
        self.condition = FloatConditionFabric(self.strMethod)
        self.isCriticalState = False
        self.__logger = get_logger()
        self.nameMap = self.reg.getParameterByPath(f"nameMap")

        if self.__logger:
            self.__logger.info(f"Alert checker {id_} initialized with condition type: {self.strMethod}")

    def getRequestId(self):
        desc = self.__getDescription()
        return desc["input"]

    def check(self, metrics: dict[str, float]) -> None:
        desc = self.__getDescription()
        if desc["type"] != self.strMethod:
            self.condition = FloatConditionFabric(self.strMethod)
            if self.__logger:
                self.__logger.debug(f"Alert {self.id} condition type changed to: {desc['type']}")

        range_ = float(desc["value"])
        metric_value = metrics[desc["metricName"]]

        if self.condition.eval(metric_value, range_):
            if not self.isCriticalState:
                self.isCriticalState = True
                if self.__logger:
                    self.__logger.warning(f"Alert {self.id} triggered! Metric '{desc['metricName']}' = {metric_value}, threshold = {range_}")
                self.__sendAlert(metrics)
        else:
            if self.isCriticalState:
                if self.__logger:
                    self.__logger.info(f"Alert {self.id} resolved. Metric '{desc['metricName']}' = {metric_value}")
            self.isCriticalState = False

    def __getDescription(self) -> dict[str, str]:
        return self.reg.getParameterByPath(f"alerts.{self.id}")

    def __getStrDescription(self, metric: dict[str, float]) -> str:
        desc = self.reg.getParameterByPath(f"alerts.{self.id}")
        return (f"input: {self.nameMap[desc['input']]}\nmetricName: {desc['metricName']}"
                f"\nevaluated_condition:{metric[desc['metricName']]}{desc['type']}{desc['value']}")

    def __sendAlert(self, metric: dict[str, float]) -> None:
        alertName = self.reg.getParameterByPath(f"nameMap.{self.id}")
        send_telegram(f"alerts.{alertName} ALERT: {self.__getStrDescription(metric)}")