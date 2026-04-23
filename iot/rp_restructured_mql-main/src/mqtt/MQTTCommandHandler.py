"""
MQTT Command Handler для Raspberry Pi
Получает и выполняет команды от бэкенда для управления ASIC майнерами
"""

import paho.mqtt.client as mqtt
import json
import requests
from typing import Dict, List, Optional, Any
from datetime import datetime
import time
from src.registrys.CommonRegistry import CommonRegistry
import re

VAR_PATTERN = re.compile(r"\$\{([^}]+)\}")

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        import logging
        return logging.getLogger(__name__)


class MQTTCommandHandler:
    """
    Обработчик MQTT команд для выполнения HTTP запросов к ASIC майнерам
    """

    def __init__(self, device_id: str, hostname: str, reg: CommonRegistry, port: int = 1883, client_id: str = ""):
        self.device_id = device_id
        self.hostname = hostname
        self.port = port
        self.client_id = client_id or f"rp_command_handler_{device_id}"
        self.client = mqtt.Client(
            client_id=self.client_id,
            callback_api_version=mqtt.CallbackAPIVersion.VERSION1
        )

        self.command_topic = f"device/{device_id}/control"
        self.response_topic = f"device/{device_id}/response"

        self.client.on_connect = self._on_connect
        self.client.on_message = self._on_message
        self.client.on_disconnect = self._on_disconnect

        self._connected = False
        self.logger = get_logger()
        self.reg = reg

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

            if payload.get('command') == 'asic_http_proxy':
                self._handle_asic_proxy_command(payload)
            else:
                self.logger.warning(f"Unknown command type: {payload.get('command')}")

        except json.JSONDecodeError as e:
            self.logger.error(f"Failed to parse MQTT message: {e}")
        except Exception as e:
            self.logger.error(f"Error processing MQTT message: {e}")

    def _handle_asic_proxy_command(self, command_data: Dict[str, Any]):
        """
        Обрабатывает команду asic_http_proxy
        
        Формат команды:
        {
            "deviceId": "device-uuid",
            "command": "asic_http_proxy",
            "cmdId": "cmd-uuid",
            "asic": {
                "ip": ${address},
                "firmware": "antminer",
                "port": ${port},
                "scheme": "http",
                "id": asic_id
            },
            "steps": [
                {
                    "id": "step1",
                    "request": {
                        "method": "GET",
                        "path": "/api/status",
                        "headers": {"Authorization": "Bearer token"},
                        "body": null,
                        "timeoutMs": 5000
                    },
                    "extract": {"status": "$.status"}
                }
            ],
            "policy": {
                "maxRetries": 3,
                "retryDelayMs": 1000
            }
        }
        """
        cmd_id = command_data.get('cmdId')
        asic_info = command_data.get('asic', {})
        steps = command_data.get('steps', [])
        policy = command_data.get('policy', {})

        self.logger.info(f"Executing ASIC proxy command {cmd_id} with {len(steps)} steps")
        
        result = self._execute_asic_command(asic_info, steps, policy)

        response = {
            'cmdId': cmd_id,
            'status': result['status'],
            'failedStep': result.get('failedStep'),
            'stepResults': result.get('stepResults', [])
        }

        self._send_response(response)

    def _execute_asic_command(self, asic_info: Dict, steps: List[Dict], policy: Dict) -> Dict:
        """
        Выполняет последовательность HTTP запросов к ASIC
        """
        id = asic_info.get('id')
        reg_vars = self._build_reg_vars(id)
        asic_info = self._substitute_vars(asic_info, reg_vars)

        ip = asic_info.get('ip')
        port = asic_info.get('port', 80)
        scheme = asic_info.get('scheme', 'http')
        base_url = f"{scheme}://{ip}:{port}"

        max_retries = policy.get('maxRetries', 3)
        retry_delay_ms = policy.get('retryDelayMs', 1000)
        retry_delay_sec = retry_delay_ms / 1000.0

        step_results = []
        extracted_vars = {}
        reg_vars = self._build_reg_vars(id)

        for step in steps:
            step_id = step.get('id')
            request_config = step.get('request', {})
            extract_config = step.get('extract', {})

            step_result = None
            last_error = None

            for attempt in range(max_retries + 1):
                variables = {
                    **reg_vars,
                    **extracted_vars
                }
                try:
                    step_result = self._execute_http_request(
                        base_url,
                        request_config,
                        variables
                    )

                    if step_result['status'] == 'success':
                        break

                except Exception as e:
                    last_error = str(e)
                    self.logger.warning(f"Step {step_id} attempt {attempt + 1} failed: {e}")

                if attempt < max_retries:
                    time.sleep(retry_delay_sec)

            if step_result is None:
                step_result = {
                    'status': 'failed',
                    'error': last_error or 'Unknown error'
                }

            step_result['stepId'] = step_id
            step_results.append(step_result)

            if step_result['status'] == 'failed':
                return {
                    'status': 'FAILED',
                    'failedStep': step_id,
                    'stepResults': step_results
                }

            if extract_config and step_result.get('responsePreview'):
                try:
                    response_data = json.loads(step_result['responsePreview'])
                    for var_name, json_path in extract_config.items():
                        extracted_vars[var_name] = self._extract_from_json(response_data, json_path)
                except Exception as e:
                    self.logger.warning(f"Failed to extract variables from step {step_id}: {e}")

        return {
            'status': 'SUCCESS',
            'stepResults': step_results
        }

    def _build_reg_vars(self, asic_id: str) -> Dict[str, Any]:
        """
        Загружает все параметры ASIC из CommonRegistry
        metricProviders.<id>.*
        """
        base_path = f"metricProviders.{asic_id}"
        result = {}

        try:
            provider = self.reg.getParameterByPath(base_path)
            if isinstance(provider, dict):
                result.update(provider)
        except Exception as e:
            self.logger.warning(f"Failed to load registry vars for ASIC {asic_id}: {e}")

        return result

    def _substitute_vars(self, value: Any, variables: Dict[str, Any]) -> Any:
        if isinstance(value, str):
            matches = VAR_PATTERN.findall(value)

            # строка — это ровно "${var}"
            if len(matches) == 1 and value.strip() == f"${{{matches[0]}}}":
                return variables.get(matches[0])

            # строка с подстановками
            def repl(match):
                var_name = match.group(1)
                return str(variables.get(var_name, match.group(0)))

            return VAR_PATTERN.sub(repl, value)

        if isinstance(value, dict):
            return {
                self._substitute_vars(k, variables):
                    self._substitute_vars(v, variables)
                for k, v in value.items()
            }

        if isinstance(value, list):
            return [self._substitute_vars(v, variables) for v in value]

        return value

    def _execute_http_request(self, base_url: str, request_config: Dict, variables: Dict) -> Dict:
        method = request_config.get('method', 'GET').upper()
        path = request_config.get('path', '/')
        headers = request_config.get('headers', {})
        body = request_config.get('body')
        timeout_ms = request_config.get('timeoutMs', 5000)
        timeout_sec = timeout_ms / 1000.0

        # 🔥 подстановка переменных ВЕЗДЕ
        path = self._substitute_vars(path, variables)
        headers = self._substitute_vars(headers, variables)
        body = self._substitute_vars(body, variables)

        # 🔥 нормализация заголовков
        normalized_headers = {}
        for k, v in headers.items():
            key = k.lstrip('_').replace('-', ' ').title().replace(' ', '-')
            normalized_headers[key] = v

        url = f"{base_url}{path}"

        try:
            response = requests.request(
                method=method,
                url=url,
                headers=normalized_headers,
                json=body if body else None,
                timeout=timeout_sec
            )

            response_text = response.text or ""
            response_preview = response_text[:500]

            return {
                'status': 'success',
                'statusCode': response.status_code,
                'responsePreview': response_preview
            }

        except requests.exceptions.Timeout:
            return {
                'status': 'failed',
                'error': f'Request timeout after {timeout_ms}ms'
            }
        except requests.exceptions.ConnectionError as e:
            return {
                'status': 'failed',
                'error': f'Connection error: {str(e)}'
            }
        except Exception as e:
            return {
                'status': 'failed',
                'error': f'Request failed: {str(e)}'
            }

    def _extract_from_json(self, data: Any, json_path: str) -> Any:
        """
        Упрощенная версия JSONPath extraction
        Поддерживает простые пути вида $.key или $.nested.key
        """
        if json_path.startswith('$.'):
            json_path = json_path[2:]

        keys = json_path.split('.')
        current = data

        for key in keys:
            if isinstance(current, dict):
                current = current.get(key)
            else:
                return None

        return current

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
