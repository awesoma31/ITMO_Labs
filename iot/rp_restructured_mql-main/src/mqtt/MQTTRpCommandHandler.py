"""
MQTT Command Handler для Raspberry Pi
Получает и выполняет команды от бэкенда для управления ASIC майнерами
"""

import paho.mqtt.client as mqtt
import json
from typing import Dict
from src.registrys.CommonRegistry import CommonRegistry

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        import logging
        return logging.getLogger(__name__)


class MQTTRpCommandHandler:
    """
    Обработчик MQTT команд для выполнения HTTP запросов к ASIC майнерам
    """

    def __init__(self, device_id: str, hostname: str, userReg: CommonRegistry, port: int = 1883, client_id: str = ""):
        self.device_id = device_id
        self.hostname = hostname
        self.port = port
        self.client_id = client_id or f"rp_command_handler_{device_id}"
        self.client = mqtt.Client(
            client_id=self.client_id,
            callback_api_version=mqtt.CallbackAPIVersion.VERSION1
        )

        self.command_topic = f"device/{device_id}/controlRp"
        self.response_topic = f"device/{device_id}/response"

        self.client.on_connect = self._on_connect
        self.client.on_message = self._on_message
        self.client.on_disconnect = self._on_disconnect

        self._connected = False
        self.logger = get_logger()
        self.reg = userReg

    def _on_connect(self, client, userdata, flags, rc):
        """Callback при подключении к MQTT брокеру"""
        if rc == 0:
            self._connected = True
            self.logger.info(f"Connected to MQTT broker. Subscribing to {self.command_topic}")
            client.subscribe(self.command_topic)
        else:
            self.logger.error(f"Failed to connect to MQTT broker, rc={rc}")
            self._connected = False

    def _on_disconnect(self, client, userdata, rc):
        """Callback при отключении от MQTT брокера"""
        self._connected = False
        if rc != 0:
            self.logger.warning(f"Unexpected disconnect from MQTT broker, rc={rc}")

    def _on_message(self, client, userdata, msg):
        """Callback при получении MQTT сообщения"""
        try:
            payload = json.loads(msg.payload.decode('utf-8'))
            self.logger.info(f"Received command on {msg.topic}: {payload.get('command', 'unknown')}")
            # path is smth like "conditions.{id}.value"
            # so in yaml in conditions:
            #    - id: {id}
            #      value: {'ll be equal new value}
            path = payload.get('path')
            value = payload.get('value')
            self.reg.setParameterByPath(path, value)

        except json.JSONDecodeError as e:
            self.logger.error(f"Failed to parse MQTT message: {e}")
        except Exception as e:
            self.logger.error(f"Error processing MQTT message: {e}")

    def _send_response(self, response_data: Dict):
        """Отправляет результат выполнения команды обратно на сервер"""
        try:
            json_payload = json.dumps(response_data)
            result = self.client.publish(self.response_topic, json_payload)

            if result.rc == mqtt.MQTT_ERR_SUCCESS:
                self.logger.info(f"Sent command response for {response_data.get('cmdId')}")
            else:
                self.logger.error(f"Failed to send command response, rc={result.rc}")

        except Exception as e:
            self.logger.error(f"Error sending command response: {e}")

    def connect(self):
        """Подключается к MQTT брокеру"""
        try:
            self.client.connect(self.hostname, self.port, 60)
            self.client.loop_start()
            self.logger.info("MQTT Command Handler started")
        except Exception as e:
            self.logger.error(f"Failed to connect to MQTT broker: {e}")
            raise

    def disconnect(self):
        """Отключается от MQTT брокера"""
        if hasattr(self, 'client') and self._connected:
            self.client.loop_stop()
            self.client.disconnect()
            self._connected = False
            self.logger.info("MQTT Command Handler stopped")

    def is_connected(self) -> bool:
        """Проверяет, подключен ли handler к MQTT"""
        return self._connected
