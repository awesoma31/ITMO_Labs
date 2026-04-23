from abc import abstractmethod
from src.commonObjects.IRequestIdObject import IRequestIdObject

class PhysicalActuator(IRequestIdObject):
    """!
    @brief Documentation for the class MetricActuator.

    it recieves boolean as new state, and changes it on some real object like pin and etc
    """

    @abstractmethod
    def setState(self, state: bool) -> None:
        pass