import logging
import os
from datetime import datetime
from pathlib import Path
from typing import Optional
from src.logging.MQTTLoggingHandler import MQTTLoggingHandler
from src.mqtt.MQTTPublisher import MQTTPublisher


_logger_instance: Optional[logging.Logger] = None


def setup_logger(
    device_id: str,
    mqtt_publisher: MQTTPublisher,
    log_dir: str = "/var/cryptoterm",
    level: int = logging.INFO
) -> logging.Logger:
    """
    Setup the main application logger with multiple handlers:
    - Console output
    - File output to /var/cryptoterm/{date}{time}.log
    - MQTT publisher to rp/logs topic
    
    Args:
        device_id: Device identifier
        mqtt_publisher: MQTT publisher instance for logs
        log_dir: Directory to store log files (default: /var/cryptoterm)
        level: Logging level (default: INFO)
    
    Returns:
        Configured logger instance
    """
    global _logger_instance
    
    # Create logger
    logger = logging.getLogger("cryptoterm")
    logger.setLevel(level)
    
    # Clear existing handlers to avoid duplicates
    logger.handlers.clear()
    
    # Create formatter
    formatter = logging.Formatter(
        '%(asctime)s - %(name)s - %(levelname)s - %(message)s',
        datefmt='%Y-%m-%d %H:%M:%S'
    )
    
    # Console handler
    console_handler = logging.StreamHandler()
    console_handler.setLevel(level)
    console_handler.setFormatter(formatter)
    logger.addHandler(console_handler)
    
    # File handler
    try:
        # Create log directory if it doesn't exist
        log_path = Path(log_dir)
        log_path.mkdir(parents=True, exist_ok=True)
        
        # Create log file with timestamp
        log_filename = datetime.now().strftime("%Y%m%d_%H%M%S.log")
        log_filepath = log_path / log_filename
        
        file_handler = logging.FileHandler(log_filepath, encoding='utf-8')
        file_handler.setLevel(level)
        file_handler.setFormatter(formatter)
        logger.addHandler(file_handler)
        
        logger.info(f"Log file created: {log_filepath}")
    except (PermissionError, OSError) as e:
        # If we can't create log file, log to console but continue
        logger.warning(f"Could not create log file in {log_dir}: {e}. Logging to console only.")
    
    # MQTT handler
    try:
        mqtt_handler = MQTTLoggingHandler(mqtt_publisher, device_id)
        mqtt_handler.setLevel(level)
        mqtt_handler.setFormatter(formatter)
        logger.addHandler(mqtt_handler)
        
        logger.info(f"MQTT logging handler initialized for device {device_id}")
    except Exception as e:
        logger.warning(f"Could not initialize MQTT logging handler: {e}")
    
    # Prevent propagation to root logger
    logger.propagate = False
    
    _logger_instance = logger
    return logger


def get_logger() -> logging.Logger:
    """
    Get the configured logger instance.
    
    Returns:
        Logger instance
    
    Raises:
        RuntimeError: If logger has not been initialized with setup_logger()
    """
    if _logger_instance is None:
        raise RuntimeError("Logger not initialized. Call setup_logger() first.")
    return _logger_instance
