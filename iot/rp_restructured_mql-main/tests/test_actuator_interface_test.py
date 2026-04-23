import pytest

from src.actuators.PhysicalActuator import PhysicalActuator

# just make sure, I ain't fucked up with interface

class ActuatorMock(PhysicalActuator):

    def __init__(self, reqId: str):
        self.reqId = reqId
        self.states = []

    def getRequestId(self) -> str:
        return self.reqId

    def setState(self, metric: bool) -> None:
        self.states.append(metric)

def test_straightForwardInterfaceTest():
    actuator = ActuatorMock("test")
    _id = actuator.getRequestId()
    actuator.setState(True)
    assert actuator.states[0] == True

if __name__ == "__main__":
    pytest.main([__file__])