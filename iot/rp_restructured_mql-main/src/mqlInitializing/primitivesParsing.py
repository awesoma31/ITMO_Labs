from src.actuators.PhysicalActuator import PhysicalActuator
from src.actuators.PhysActuatorFabric import PhysycalActuatorFabirc

from src.condition.Condition import MetricCondition
from src.condition.ConditionFabric import MetricConditionFabric

from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.metricProviders.VirtualMetricProviderImpl import VirtualMetricProviderImpl
from src.metricProviders.PhysicalMetricProviderFabric import PhysicalMetricProviderFabric
from src.metricProviders.AbsVirtualMetricProvider import AbstractVirtualMetricProvider, AbstractMetricProviderWrapper
from src.metricProviders.VirtualMetricProviderAggregator import VirtualMetricProviderAggregator

from src.alerts.AlertChecker import AlertChecker
from src.alerts.AlertCheckerFabric import AlertCheckerFabric

from src.booleanConditionGraph.BooleanGraph import BooleanGraph
from src.booleanConditionGraph.BooleanNode import BooleanNode

from src.registrys.CommonRegistry import CommonRegistry
from src.exceptions.ConfigError import ConfigError


def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None


def parseInternalConfig(
        reg: CommonRegistry
) -> tuple[
    list[AbstractVirtualMetricProvider],
    list[AlertChecker],
    list[MetricCondition],
    BooleanGraph,
    list[PhysicalActuator]
]:
    logger = get_logger()

    virtualMetricProviderPool: dict[str, AbstractVirtualMetricProvider] = {}

    for cfg in reg.getParameterByPath("metricProviders"):
        pid = cfg.get("id")
        ptype = cfg.get("type")

        if not pid:
            raise ConfigError("MetricProvider without id")

        if pid in virtualMetricProviderPool:
            raise ConfigError(f"Duplicate MetricProvider id '{pid}'")

        physical = PhysicalMetricProviderFabric(ptype, pid, reg)
        logger.debug(
            "Created PhysicalMetricProvider id=%s type=%s",
            pid, ptype
        )

        virtual = VirtualMetricProviderImpl(physical, pid)
        logger.debug(
            "Created VirtualMetricProviderImpl id=%s (cache holder)",
            pid
        )

        virtualMetricProviderPool[pid] = virtual

    alerts: list[AlertChecker] = []
    if reg.getParameterByPath("alerts") and reg.getParameterByPath("alerts") is not None:
        for alert_cfg in reg.getParameterByPath("alerts"):
            alert = AlertCheckerFabric(alert_cfg, reg)
            alerts.append(alert)
            logger.debug(
                "Created AlertChecker id=%s",
                alert.getRequestId()
            )
    if reg.getParameterByPath("metricAggregators") and reg.getParameterByPath("metricAggregators") is not None:
        for agg in reg.getParameterByPath("metricAggregators"):
            agg_id = agg.get("id")
            agg_type = agg.get("type")
            metric_name = agg.get("metricName")
            inputs = agg.get("input")

            if not agg_id:
                raise ConfigError("MetricAggregator without id")

            if agg_id in virtualMetricProviderPool:
                raise ConfigError(f"Duplicate MetricAggregator id '{agg_id}'")

            if not isinstance(inputs, list) or not inputs:
                raise ConfigError(
                    f"MetricAggregator '{agg_id}' has empty or invalid input list"
                )

            resolved_inputs: list[AbstractVirtualMetricProvider] = []

            for input_id in inputs:
                if input_id not in virtualMetricProviderPool:
                    raise ConfigError(
                        f"MetricAggregator '{agg_id}' references unknown provider '{input_id}'"
                    )

                provider = virtualMetricProviderPool[input_id]

                if isinstance(provider, AbstractMetricProviderWrapper):
                    raise ConfigError(
                        f"MetricAggregator '{agg_id}' references another aggregator '{input_id}'"
                    )

                resolved_inputs.append(provider)

            logger.debug(
                "Creating VirtualMetricProviderAggregator id=%s type=%s metric=%s inputs=%s",
                agg_id,
                agg_type,
                metric_name,
                inputs
            )

            aggregator = VirtualMetricProviderAggregator(
                resolved_inputs,
                agg_type,
                metric_name,
                agg_id
            )

            virtualMetricProviderPool[agg_id] = aggregator

    conditions: list[MetricCondition] = []
    for cond_cfg in reg.getParameterByPath("conditions"):
        cond = MetricConditionFabric(cond_cfg, reg)
        conditions.append(cond)
        logger.debug(
            "Created MetricCondition id=%s, reqId=%s",
            cond.getId(),
            cond.getRequestId()
        )

    graph_nodes = []
    if reg.getParameterByPath("conditionGrouping") and reg.getParameterByPath("conditionGrouping") is not None:
        for node_cfg in reg.getParameterByPath("conditionGrouping"):
            node = BooleanNode(
                node_cfg["type"],
                node_cfg["input"],
                node_cfg["id"]
            )
            graph_nodes.append(node)
            logger.debug(
                "Created BooleanNode id=%s type=%s inputs=%s",
                node_cfg["id"],
                node_cfg["type"],
                node_cfg["input"]
            )

    graph = BooleanGraph(graph_nodes)
    logger.debug(
        "Created BooleanGraph with %d nodes",
        len(graph_nodes)
    )

    actuators: list[PhysicalActuator] = []
    for act_cfg in reg.getParameterByPath("actuators"):
        actuator = PhysycalActuatorFabirc(act_cfg, reg)
        actuators.append(actuator)
        logger.debug(
            "Created PhysicalActuator id=%s",
            actuator.getRequestId()
        )

    logger.debug(
        "Config parsed successfully: "
        "%d virtual metric providers, %d alerts, %d conditions, %d actuators",
        len(virtualMetricProviderPool),
        len(alerts),
        len(conditions),
        len(actuators),
    )

    return (
        list(virtualMetricProviderPool.values()),
        alerts,
        conditions,
        graph,
        actuators,
    )
