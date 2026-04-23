import time

from src.booleanConditionGraph import BooleanGraph
from src.condition.Condition import MetricCondition
from src.exceptions.ConfigError import ConfigError
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProvider, \
    AbstractVirtualMetricProviderCacheDroppable
from src.metricProviders.AbsVirtualMetricProvider import AbstractMetricProviderWrapper
from src.alerts.AlertChecker import AlertChecker
from src.actuators import PhysicalActuator
from src.mqtt.MQTTPublisher import MQTTPublisher

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class MQL:

    def __init__(
            self,
            virtMetricsProviders: list[AbstractVirtualMetricProvider],
            alertList: list[AlertChecker],
            conditionList: list[MetricCondition],
            booleanGraph: BooleanGraph,
            actuatorList: list[PhysicalActuator],
            metricPublisher: MQTTPublisher = None
    ):
        self.__virtMetricsProviders = virtMetricsProviders
        self.__alertList = alertList
        self.__conditionList = conditionList
        self.__booleanGraph = booleanGraph
        self.__actuatorList = actuatorList
        self.__metricPublisher = metricPublisher
        self.__logger = get_logger()

        cacheDroppableMetricProvidersSet = set()

        for provider in self.__virtMetricsProviders:
            if isinstance(provider, AbstractMetricProviderWrapper):
                temp = provider.getCacheDroppableVirtualMetricProviderInstances()
                for p in temp:
                    cacheDroppableMetricProvidersSet.add(p)
            elif isinstance(provider, AbstractVirtualMetricProviderCacheDroppable):
                cacheDroppableMetricProvidersSet.add(provider)

        self.__cacheDroppableMetricsProviders: list[AbstractVirtualMetricProviderCacheDroppable] = (
            list(cacheDroppableMetricProvidersSet))

    def run(self, delay: int = 5, maxRuns: int = -1):
        if self.__logger:
            self.__logger.info(f"Starting MQL run loop (delay={delay}s, maxRuns={maxRuns})")

        currentRun = 0
        while currentRun < maxRuns or maxRuns == -1:
            currentRun += 1

            if self.__logger:
                self.__logger.debug(f"MQL loop iteration {currentRun} starting...")

            try:
                self.__runSingleLoop()
                if self.__logger:
                    self.__logger.debug(f"MQL loop iteration {currentRun} completed successfully")
            except Exception as e:
                if self.__logger:
                    self.__logger.error(f"Error in MQL loop iteration {currentRun}: {e}", exc_info=True)
                else:
                    print(f"Error in MQL loop: {e}")

            time.sleep(delay)

    def __runSingleLoop(self):
        # Drop cache
        if self.__logger:
            self.__logger.debug("Dropping virtual metrics provider cache...")
        self.__dropVirtMetricsProviderCache__()

        # Get metrics
        if self.__logger:
            self.__logger.debug("Reading metrics from providers...")
        vMetrics = self.__getVirtMetricsProviderCache__()
        if self.__logger:
            self.__logger.debug(f"Collected metrics from {len(vMetrics)} provider(s)")

        # Check alerts
        if self.__logger:
            self.__logger.debug("Checking alerts...")
        self.__putVirtualMetricsProviderCacheToAlerts__(vMetrics)

        # Evaluate conditions
        if self.__logger:
            self.__logger.debug("Evaluating conditions...")
        conditionsOutput = self.__putVirtualMetricsProviderCacheToConditions__(vMetrics)
        if self.__logger:
            self.__logger.debug(f"Evaluated {len(conditionsOutput)} condition(s)")

        # Evaluate boolean graph
        if self.__logger:
            self.__logger.debug("Evaluating boolean graph...")
        graphOutputs = self.__booleanGraph.evaluate(conditionsOutput)

        # Merge outputs
        self.__mergeConditionOutputWithGraphOutputs__(graphOutputs, conditionsOutput)

        # Update actuators
        if self.__logger:
            self.__logger.debug(f"Updating {len(self.__actuatorList)} actuator(s)...")
        self.__putGraphOutputToActuators__(graphOutputs)

        # Publish metrics
        if self.__metricPublisher is not None:
            if self.__logger:
                self.__logger.debug("Publishing metrics to MQTT...")
            self.__metricPublisher.sendMessage(vMetrics)

    def __mergeConditionOutputWithGraphOutputs__(
            self,
            graphOutput: dict[str, bool],
            conditionOutput: dict[str, bool]
    ) -> None:
        for condKey in conditionOutput.keys():
            if condKey not in graphOutput:
                graphOutput[condKey] = conditionOutput[condKey]

    def __putGraphOutputToActuators__(
            self,
            graphOutput: dict[str, bool]
    ) -> None:
        for actuator in self.__actuatorList:
            request_id = actuator.getRequestId()
            if request_id not in graphOutput:
                error_msg = f"Actuator expected non-existing id: {request_id}"
                if self.__logger:
                    self.__logger.error(error_msg)
                raise ConfigError(error_msg)

            new_state = graphOutput[request_id]
            if self.__logger:
                self.__logger.info(f"Setting actuator '{request_id}' state to: {new_state}")
            actuator.setState(new_state)

    def __putVirtualMetricsProviderCacheToConditions__(
            self,
            vmpCache: dict[str, dict[str, float]]
    ) -> dict[str, bool]:
        ret: dict[str, bool] = {}
        for cond in self.__conditionList:
            request_id = cond.getRequestId()
            if request_id not in vmpCache:
                error_msg = f"Condition expected non-existing input id: {request_id}"
                if self.__logger:
                    self.__logger.error(error_msg)
                raise ConfigError(error_msg)

            result = cond.checkMetricCondition(vmpCache[request_id])
            ret[cond.getId()] = result

            if self.__logger:
                self.__logger.debug(f"Condition '{cond.getId()}' evaluated to: {result}")

        return ret

    def __putVirtualMetricsProviderCacheToAlerts__(
            self,
            vmpCache: dict[str, dict[str, float]]
    ) -> None:
        for alert in self.__alertList:
            request_id = alert.getRequestId()
            if request_id not in vmpCache:
                error_msg = f"Alert expected non-existing input id: {request_id}"
                if self.__logger:
                    self.__logger.error(error_msg)
                raise ConfigError(error_msg)

            if self.__logger:
                self.__logger.debug(f"Checking alert for metric: {request_id}")
            alert.check(vmpCache[request_id])

    def __dropVirtMetricsProviderCache__(self) -> None:
        for vmp in self.__cacheDroppableMetricsProviders:
            vmp.dropCache()

    def __getVirtMetricsProviderCache__(self) -> dict[str, dict[str, float]]:
        vmpCache: dict[str, dict[str, float]] = {} # mapping each id to it's metric
        for vmp in self.__virtMetricsProviders:
            provider_id = vmp.getId()
            try:
                metric = vmp.recieveMetric()
                vmpCache[provider_id] = metric
                if self.__logger:
                    self.__logger.info(f"Received metric from provider '{provider_id}': {metric}")
            except Exception as e:
                if self.__logger:
                    self.__logger.error(f"Failed to receive metric from provider '{provider_id}': {e}", exc_info=True)
                raise
        return vmpCache
