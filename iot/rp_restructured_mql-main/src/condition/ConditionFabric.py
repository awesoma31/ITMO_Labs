from src.condition.ConditionImpl import MetricConditionImpl
from src.condition.Condition import MetricCondition
from src.registrys.CommonRegistry import CommonRegistry
from src.exceptions.ConfigError import ConfigError

def MetricConditionFabric(desc: dict[str, str], reg: CommonRegistry) -> MetricCondition:
    if "id" not in desc:
        raise ConfigError("can't find id field {desc}".format(desc=desc))
    return MetricConditionImpl(reg, desc["id"])