from abc import ABC, abstractmethod

class FloatCondition(ABC):
    @abstractmethod
    def eval(self, a: float, b: float) -> bool:
        pass