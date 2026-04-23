from abc import abstractmethod
from src.commonObjects.IRequestIdObject import IRequestIdObject

class AlertChecker(IRequestIdObject):

    @abstractmethod
    def check(self, metric: dict[str, float]) -> None:
        pass