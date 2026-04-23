import serial
import json
import time

def get_logger():
    """Get logger instance"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        return None

ser = None

# TODO tty reboot
# TODO fix parsing

def init_serial(port="/dev/ttyUSB0", baudrate=9600, timeout=2):
    global ser
    logger = get_logger()

    try:
        ser = serial.Serial(port, baudrate, timeout=timeout) # TODO TODO

        # Даем Arduino больше времени на инициализацию
        time.sleep(3)

        # Очищаем ВСЕ начальные сообщения от Arduino
        ser.reset_input_buffer()
        time.sleep(0.5)

        # Читаем и игнорируем все, что Arduino отправил при старте
        start_time = time.time()
        while time.time() - start_time < 5:  # Ждем до 5 секунд
            if ser.in_waiting > 0:
                line = ser.readline()
                if logger:
                    logger.debug(f"Clearing serial buffer: {line}")
                # Если получили сообщение о готовности, выходим
                if b'ready' in line.lower() or b'Arduino' in line:
                    if logger:
                        logger.info("Arduino reported ready")
                    break
            time.sleep(0.1)

        # Финальная очистка буфера
        ser.reset_input_buffer()
        time.sleep(0.1)

        if logger:
            logger.info(f"Serial connection initialized on {port}")
        return True

    except Exception as e:
        if logger:
            logger.error(f"Serial initialization error: {e}")
        return False


def read_temperatures(pin_list, port="/dev/ttyUSB0", baudrate=9600, timeout=2):
    global ser
    logger = get_logger()

    if ser is None:
        if not init_serial(port, baudrate, timeout):
            raise Exception("Failed to initialize serial connection")

    payload = json.dumps(pin_list) + "\n"
    ser.write(payload.encode("ascii"))
    ser.flush()

    response = ser.readline()
    while response.strip() == b'':
        response = ser.readline()

    try:
        result = float(json.loads(response.decode().strip())[0])
        if logger:
            logger.debug(f"Temperature read from pins {pin_list}: {result}")
        return result
    except Exception as e:
        error_msg = f"Temperature parsing error: {e}, Response: {response}"
        if logger:
            logger.error(error_msg)
        raise Exception(error_msg)