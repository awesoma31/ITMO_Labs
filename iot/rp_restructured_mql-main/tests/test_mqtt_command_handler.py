"""
Тест для MQTTCommandHandler
"""

import unittest
import json
import time
from unittest.mock import Mock, patch, MagicMock
from src.mqtt.MQTTCommandHandler import MQTTCommandHandler


class TestMQTTCommandHandler(unittest.TestCase):
    
    def setUp(self):
        """Настройка для каждого теста"""
        self.device_id = "test-device-123"
        self.mqtt_host = "localhost"
        self.mqtt_port = 1883
    
    @patch('paho.mqtt.client.Client')
    def test_handler_initialization(self, mock_mqtt):
        """Тест инициализации handler"""
        handler = MQTTCommandHandler(
            device_id=self.device_id,
            hostname=self.mqtt_host,
            port=self.mqtt_port
        )
        
        self.assertEqual(handler.device_id, self.device_id)
        self.assertEqual(handler.command_topic, f"device/{self.device_id}/control")
        self.assertEqual(handler.response_topic, f"device/{self.device_id}/response")
    
    @patch('paho.mqtt.client.Client')
    @patch('requests.request')
    def test_execute_simple_get_request(self, mock_request, mock_mqtt):
        """Тест выполнения простого GET запроса"""
        # Настраиваем mock для HTTP ответа
        mock_response = Mock()
        mock_response.status_code = 200
        mock_response.text = '{"status": "ok", "hashrate": 100}'
        mock_request.return_value = mock_response
        
        handler = MQTTCommandHandler(
            device_id=self.device_id,
            hostname=self.mqtt_host,
            port=self.mqtt_port
        )
        
        # Тестовая команда
        asic_info = {
            "ip": "192.168.1.10",
            "firmware": "antminer",
            "port": 80,
            "scheme": "http"
        }
        
        steps = [
            {
                "id": "get_status",
                "request": {
                    "method": "GET",
                    "path": "/api/status",
                    "headers": {},
                    "timeoutMs": 5000
                },
                "extract": {}
            }
        ]
        
        policy = {
            "maxRetries": 1,
            "retryDelayMs": 100
        }
        
        # Выполняем команду
        result = handler._execute_asic_command(asic_info, steps, policy)
        
        # Проверяем результат
        self.assertEqual(result['status'], 'SUCCESS')
        self.assertEqual(len(result['stepResults']), 1)
        self.assertEqual(result['stepResults'][0]['status'], 'success')
        self.assertEqual(result['stepResults'][0]['statusCode'], 200)
    
    @patch('paho.mqtt.client.Client')
    @patch('requests.request')
    def test_execute_with_variable_extraction(self, mock_request, mock_mqtt):
        """Тест извлечения переменных и их использования в следующих шагах"""
        # Настраиваем mock для HTTP ответов
        def mock_request_side_effect(*args, **kwargs):
            url = kwargs.get('url', '')
            
            # Первый запрос - логин
            if '/login' in url:
                mock_resp = Mock()
                mock_resp.status_code = 200
                mock_resp.text = '{"token": "abc123"}'
                return mock_resp
            
            # Второй запрос - с токеном
            if 'Bearer abc123' in kwargs.get('headers', {}).get('Authorization', ''):
                mock_resp = Mock()
                mock_resp.status_code = 200
                mock_resp.text = '{"result": "success"}'
                return mock_resp
            
            # Неавторизованный запрос
            mock_resp = Mock()
            mock_resp.status_code = 401
            mock_resp.text = '{"error": "unauthorized"}'
            return mock_resp
        
        mock_request.side_effect = mock_request_side_effect
        
        handler = MQTTCommandHandler(
            device_id=self.device_id,
            hostname=self.mqtt_host,
            port=self.mqtt_port
        )
        
        # Команда с извлечением переменной
        asic_info = {
            "ip": "192.168.1.10",
            "port": 80,
            "scheme": "http"
        }
        
        steps = [
            {
                "id": "login",
                "request": {
                    "method": "POST",
                    "path": "/login",
                    "headers": {},
                    "body": {"user": "admin"},
                    "timeoutMs": 5000
                },
                "extract": {
                    "sessionToken": "$.token"
                }
            },
            {
                "id": "use_token",
                "request": {
                    "method": "GET",
                    "path": "/api/data",
                    "headers": {
                        "Authorization": "Bearer {sessionToken}"
                    },
                    "timeoutMs": 5000
                },
                "extract": {}
            }
        ]
        
        policy = {"maxRetries": 0, "retryDelayMs": 100}
        
        # Выполняем команду
        result = handler._execute_asic_command(asic_info, steps, policy)
        
        # Проверяем что оба шага успешны
        self.assertEqual(result['status'], 'SUCCESS')
        self.assertEqual(len(result['stepResults']), 2)
    
    @patch('paho.mqtt.client.Client')
    @patch('requests.request')
    def test_retry_on_failure(self, mock_request, mock_mqtt):
        """Тест повторных попыток при ошибке"""
        # Первые 2 попытки - ошибка, третья - успех
        call_count = [0]
        
        def mock_request_side_effect(*args, **kwargs):
            call_count[0] += 1
            if call_count[0] < 3:
                raise Exception("Connection failed")
            
            mock_resp = Mock()
            mock_resp.status_code = 200
            mock_resp.text = '{"status": "ok"}'
            return mock_resp
        
        mock_request.side_effect = mock_request_side_effect
        
        handler = MQTTCommandHandler(
            device_id=self.device_id,
            hostname=self.mqtt_host,
            port=self.mqtt_port
        )
        
        asic_info = {
            "ip": "192.168.1.10",
            "port": 80,
            "scheme": "http"
        }
        
        steps = [
            {
                "id": "flaky_request",
                "request": {
                    "method": "GET",
                    "path": "/api/status",
                    "timeoutMs": 5000
                },
                "extract": {}
            }
        ]
        
        policy = {
            "maxRetries": 3,
            "retryDelayMs": 10  # Короткая задержка для теста
        }
        
        # Выполняем команду
        result = handler._execute_asic_command(asic_info, steps, policy)
        
        # Проверяем что команда выполнена после повторов
        self.assertEqual(result['status'], 'SUCCESS')
        self.assertEqual(call_count[0], 3)  # 3 попытки


if __name__ == '__main__':
    unittest.main()
