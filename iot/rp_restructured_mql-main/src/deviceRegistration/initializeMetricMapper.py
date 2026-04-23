from typing import Union

from src.registrys.CommonRegistry import CommonRegistry
from src.metricProviders.PhysicalMetricProviderFabric import dispatchTopicByType
from src.metricProviders.PhysicalMetricProviderFabric import TopicType

class MetricTopicMapper:
    # id -> topic name 4 metrics
    def __init__(self, metricMap: dict[str, TopicType], reg: CommonRegistry):
        self.metricMap = metricMap
        self.reg = reg

    def __getTopicByProviderId(self, providerId) -> TopicType:
        return self.metricMap[providerId]

    # TODO !!refactor this later!!
    # returns payload and topic
    def formatMQTTMetricMessage(self, instanceId: str, deviceId: str, singlePayload: dict[str, float]) -> tuple[dict[str, Union[str, float]], str]:
        ret: dict[str, Union[str, float]] = singlePayload
        topic = self.__getTopicByProviderId(instanceId)
        if topic == TopicType.PRIMARY:
            ret["deviceId"] = deviceId
            ret["instanceId"] = instanceId
            return ret, topic.value
        else:
            # TODO this stuff is pretty match hardcoded 4 temp sensor, do smth later
            ret["deviceId"] = deviceId
            ret["metricKey"] = instanceId
            ret["metricType"] = "ambient_temperature"
            ret["metricValue"] = singlePayload[list(singlePayload.keys())[0]]
            return ret, topic.value


def makeMetricMapper(internalReg: CommonRegistry) -> MetricTopicMapper:
    ret = {}
    for prov in internalReg.getParameterByPath("metricProviders"):
        ret[prov["id"]] = dispatchTopicByType(prov["type"])

    if (internalReg.getParameterByPath("metricAggregators")
            and internalReg.getParameterByPath("metricAggregators") is not None):
        for agg in internalReg.getParameterByPath("metricAggregators"):
            ret[agg["id"]] = TopicType.OTHER

    return MetricTopicMapper(ret, internalReg)

