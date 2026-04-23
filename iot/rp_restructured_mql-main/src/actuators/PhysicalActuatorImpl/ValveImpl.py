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


class ValveImpl(PhysicalActuator):
    def __init__(self, reg: CommonRegistry, id_: str) -> None:
        self.reg = reg
        self.id_ = id_
        self.pinNbr = self.__getDescFromReg()["pin"]
        self.gpioPin = GPIOPin(self.pinNbr)
        self.__logger = get_logger()

        if self.__logger:
            self.__logger.info(f"Valve {id_} initialized on pin {self.pinNbr}")

    def getRequestId(self) -> str:
        return self.__getReqIdFromReg()

    def setState(self, state: bool) -> None:
        if self.__logger:
            self.__logger.debug(f"Valve {self.id_} state changing to: {state}")
        self.gpioPin.set_state(state)

    def __getDescFromReg(self) -> dict:
        return self.reg.getParameterByPath(f"actuators.{self.id_}")

    def __getReqIdFromReg(self) -> str:
        return self.reg.getParameterByPath(f"actuators.{self.id_}.input")
