import dataclasses
import hashlib
import json
import logging
import threading
import time
from datetime import datetime, timezone

import http_client as http
import mqtt_client as mqtt
from constants import RequestStatus, ResultStatus

logger = logging.getLogger(__name__)


@dataclasses.dataclass
class FirmwareDownloadRequest:
    command_id: str
    signed_url: str
    version: str
    checksum: str
    size: int
    timeout: int = 300  # 기본 타임아웃 300초
    timestamp: str = dataclasses.field(
        default_factory=lambda: datetime.now(timezone.utc)
        .isoformat()
        .replace("+00:00", "Z")
    )


class FirmwareManager:
    """펌웨어 다운로드 및 업데이트 프로세스의 전체 로직을 관리하는 클래스."""

    def __init__(
        self,
        device_id: str,
        mqtt_client: mqtt.MqttClient,
        http_client: http.HttpClient,
    ):
        """FirmwareManager를 초기화합니다.

        Args:
            device_id: 시뮬레이션할 디바이스의 고유 ID.
            mqtt_client: MQTT 통신을 위한 클라이언트 인스턴스.
            http_client: 파일 다운로드를 위한 클라이언트 인스턴스.
        """
        self._device_id = device_id
        self._mqtt_client = mqtt_client
        self._http_client = http_client
        self._download_thread = None
        logger.info("FirmwareManager has been initialized successfully.")

    def handle_download_request(self, topic: str, payload: bytes) -> None:
        """펌웨어 다운로드 요청 MQTT 메시지를 처리하는 콜백 함수.

        메시지를 파싱하고, ACK를 전송한 후, 별도의 스레드에서 다운로드를 시작합니다.
        """
        logger.info("Received firmware download request on topic '%s'", topic)
        try:
            data = json.loads(payload)
            download_request = FirmwareDownloadRequest(**data)
        except (json.JSONDecodeError, TypeError, KeyError) as e:
            logger.info("Invalid download request payload: %s", e)
            return

        # 이미 다른 다운로드가 진행 중인지 확인
        if self._download_thread and self._download_thread.is_alive():
            logger.info(
                "Firmware download is already in progress. Ignoring new request %s.",
                download_request.command_id,
            )
            return

        # 1. 요청 수신 확인 (ACK) 메시지 전송
        self._send_ack(download_request.command_id)

        # 2. 메인 스레드를 블로킹하지 않기 위해 별도의 스레드에서 다운로드 시작
        self._download_thread = threading.Thread(
            target=self._download_firmware, args=(download_request,)
        )
        self._download_thread.start()

    def _send_ack(self, command_id: str) -> None:
        """펌웨어 다운로드 요청에 대한 ACK 메시지를 MQTT로 발행합니다."""
        topic = f"v1/{self._device_id}/firmware/download/request/ack"
        payload = {
            "command_id": command_id,
            "status": RequestStatus.ACKNOWLEDGED.value,
            "message": "Firmware download request received",
            "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        }
        self._mqtt_client.publish(topic, json.dumps(payload))

    def _send_progress(
        self,
        command_id: str,
        downloaded_bytes: int,
        total_bytes: int,
        start_time: float,
    ) -> None:
        """펌웨어 다운로드 진행 상황을 MQTT로 발행합니다.

        현재 다운로드 된 바이트 수와 전체 바이트 수를
        기반으로 진행률, 속도, ETA 등을 계산하여 전송합니다.

        Args:
            command_id: 다운로드 명령의 고유 ID.
            downloaded_bytes: 현재까지 다운로드된 바이트 수.
            total_bytes: 다운로드할 전체 바이트 수.
            start_time: 다운로드 시작 시각 (epoch time).
        """
        if total_bytes == 0:
            return

        progress = int((downloaded_bytes / total_bytes) * 100)
        elapsed_time = time.time() - start_time
        speed_bps = (downloaded_bytes / elapsed_time) if elapsed_time > 0 else 0
        speed_kbps = speed_bps / 1024
        eta_seconds = (
            ((total_bytes - downloaded_bytes) / speed_bps) if speed_bps > 0 else 0
        )

        topic = f"v1/{self._device_id}/firmware/download/progress"
        payload = {
            "command_id": command_id,
            "progress": progress,
            "downloaded_bytes": downloaded_bytes,
            "total_bytes": total_bytes,
            "speed_kbps": round(speed_kbps, 2),
            "eta_seconds": int(eta_seconds),
            "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        }
        self._mqtt_client.publish(topic, json.dumps(payload))

    def _send_result(
        self,
        command_id: str,
        status: ResultStatus,
        message: str,
        checksum_verified: bool,
        download_time: float,
    ) -> None:
        """펌웨어 다운로드 최종 결과를 MQTT로 발행합니다.

        Args:
            command_id: 다운로드 명령의 고유 ID.
            status: 다운로드 상태 ("success", "failed", "timeout" 등).
            message: 상태에 대한 상세 메시지.
            checksum_verified: 체크섬 검증 결과 (True/False).
            download_time: 다운로드에 소요된 시간 (초).
        """
        topic = f"v1/{self._device_id}/firmware/download/result"
        payload = {
            "command_id": command_id,
            "status": status.value,
            "message": message,
            "checksum_verified": checksum_verified,
            "download_time": round(download_time, 2),
            "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        }
        self._mqtt_client.publish(topic, json.dumps(payload))

    def _verify_checksum(
        self,
        file_path: str,
        expected_checksum: str,
    ) -> bool:
        """다운로드된 파일의 체크섬을 검증합니다.

        SHA256 알고리즘을 사용하여 파일의 체크섬을 계산하고,
        기대하는 체크섬과 비교합니다. 이 때, 메모리 사용량을 줄이기 위해
        파일을 청크 단위로 읽어 처리합니다.

        Args:
            file_path: 검증할 파일의 경로.
            expected_checksum_str: 기대하는 체크섬 문자열

        Returns:
            tuple: (검증 성공 여부, 실제 계산된 체크섬 또는 오류 메시지)
        """
        hasher = hashlib.sha256()
        try:
            with open(file_path, "rb") as f:
                # 파일을 4KB 청크 단위로 읽어 메모리 사용량을 줄임. (IoT 기기와 비슷한 환경 조성)
                for chunk in iter(lambda: f.read(4096), b""):
                    hasher.update(chunk)
            actual_hash = hasher.hexdigest()
            return actual_hash == expected_checksum
        except FileNotFoundError:
            logger.error("File not found for checksum verification: %s", file_path)
            return False
        except Exception as e:
            logger.error("Error during checksum verification: %s", e)
            return False

    def _download_firmware(self, download_request: FirmwareDownloadRequest) -> None:
        """HTTP 클라이언트를 사용하여 펌웨어 파일을 다운로드하는 전체 프로세스.

        다운로드 진행 상황을 주기적으로 MQTT로 전송하고,
        완료 후 체크섬을 검증하여 최종 결과를 전송합니다.

        Args:
            download_request: 펌웨어 다운로드 요청 정보가 담긴 데이터 클래스 인스턴스.
        """
        start_time = time.time()

        # 최종 결과 전송을 위한 기본값 설정
        status: ResultStatus = ResultStatus.FAILED
        message = ""
        checksum_verified = False

        try:
            file_path = self._http_client.download_file_with_progress(
                signed_url=download_request.signed_url,
                total_size=download_request.size,
                progress_callback=lambda downloaded: self._send_progress(
                    download_request.command_id,
                    downloaded,
                    download_request.size,
                    start_time,
                ),
                timeout=download_request.timeout,
            )

            is_valid = self._verify_checksum(file_path, download_request.checksum)

            if is_valid:
                # 성공했으므로 상태 업데이트
                status = ResultStatus.SUCCESS
                message = "Download completed successfully"
                checksum_verified = True
            else:
                # 상태는 FAILED 유지
                message = "Checksum mismatch."
                checksum_verified = False

        except TimeoutError as e:
            status = ResultStatus.TIMEOUT
            message = str(e)
        except Exception as e:
            # 그 외 모든 예외 처리
            status = ResultStatus.FAILED
            message = f"An unexpected error occurred: {e}"
        finally:
            # 성공, 실패, 타임아웃 등 모든 경우에 마지막에 한번만 결과 전송
            download_time = time.time() - start_time
            self._send_result(
                download_request.command_id,
                status,
                message,
                checksum_verified,
                download_time,
            )
