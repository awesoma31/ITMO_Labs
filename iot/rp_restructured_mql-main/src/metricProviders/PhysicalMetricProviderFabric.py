from enum import Enum

from src.metricProviders.physicalMetricProviderImpl.TemperatureSensorMock import TemperatureSensorMock
from src.metricProviders.physicalMetricProviderImpl.ASICMock import ASICMock
from src.metricProviders.physicalMetricProviderImpl.ASICImpl import ASICImpl
from src.metricProviders.physicalMetricProviderImpl.TemperatureSensorImpl import TemperatureSensorImpl
from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.registrys.CommonRegistry import CommonRegistry


def PhysicalMetricProviderFabric(type: str, id_: str, reg: CommonRegistry) -> PhysicalMetricProvider:
    if type == "ASICMock":
        return ASICMock(id_, reg)
    if type == "TemperatureSensorMock":
        return TemperatureSensorMock(id_, reg)
    if type == "ASIC":
        return ASICImpl(id_, reg)
    if type == "TemperatureSensor":
        return TemperatureSensorImpl(id_, reg)
    raise NotImplementedError(f"PhysicalMetricProviderFabric type {type}")

#! it's not really fit here, but i just want all string magic constants 'll be in one place
class TopicType(Enum):
    PRIMARY = "rp/metrics"
    OTHER = "rp/other_metrics"

def dispatchTopicByType(type: str) -> TopicType:
    if type == "ASICMock" or type == "ASIC":
        return TopicType.PRIMARY
    if type == "TemperatureSensorMock" or type == "TemperatureSensor":
        return TopicType.OTHER
    raise NotImplementedError(f"PhysicalMetricProviderFabric type {type}")