from src.actuators.PhysicalActuator import PhysicalActuator
from src.registrys.CommonRegistry import CommonRegistry


class ValveMock(PhysicalActuator):
    def __init__(self, reg: CommonRegistry, id_: str) -> None:
        self.reg = reg
        self.id_ = id_

    def getRequestId(self) -> str:
        return self.__getReqIdFromReg()

    def setState(self, state: bool) -> None:
        pass

    def __getReqIdFromReg(self) -> str:
        return self.reg.getParameterByPath(f"actuators.{self.id_}.input")