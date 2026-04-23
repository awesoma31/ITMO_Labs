from abc import ABC, abstractmethod

class IRequestIdObject(ABC):
    """!
    @brief IRequestIdObject
    describes object that might have inputs by id
    """
    @abstractmethod
    def getRequestId(self):
        pass