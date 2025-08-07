package mqttclient

import (
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/types"
)

func (m *MQTTClient) SubscribeDownloadRequestAck() {
	// TODO: Topic 변경
	//const topic = "v1/{그룹 ID}/{기기 ID}/firmware/download/request/ack"
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

		// TODO: DB 저장 또는 상태 업데이트 가능
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] ACK 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

func (m *MQTTClient) SubscribeDownloadProgress() {
	// TODO: Topic 변경
	//const topic = "v1/{그룹 ID}/{기기 ID}/firmware/download/progress"
	const topic = "test/topic"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] ACK 수신 - 토픽: %s", msg.Topic())

		var progress types.FirmwareProgress
		if err := json.Unmarshal(msg.Payload(), &progress); err != nil {
			log.Printf("[ERROR] ACK 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[PROGRESS] Command ID: %s", progress.CommandID)
		log.Printf("[PROGRESS] %d%% (%d / %d bytes), %d kbps",
			progress.Progress, progress.DownloadedBytes, progress.TotalBytes, progress.SpeedKbps)
		log.Printf("[PROGRESS] ETA: %d초", progress.EtaSeconds)
		log.Printf("[PROGRESS] Timestamp: %s", progress.Timestamp)

		// TODO: DB 저장 또는 상태 업데이트 가능
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] ACK 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}
