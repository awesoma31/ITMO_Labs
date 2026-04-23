from abc import ABC, abstractmethod
from src.commonObjects.IIdObject import IIdObject


class PhysicalMetricProvider(IIdObject):
    """!
    @brief Documentation for the class PhysicalMetricProvider.

    this class describes physical metric provider. It provides metrics from
    real objects like ASIC, temperature pin and etc
    it have just one method to accept 'em, on each call it'll
    recieve new metric from an object
    """
    @abstractmethod
    def provideMetric(self) -> dict[str, float]:
        pass