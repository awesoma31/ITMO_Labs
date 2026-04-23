from src.actuators.PhysicalActuator import PhysicalActuator
from src.actuators.PhysicalActuatorImpl.BypassValveMock import BypassValveMock
from src.actuators.PhysicalActuatorImpl.ValveMock import ValveMock
from src.actuators.PhysicalActuatorImpl.ValveImpl import ValveImpl
from src.actuators.PhysicalActuatorImpl.BypassValveImpl import BypassValveImpl
from src.exceptions.ConfigError import ConfigError
from src.registrys.CommonRegistry import CommonRegistry

def PhysycalActuatorFabirc(desc: dict[str, any], reg: CommonRegistry) -> PhysicalActuator:
    if "type" not in desc:
        raise ConfigError("PhysicalActuatorFabric type {}".format(desc))
    if "id" not in desc:
        raise ConfigError("PhysicalActuatorFabric id {}".format(desc))
    type_: str = desc["type"]
    id_ = desc["id"]
    if type_ == "BypassValveMock":
        return BypassValveMock(reg, id_)
    if type_ == "ValveMock":
        return ValveMock(reg, id_)
    if type_ == "BypassValve":
        return BypassValveImpl(reg, id_)
    if type_ == "Valve":
        return ValveImpl(reg, id_)
    raise NotImplementedError(f"PhysicalActuatorFabric type {type_}")