from enum import Enum

DEFAULT_DOWNLOAD_CHUNK_SIZE = 1024 * 100  # 100KB


class RequestStatus(Enum):
    """펌웨어 다운로드 요청 상태"""

    ACKNOWLEDGED = "ACKNOWLEDGED"


class ResultStatus(Enum):
    """펌웨어 다운로드 결과 상태"""

    SUCCESS = "SUCCESS"
    FAILED = "FAILED"
    CANCELLED = "CANCELLED"
    TIMEOUT = "TIMEOUT"
