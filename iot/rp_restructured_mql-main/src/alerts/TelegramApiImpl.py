import os
import requests

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

def send_telegram(text):
    logger = get_logger()

    TELEGRAM_TOKEN = os.getenv("TELEGRAM_TOKEN")
    TELEGRAM_CHAT_ID = os.getenv("TELEGRAM_CHAT_ID")
    url = f"https://api.telegram.org/bot{TELEGRAM_TOKEN}/sendMessage"
    payload = {
        'chat_id': TELEGRAM_CHAT_ID,
        'text': text
    }
    headers = {
        "Content-Type": "application/json"
    }

    try:
        resp = requests.post(url, json=payload, headers=headers)

        if logger:
            logger.info(f"Telegram message sent - Status: {resp.status_code}")
            logger.debug(f"Telegram API response: {resp.json()}")

        return resp.status_code == 200
    except Exception as e:
        if logger:
            logger.error(f"Failed to send Telegram message: {e}")
        return False