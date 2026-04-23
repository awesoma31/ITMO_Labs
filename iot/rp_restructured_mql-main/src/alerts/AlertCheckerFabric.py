from src.alerts.AlertChecker import AlertChecker
from src.alerts.AlertCheckerImpl import AlertCheckerByCondition
from src.exceptions.ConfigError import ConfigError
from src.registrys.CommonRegistry import CommonRegistry


def AlertCheckerFabric(desc: dict[str, str], reg: CommonRegistry) -> AlertChecker:
    if "id" not in desc:
        raise ConfigError("can't find id field {desc}".format(desc=desc))
    return AlertCheckerByCondition(reg, desc["id"])