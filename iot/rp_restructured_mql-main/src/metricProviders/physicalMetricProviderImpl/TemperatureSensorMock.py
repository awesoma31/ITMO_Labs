from src.metricProviders.PhysicalMetricProvider import PhysicalMetricProvider
from src.registrys.CommonRegistry import CommonRegistry

#TODO!! THIS IS MOCK, DO NOT USE IN PRODUCTION
from random import uniform

class TemperatureSensorMock(PhysicalMetricProvider):
    def __init__(self, id: str, reg: CommonRegistry):
        self.id = id
        self.reg = reg

    def getId(self) -> str:
        return self.id

    def provideMetric(self) -> dict[str, float]:
        return {"tempC": uniform(0, 100)}