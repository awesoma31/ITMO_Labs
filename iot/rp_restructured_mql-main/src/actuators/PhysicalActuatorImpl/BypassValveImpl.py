from src.actuators.PhysicalActuator import PhysicalActuator
from src.registrys.CommonRegistry import CommonRegistry
from src.actuators.PhysicalActuatorImpl.GPIOPin import GPIOPin

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

class BypassValveImpl(PhysicalActuator):
    def __init__(self, reg: CommonRegistry, id_: str) -> None:
        self.reg = reg
        self.id_ = id_
        desc = self.__getDescFromReg()
        self.pinOn = desc["pinOn"]
        self.pinOff = desc["pinOff"]
        self.gpioOn = GPIOPin(self.pinOn)
        self.gpioOff = GPIOPin(self.pinOff)
        self.__logger = get_logger()

        if self.__logger:
            self.__logger.info(f"BypassValve {id_} initialized (ON pin: {self.pinOn}, OFF pin: {self.pinOff})")

    def getRequestId(self) -> str:
        return self.__getReqIdFromReg()

    def setState(self, state: bool) -> None:
        if self.__logger:
            self.__logger.debug(f"BypassValve {self.id_} state changing to: {state}")

        if state:
            self.gpioOn.set_state(True)
            self.gpioOff.set_state(False)
        else:
            self.gpioOn.set_state(False)
            self.gpioOff.set_state(True)

    def __getDescFromReg(self) -> dict:
        return self.reg.getParameterByPath(f"actuators.{self.id_}")

    def __getReqIdFromReg(self) -> str:
        return self.reg.getParameterByPath(f"actuators.{self.id_}.input")
