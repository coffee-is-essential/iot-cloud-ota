from enum import Enum

DEFAULT_DOWNLOAD_CHUNK_SIZE = 1024 * 10  # 10KB
DEFAULT_SLEEP_INTERVAL = 1 # 1초

# MQTT Topics
BASE_TOPIC = "v1/{device_id}"
FIRMWARE_DOWNLOAD_REQUEST_TOPIC = BASE_TOPIC + "/update/request/firmware"
ADVERTISEMENT_DOWNLOAD_REQUEST_TOPIC = BASE_TOPIC + "/update/request/advertisement"

DOWNLOAD_ACK_TOPIC = BASE_TOPIC + "/update/request/ack"
DOWNLOAD_PROGRESS_TOPIC = BASE_TOPIC + "/update/progress"
DOWNLOAD_RESULT_TOPIC = BASE_TOPIC + "/update/result"
SYSTEM_STATUS_TOPIC = BASE_TOPIC + "/status/system"

# Firmware
FIRMWARE_VERSION = "1.0.0"
DOWNLOAD_PATH = "downloads"


class RequestStatus(Enum):
    ACKNOWLEDGED = "ACKNOWLEDGED"


class ResultStatus(Enum):
    SUCCESS = "SUCCESS"
    FAILED = "ERROR"
    TIMEOUT = "TIMEOUT"
