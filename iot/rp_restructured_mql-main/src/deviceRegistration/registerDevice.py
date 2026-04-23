import yaml
import uuid
import requests
from src.registrys.CommonRegistry import CommonRegistry
from copy import deepcopy

def get_logger():
    """Get logger instance, fallback to print if not available"""
    try:
        from src.logging import get_logger as get_logger_impl
        return get_logger_impl()
    except (ImportError, RuntimeError):
        # Logger not yet initialized, use print
        return None

def log_info(message: str):
    logger = get_logger()
    if logger:
        logger.info(message)
    else:
        print(f"INFO: {message}")

def log_warning(message: str):
    logger = get_logger()
    if logger:
        logger.warning(message)
    else:
        print(f"WARNING: {message}")

def log_error(message: str):
    logger = get_logger()
    if logger:
        logger.error(message)
    else:
        print(f"ERROR: {message}")

def sendRegistrationRequest(userConfigRegistry: CommonRegistry,
                            systemConfigRegistry: CommonRegistry) -> dict[str, str]:

    log_info("Preparing device registration request...")

    asicList: list[dict] = []
    tempSensorsList: list[dict] = []

    for mp in userConfigRegistry.getParameterByPath("metricProviders"):
        type_ = mp["type"]
        if type_ == "TemperatureSensorMock" or type_ == "TemperatureSensor":
            tempSensorsList.append({
                "name": mp["name"],
            })
        if type_ == "ASICMock" or type_ == "ASIC":
            asicList.append({
                "name": mp["name"],
                "model": mp.get("model", "default"),
                "vendor": mp.get("vendor", "default"),
            })

    log_info(f"Found {len(asicList)} ASIC(s) and {len(tempSensorsList)} temperature sensor(s)")

    conditionList: list[dict] = []
    for cd in userConfigRegistry.getParameterByPath("conditions"):
        conditionList.append({
            "name": cd["name"],
            "type": cd["type"],
            "value": cd["value"],
        })

    log_info(f"Found {len(conditionList)} condition(s)")

    sensorGroupList: list[dict] = []
    aggParams = userConfigRegistry.getParameterByPath("metricAggregators")
    if aggParams and isinstance(aggParams, list):
        for ad in userConfigRegistry.getParameterByPath("metricAggregators"):
            sensorGroupList.append({
                "name": ad["name"],
                "aggregationMethod": ad["type"],
            })
        log_info(f"Found {len(sensorGroupList)} sensor group(s)")

    # Получаем данные для регистрации
    backend_url = systemConfigRegistry.getParameterByPath("backendUrl")
    api_key = systemConfigRegistry.getParameterByPath("apiKey")
    user_email = systemConfigRegistry.getParameterByPath("userEmail")
    telegram = systemConfigRegistry.getParameterByPath("telegram", "@default")
    ip_address = systemConfigRegistry.getParameterByPath("ipAddress", "192.168.1.100")

    log_info(f"Backend URL: {backend_url}")
    log_info(f"User email: {user_email}")

    registration_payload = {
        "email": user_email,
        "telegram": telegram,
        "ipAdress": ip_address,  # Замечание: в API опечатка ipAdress
        "asic": asicList,
        "temperatureSensor": tempSensorsList,
        "condition": conditionList,
        "sensorGroup": sensorGroupList,
    }

    try:
        log_info(f"Sending registration request to {backend_url}/api/device-auth/signup...")

        # Отправляем запрос на бэкенд
        response = requests.post(
            f"{backend_url}/api/device-auth/signup",
            json=registration_payload,
            headers={
                "X-API-Key": api_key,
                "Content-Type": "application/json"
            },
            timeout=30
        )

        if response.status_code == 200:
            result = response.json()
            device_id = result.get('device_id')
            log_info(f"Device registered successfully with ID: {device_id}")

            # Преобразуем snake_case в camelCase (если нужно)
            # Бэкенд возвращает: device_id, asic, temperature_sensor, condition, sensor_group
            # Каждый - это словарь {name: id}

            name_map = {}

            # Device ID
            name_map["deviceId"] = device_id

            # ASIC IDs
            if "asic" in result:
                for name, id_val in result["asic"].items():
                    name_map[name] = id_val
                    log_info(f"  ASIC '{name}' registered with ID: {id_val}")

            # Temperature Sensor IDs
            if "temperature_sensor" in result:
                for name, id_val in result["temperature_sensor"].items():
                    name_map[name] = id_val
                    log_info(f"  Temperature sensor '{name}' registered with ID: {id_val}")

            # Condition IDs
            if "condition" in result:
                for name, id_val in result["condition"].items():
                    name_map[name] = id_val
                    log_info(f"  Condition '{name}' registered with ID: {id_val}")

            # Sensor Group IDs
            if "sensor_group" in result:
                for name, id_val in result["sensor_group"].items():
                    name_map[name] = id_val
                    log_info(f"  Sensor group '{name}' registered with ID: {id_val}")

            return name_map

        else:
            log_error(f"Registration failed with status {response.status_code}: {response.text}")
            # Fallback: генерируем локальные ID
            return generate_fallback_ids(registration_payload)

    except Exception as e:
        log_error(f"Failed to register device: {e}")
        # Fallback: генерируем локальные ID
        return generate_fallback_ids(registration_payload)

