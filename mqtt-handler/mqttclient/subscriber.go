package mqttclient

import (
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/types"
)

func (m *MQTTClient) SubscribeDownloadRequestAck() {
	// TODO: Topic 변경
	//const topic = "v1/+/+/firmware/download/request/ack"
	const topic = "test/topic"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] ACK 수신 - 토픽: %s", msg.Topic())

		var ack types.FirmwareAck
		if err := json.Unmarshal(msg.Payload(), &ack); err != nil {
			log.Printf("[ERROR] ACK 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[ACK] Command ID: %s", ack.CommandID)
		log.Printf("[ACK] Status: %s", ack.Status)
		log.Printf("[ACK] Message: %s", ack.Message)
		log.Printf("[ACK] Timestamp: %s", ack.Timestamp)

		// TODO: 여기서 ack 정보 DB 저장 또는 상태 업데이트 가능
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] ACK 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}
