import logging
import os
import time

from constants import DEFAULT_DOWNLOAD_CHUNK_SIZE
from firmware_manager import FirmwareManager
from http_client import HttpClient
from mqtt_client import MqttClient

logger = logging.getLogger(__name__)
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)


class Config:
    """시뮬레이터의 설정을 관리하는 클래스."""

    DEVICE_ID = os.getenv("DEVICE_ID", "1")
    BROKER_URL = os.getenv("BROKER_URL", "test.mosquitto.org")
    BROKER_PORT = int(os.getenv("BROKER_PORT", 1883))
    DOWNLOAD_CHUNK_SIZE = int(
        os.getenv("DOWNLOAD_CHUNK_SIZE", DEFAULT_DOWNLOAD_CHUNK_SIZE)
    )


class Simulator:
    """IoT 디바이스 시뮬레이터를 설정하고 실행하는 메인 클래스."""

    def __init__(
        self,
        mqtt_client: MqttClient,
        http_client: HttpClient,
        config: Config,
    ):
        """Simulator를 초기화하고 필요한 컴포넌트들을 설정합니다."""
        self._mqtt_client = mqtt_client
        self._http_client = http_client
        self._config = config

        # 펌웨어 업데이트 로직을 처리할 FirmwareManager 인스턴스 생성
        self._firmware_manager = FirmwareManager(
            device_id=self._config.DEVICE_ID,
            mqtt_client=self._mqtt_client,
            http_client=self._http_client,
        )

    def run(self):
        """시뮬레이터를 시작합니다."""
        # MQTT 브로커에 연결
        self._mqtt_client.connect()

        # 펌웨어 다운로드 요청 토픽 구독
        self._mqtt_client.subscribe(
            f"v1/{self._config.DEVICE_ID}/firmware/download/request",
            callback=self._firmware_manager.handle_download_request,
        )

    def stop(self):
        """시뮬레이터를 중지합니다."""
        self._mqtt_client.disconnect()


if __name__ == "__main__":
    # 1. 설정 초기화
    config = Config()

    # 2. 클라이언트 인스턴스 생성
    http_client = HttpClient(config.DOWNLOAD_CHUNK_SIZE)
    mqtt_client = MqttClient(
        config.BROKER_URL,
        config.BROKER_PORT,
        config.DEVICE_ID,
    )

    # 3. 시뮬레이터 인스턴스 생성 및 실행
    simulator = Simulator(mqtt_client, http_client, config)
    simulator.run()

    logger.info("====== Device Simulator started. ======")
    logger.info("Device ID  : %s", config.DEVICE_ID)
    logger.info("Broker URL : %s", config.BROKER_URL)
    logger.info("Broker Port: %d", config.BROKER_PORT)
    logger.info("=======================================")
    logger.info(
        "Listening for messages on topic: v1/%s/firmware/download/request",
        config.DEVICE_ID,
    )
    logger.info("Press Ctrl+C to exit.")

    try:
        # MQTT 클라이언트가 백그라운드에서 메시지를 처리하므로
        # 메인 스레드는 스크립트가 종료되지 않도록 대기
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        logger.info("\nExiting...")
    finally:
        simulator.stop()
