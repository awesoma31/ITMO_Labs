from abc import ABC, abstractmethod

class IIdObject(ABC):
    """!
    @brief Id Object
    describes object with id
    """
    @abstractmethod
    def getId(self):
        pass