from src.deviceRegistration.initializeMetricMapper import makeMetricMapper, MetricTopicMapper
from src.mqlInitializing.mqlInitializing import initializeMQL
from src.deviceRegistration.registerDevice import registerDevice
from src.registrys.CommonRegistry import CommonRegistry
from src.mqtt.MQTTPublisher import MQTTPublisher
from src.mqtt.MQTTCommandHandler import MQTTCommandHandler
from src.mqtt.MQTTRpCommandHandler import MQTTRpCommandHandler
from src.ledNotification.LedNotificator import LedNotificator
from src.logging import setup_logger
import uuid
from dotenv import load_dotenv
from pathlib import Path

load_dotenv()

SYSTEM_CONFIG_PATH = "/etc/cryptoterm/config/systemConfig.yaml"
USER_CONFIG_PATH = "/etc/cryptoterm/config/userConfig.yaml"
INTERNAL_CONFIG_PATH = "/etc/cryptoterm/config/internalConfig.yaml"

if __name__ == "__main__":
    # Load system configuration
    try:
        sysreg = CommonRegistry(SYSTEM_CONFIG_PATH)
    except Exception as e:
        print(f"FATAL: Failed to load system config from {SYSTEM_CONFIG_PATH}: {e}")
        exit(1)

    # Check and register device if needed
    internalPath = Path(INTERNAL_CONFIG_PATH)
    if not internalPath.is_file() and not internalPath.exists():
        print(f"Internal config not found, registering device...")
        try:
            userreg = CommonRegistry(USER_CONFIG_PATH)
            registerDevice(
                sysreg,
                userreg,
                INTERNAL_CONFIG_PATH,
            )
            print(f"Device registration completed")
        except Exception as e:
            print(f"FATAL: Device registration failed: {e}")
            exit(1)

    # Load internal configuration
    try:
        internalreg = CommonRegistry(INTERNAL_CONFIG_PATH)
        metricTopicMapper: MetricTopicMapper = makeMetricMapper(internalreg)
        device_id = internalreg.deviceId
        mqtt_host = sysreg.getParameterByPath("mqttHost")
    except Exception as e:
        print(f"FATAL: Failed to load internal config: {e}")
        exit(1)

    # Initialize MQTT clients
    client_id_publisher = f"{device_id}-publisher-{uuid.uuid4()}"
    client_id_control = f"{device_id}-control-{uuid.uuid4()}"
    client_id_controlRp = f"{device_id}-controlRp-{uuid.uuid4()}"
    client_id_logs = f"{device_id}-logs-{uuid.uuid4()}"

    # Create MQTT log publisher (must be first)
    log_publisher = MQTTPublisher(
        hostname=mqtt_host,
        topic="rp/logs",
        deviceId=device_id,
        port=1883,
        client_id=client_id_logs
    )

    try:
        log_publisher.connect()
    except Exception as e:
        print(f"WARNING: Failed to connect log publisher: {e}")

    # Setup logger with MQTT, console, and file handlers
    logger = setup_logger(
        device_id=device_id,
        mqtt_publisher=log_publisher,
        log_dir="/var/cryptoterm"
    )

    logger.info("="*60)
    logger.info("CryptoTerm Device Starting")
    logger.info(f"Device ID: {device_id}")
    logger.info(f"MQTT Host: {mqtt_host}")
    logger.info("="*60)

    # Initialize LED notification
    try:
        ledNotification = LedNotificator(sysreg.getParameterByPath("ledNotification.port"))
        logger.info("LED notificator initialized")
    except Exception as e:
        logger.error(f"Failed to initialize LED notificator: {e}")
        ledNotification = None

    # Create MQTT publishers and handlers
    publisher = MQTTPublisher(
        hostname=mqtt_host,
        topic=metricTopicMapper,
        deviceId=device_id,
        port=1883,
        client_id=client_id_publisher
    )

    command_handler = MQTTCommandHandler(
        device_id=device_id,
        hostname=mqtt_host,
        reg=internalreg,
        port=1883,
        client_id=client_id_control,
    )

    rp_command_handler = MQTTRpCommandHandler(
        device_id=device_id,
        hostname=mqtt_host,
        userReg=internalreg,
        client_id=client_id_controlRp,
        port=1883
    )

    try:
        # Connect MQTT handlers
        logger.info("Connecting MQTT handlers...")
        command_handler.connect()
        logger.info("Command handler connected")

        rp_command_handler.connect()
        logger.info("RP command handler connected")

        publisher.connect()
        logger.info("Metrics publisher connected")

        # Initialize MQL
        logger.info("Initializing MQL system...")
        mql = initializeMQL(internalreg, publisher)
        logger.info("MQL system initialized successfully")

        # Turn off LED (device is ready)
        if ledNotification:
            ledNotification.off()
            logger.info("LED notificator ready")

        # Get polling interval
        mql_timeout = internalreg.getParameterByPath("mqlTimeOut", 60)
        logger.info(f"Starting MQL polling loop with {mql_timeout}s interval")

        # Start MQL loop
        mql.run(mql_timeout, -1)

    except KeyboardInterrupt:
        logger.info("Received shutdown signal (Ctrl+C)")
    except Exception as e:
        logger.exception(f"Fatal error in main loop: {e}")
    finally:
        logger.info("Shutting down...")

        # Disconnect MQTT handlers
        try:
            command_handler.disconnect()
            logger.info("Command handler disconnected")
        except Exception as e:
            logger.error(f"Error disconnecting command handler: {e}")

        try:
            rp_command_handler.disconnect()
            logger.info("RP command handler disconnected")
        except Exception as e:
            logger.error(f"Error disconnecting RP command handler: {e}")

        try:
            publisher.disconnect()
            logger.info("Metrics publisher disconnected")
        except Exception as e:
            logger.error(f"Error disconnecting publisher: {e}")

        try:
            log_publisher.disconnect()
            logger.info("Log publisher disconnected")
        except Exception as e:
            logger.error(f"Error disconnecting log publisher: {e}")

        # Blink LED to indicate shutdown
        if ledNotification:
            try:
                ledNotification.blink()
            except Exception as e:
                logger.error(f"Error blinking LED: {e}")

        logger.info("Shutdown complete")
        logger.info("="*60)
