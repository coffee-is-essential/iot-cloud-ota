from enum import Enum

DEFAULT_DOWNLOAD_CHUNK_SIZE = 1024  # 1KB

# MQTT Topics
BASE_TOPIC = "v1/{device_id}"
FIRMWARE_DOWNLOAD_REQUEST_TOPIC = BASE_TOPIC + "/update/request/firmware"
FIRMWARE_DOWNLOAD_ACK_TOPIC = BASE_TOPIC + "/update/request/ack"
FIRMWARE_DOWNLOAD_PROGRESS_TOPIC = BASE_TOPIC + "/update/progress"
FIRMWARE_DOWNLOAD_RESULT_TOPIC = BASE_TOPIC + "/update/result"

# Firmware
FIRMWARE_VERSION = "1.0.0"
DOWNLOAD_PATH = "downloads"


class RequestStatus(Enum):
    ACKNOWLEDGED = "ACKNOWLEDGED"


class ResultStatus(Enum):
    SUCCESS = "SUCCESS"
    FAILED = "ERROR"
    TIMEOUT = "TIMEOUT"
