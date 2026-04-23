from abc import ABC, abstractmethod

class BooleanNodeMethod(ABC):
    @abstractmethod
    def evaluate(self, input: list[bool]) -> bool:
        pass