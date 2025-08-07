package mqttclient

import (
	"encoding/json"
	"fmt"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/types"
	"sync"
)

var Client mqtt.Client

/*
MQTT 클라이언트를 사용해 지정된 토픽으로 JSON 형태의 펌웨어 요청 메시지를 전송합니다.
토픽 ID: v1/{그룹 ID}/{기기 ID}/firmware/download/request

Parameters:
- client: 연결된 MQTT 클라이언트
- topic: 메시지를 publish할 MQTT 토픽
- payload: 전송할 데이터 (임의의 구조체, JSON으로 직렬화됨)
*/
func PublishUpdateRequest(client mqtt.Client, topic string, payload any) {
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

func (m *MQTTClient) PublishDownloadRequest(req *types.FirmwareDeployRequest) {

	var wg sync.WaitGroup
	num := 10
	for i := 0; i < num; i++ {
		wg.Add(1)
		go func(i int) {
			defer wg.Done()
			text := fmt.Sprintf("Message %d", i)
			token := m.mqttClient.Publish("v1/+/+/test", 0, false, text)
			token.Wait()
		}(i)
	}
	wg.Wait() // 모든 고루틴이 끝날 때까지 대기
}
