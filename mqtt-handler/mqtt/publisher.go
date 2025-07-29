package mqtt

import (
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
)

var Client mqtt.Client

/*
MQTT 클라이언트를 사용해 지정된 토픽으로 JSON 형태의 펌웨어 요청 메시지를 전송합니다.

Parameters:
- client: 연결된 MQTT 클라이언트
- topic: 메시지를 publish할 MQTT 토픽
- payload: 전송할 데이터 (임의의 구조체, JSON으로 직렬화됨)
*/
func PublishFirmwareRequest(client mqtt.Client, topic string, payload any) {
	// payload를 JSON 바이트 배열로 직렬화
	jsonBytes, err := json.Marshal(payload)
	if err != nil {
		log.Printf("JSON 변환 실패: %v", err)
		return
	}

	/*
		MQTT 토픽으로 메시지 publish (QOS 2, retain true)
		qos - 2: 정확히 한 번 전송. 송신자와 수신자가 두 번의 핸드셰이크를 통해 중복 없이 보장

		retain - true로 설정하면 브로커가 해당 토픽에 마지막으로 publish된 메시지를 저장, 이후
		누군가가 해당 토픽을 구독하면, 브로커는 즉시 저장된 메시지를 보내줌
	*/
	token := client.Publish(topic, 2, true, jsonBytes)

	// publish가 완료될 때까지 대기
	token.Wait()

	if token.Error() != nil {
		log.Printf("Publish 실패: %v", token.Error())
	}
}
