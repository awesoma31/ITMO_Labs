import logging
import json
from datetime import datetime
from typing import Optional
from src.mqtt.MQTTPublisher import MQTTPublisher


class MQTTLoggingHandler(logging.Handler):
    """
    Custom logging handler that sends logs to MQTT topic.
    """
    
    def __init__(self, mqtt_publisher: MQTTPublisher, device_id: str):
        super().__init__()
        self.mqtt_publisher = mqtt_publisher
        self.device_id = device_id
        
    def emit(self, record: logging.LogRecord) -> None:
        """
        Emit a log record to MQTT topic.
        """
        try:
            log_entry = {
                "deviceId": self.device_id,
                "level": record.levelname,
                "message": self.format(record)
            }
            
            # Use the MQTT publisher to send log message
            if self.mqtt_publisher._connected:
                json_payload = json.dumps(log_entry)
                self.mqtt_publisher.client.publish(
                    self.mqtt_publisher.topic,
                    json_payload
                )
        except Exception:
            # Avoid recursion if logging fails
            self.handleError(record)
