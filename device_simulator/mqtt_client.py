import logging
from typing import Callable

import paho.mqtt.client as mqtt

logger = logging.getLogger(__name__)


class MqttClient:
    """여러 토픽을 구독하고 메시지를 발행할 수 있는 MQTT 클라이언트 클래스."""

    def __init__(
        self,
        broker_url: str,
        broker_port: int,
        client_id: str,
    ) -> None:
        self._broker_url = broker_url
        self._broker_port = broker_port
        self._client_id = client_id
        self._callbacks = {}

        self._client = mqtt.Client(client_id=self._client_id)
        self._client.on_message = self._on_message

    def connect(self):
        """브로커에 연결하고 네트워크 루프를 시작합니다."""
        self._client.connect(self._broker_url, self._broker_port)
        self._client.loop_start()
        logger.info(
            "MQTT Client has been connected to broker at %s:%d",
            self._broker_url,
            self._broker_port,
        )

    def _on_message(
        self,
        client: mqtt.Client,
        _,
        msg: mqtt.MQTTMessage,
    ) -> None:
        """메시지를 수신했을 때 호출되는 내부 콜백."""
        # 등록된 모든 구독 토픽(sub)에 대해 수신된 메시지 토픽(msg.topic)이 매치되는지 확인
        for sub, callback in self._callbacks.items():
            if mqtt.topic_matches_sub(sub, msg.topic):
                try:
                    callback(msg.topic, msg.payload)
                except Exception as e:
                    logger.error("Error in callback for topic '%s': %s", msg.topic, e)
                break

    def publish(
        self,
        topic: str,
        payload: str,
    ) -> None:
        """지정된 토픽으로 메시지를 발행합니다."""
        result = self._client.publish(topic, payload)
        if result[0] == 0:
            logger.info(
                "Successfully published to topic '%s' with payload: %s",
                topic,
                payload,
            )
        else:
            logger.error("Failed to publish to topic '%s'", topic)

    def subscribe(
        self,
        topic: str,
        callback: Callable,
    ) -> None:
        """토픽을 구독하고, 해당 토픽에 메시지가 오면 실행될 콜백 함수를 등록합니다."""
        logger.info("Subscribing to topic: %s", topic)
        self._callbacks[topic] = callback
        self._client.subscribe(topic)

    def disconnect(self) -> None:
        """브로커와의 연결을 종료합니다."""
        logger.info("Disconnecting from broker...")
        self._client.loop_stop()
        self._client.disconnect()
        logger.info("Disconnected.")

    def loop_forever(self) -> None:
        """네트워크 루프를 무한히 실행합니다. (블로킹 호출)"""
        self._client.loop_forever()
