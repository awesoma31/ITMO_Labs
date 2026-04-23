import paho.mqtt.client as mqtt
import json
from typing import Union

from src.deviceRegistration.initializeMetricMapper import MetricTopicMapper


class MQTTPublisher:
    def __init__(self, hostname: str, topic: Union[str, MetricTopicMapper], deviceId: str, port: int = 1883, client_id: str = ""):
        self.topic = topic
        if isinstance(topic, MetricTopicMapper):
            self.useMapper = True
        else:
            self.useMapper = False
        self.hostname = hostname
        self.port = port
        self.client = mqtt.Client(
            client_id=client_id,
            callback_api_version=mqtt.CallbackAPIVersion.VERSION1
        )
        self.deviceId = deviceId
        self._connected = False

    def __enter__(self):
        self.connect()
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.disconnect()

    def __del__(self):
        self.disconnect()

    def connect(self) -> None:
        try:
            self.client.connect(self.hostname, self.port, 60)
            self.client.loop_start()  # Запускаем фоновый цикл обработки сообщений
            self._connected = True
        except Exception as _:
            pass
            self._connected = False

    def disconnect(self) -> None:
        if hasattr(self, 'client') and self._connected:
            self.client.loop_stop()
            self.client.disconnect()
            self._connected = False
            pass

    def sendMessage(self, payload: dict[str, dict[str, float]]) -> None:
        if not self._connected:
            pass
            return

        try:
            for pl in payload.keys():
                if self.useMapper:
                    metric, topic = self.topic.formatMQTTMetricMessage(pl, self.deviceId, payload[pl])
                else:
                    topic = self.topic
                    metric = payload[pl]
                    metric["deviceId"] = self.deviceId
                    metric["instanceId"] = pl
                json_payload = json.dumps(metric)
                result = self.client.publish(topic, json_payload)

                if result.rc == mqtt.MQTT_ERR_SUCCESS:
                    pass
                else:
                    pass

        except Exception as _:
            pass