def generate_fallback_ids(registration_payload: dict) -> dict[str, str]:
    """
    Генерирует локальные ID если регистрация на бэкенде не удалась
    """
    log_warning("Using fallback ID generation (backend registration failed)")
    name_map = {"deviceId": generate_id()}
    log_info(f"Generated fallback device ID: {name_map['deviceId']}")

    for asic in registration_payload.get("asic", []):
        name_map[asic["name"]] = generate_id()
        log_info(f"  Generated fallback ID for ASIC '{asic['name']}': {name_map[asic['name']]}")

    for sensor in registration_payload.get("temperatureSensor", []):
        name_map[sensor["name"]] = generate_id()
        log_info(f"  Generated fallback ID for sensor '{sensor['name']}': {name_map[sensor['name']]}")

    for condition in registration_payload.get("condition", []):
        name_map[condition["name"]] = generate_id()
        log_info(f"  Generated fallback ID for condition '{condition['name']}': {name_map[condition['name']]}")

    for group in registration_payload.get("sensorGroup", []):
        name_map[group["name"]] = generate_id()
        log_info(f"  Generated fallback ID for group '{group['name']}': {name_map[group['name']]}")

    return name_map

def generate_id() -> str:
    return str(uuid.uuid4())

def transferUserConfigToInternalConfig(
    userConfigRegistry,
    nameMap: dict[str, str]
) -> dict:
    log_info("Transferring user config to internal config format...")
    resultingConfig: dict = {}
    data = deepcopy(userConfigRegistry.data)
    for group, items in data.items():
        if not isinstance(items, list):
            resultingConfig[group] = items
            continue
        resultingConfig[group] = []
        for instanceDesc in items:
            instance = deepcopy(instanceDesc)
            name = instance.get("name")
            if not name:
                raise ValueError(f"Object in group '{group}' has no name")
            if name in nameMap:
                instance["id"] = nameMap[name]
            else:
                new_id = generate_id()
                instance["id"] = new_id
                nameMap[name] = new_id
                log_warning(f"Generated missing ID for '{name}' in group '{group}': {new_id}")
            instance.pop("name")
            resultingConfig[group].append(instance)
    for group, items in resultingConfig.items():
        if not isinstance(items, list):
            continue
        for instance in items:
            if "input" not in instance:
                continue
            inputs = instance["input"]
            if isinstance(inputs, list):
                instance["input"] = [nameMap[name] for name in inputs]
            else:
                instance["input"] = nameMap[inputs]
    reversedNameMap = {nameMap[key]: key for key in nameMap.keys()}
    resultingConfig["nameMap"] = reversedNameMap
    log_info("Config transfer completed successfully")
    return resultingConfig

def registerDevice(
        systemRegistry: CommonRegistry,
        userConfigRegistry: CommonRegistry,
        savePath: str
) -> None:
    log_info("Starting device registration process...")
    nameMap = sendRegistrationRequest(userConfigRegistry, systemRegistry)
    resultingConfig = transferUserConfigToInternalConfig(userConfigRegistry, nameMap)
    resultingConfig["deviceId"] = nameMap["deviceId"]
    resultingConfig["mqlTimeOut"] = userConfigRegistry.getParameterByPath("mqlTimeOut")

    log_info(f"Saving internal config to: {savePath}")
    with open(savePath, "w") as conf:
        yaml.safe_dump(resultingConfig, conf, sort_keys=False)
    log_info("Device registration completed successfully")

