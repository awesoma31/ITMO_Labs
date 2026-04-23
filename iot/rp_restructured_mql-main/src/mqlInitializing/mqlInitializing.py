
# parsers
from src.mqlInitializing.primitivesParsing import parseInternalConfig

# other stuff
from src.mql import MQL
from src.registrys.CommonRegistry import CommonRegistry
from src.mqtt.MQTTPublisher import MQTTPublisher

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

def initializeMQL(reg: CommonRegistry, publisher: MQTTPublisher = None) -> MQL:

    vmps, alerts, conditions, graph, actuators = parseInternalConfig(reg)

    return MQL(
        vmps,
        alerts,
        conditions,
        graph,
        actuators,
        publisher,
    )
