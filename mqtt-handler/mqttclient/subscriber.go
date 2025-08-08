package mqttclient

import (
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/repository"
	"mqtt-handler/types"
	"strconv"
	"strings"
)

func (m *MQTTClient) SubscribeDownloadRequestAck() {
	const topic = "v1/+/+/+/firmware/download/request/ack"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] ACK 수신 - 토픽: %s", msg.Topic())

		var ack types.FirmwareDownloadRequestAck
		if err := json.Unmarshal(msg.Payload(), &ack); err != nil {
			log.Printf("[ERROR] ACK 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[ACK] Command ID: %s", ack.CommandID)
		log.Printf("[ACK] Status: %s", ack.Status)
		log.Printf("[ACK] Message: %s", ack.Message)
		log.Printf("[ACK] Timestamp: %s", ack.Timestamp)
		topicParts := strings.Split(msg.Topic(), "/")
		topicRegionId, _ := strconv.ParseInt(topicParts[1], 10, 64)
		topicGroupId, _ := strconv.ParseInt(topicParts[2], 10, 64)
		topicDeviceId, _ := strconv.ParseInt(topicParts[3], 10, 64)

		event := types.FirmwareDownloadEvent{
			CommandID:        ack.CommandID,
			GroupID:          topicGroupId,
			RegionID:         topicRegionId,
			DeviceID:         topicDeviceId,
			Message:          ack.Message,
			Status:           ack.Status,
			Progress:         0,
			TotalBytes:       0,
			DownloadBytes:    0,
			SpeedKbps:        0,
			ChecksumVerified: false,
			DownloadTime:     0,
		}

		repository.InsertChan <- event
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
	const topic = "v1/+/+/+/firmware/download/progress"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] PROGRESS 수신 - 토픽: %s", msg.Topic())

		var progress types.FirmwareDownloadProgress
		if err := json.Unmarshal(msg.Payload(), &progress); err != nil {
			log.Printf("[ERROR] PROGRESS 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[PROGRESS] Command ID: %s", progress.CommandID)
		log.Printf("[PROGRESS] %d%% (%d / %d bytes), %d kbps",
			progress.Progress, progress.DownloadedBytes, progress.TotalBytes, progress.SpeedKbps)
		log.Printf("[PROGRESS] ETA: %d초", progress.EtaSeconds)
		log.Printf("[PROGRESS] Timestamp: %s", progress.Timestamp)

		topicParts := strings.Split(msg.Topic(), "/")
		topicRegionId, _ := strconv.ParseInt(topicParts[1], 10, 64)
		topicGroupId, _ := strconv.ParseInt(topicParts[2], 10, 64)
		topicDeviceId, _ := strconv.ParseInt(topicParts[3], 10, 64)

		event := types.FirmwareDownloadEvent{
			CommandID:        progress.CommandID,
			GroupID:          topicGroupId,
			RegionID:         topicRegionId,
			DeviceID:         topicDeviceId,
			Message:          "Download in progress",
			Status:           "IN PROGRESS",
			Progress:         progress.Progress,
			TotalBytes:       progress.TotalBytes,
			DownloadBytes:    progress.DownloadedBytes,
			SpeedKbps:        progress.SpeedKbps,
			ChecksumVerified: false,
			DownloadTime:     0, // 다운로드 진행 시간도 보내주면 좋을 거 같은데 ~
		}
		repository.InsertChan <- event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] PROGRESS 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

func (m *MQTTClient) SubscribeDownloadResult() {
	const topic = "v1/+/+/+/firmware/download/result"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] RESULT 수신 - 토픽: %s", msg.Topic())

		var result types.FirmwareDownloadResult
		if err := json.Unmarshal(msg.Payload(), &result); err != nil {
			log.Printf("[ERROR] RESULT 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[RESULT] Command ID: %s", result.CommandID)
		log.Printf("[RESULT] Status: %s", result.Status)
		log.Printf("[RESULT] Message: %s", result.Message)
		log.Printf("[RESULT] Checksum Verified: %v", result.ChecksumVerified)
		log.Printf("[RESULT] Download Time: %ds", result.DownloadTime)
		log.Printf("[RESULT] Timestamp: %s", result.Timestamp)

		// TODO: DB 저장, 상태 알림, 실패 시 경고 처리 등
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] RESULT 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

func (m *MQTTClient) SubscribeDownloadCancelAck() {
	const topic = "v1/+/+/+/firmware/download/cancel/ack"
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] CANCEL ACK 수신 - 토픽: %s", msg.Topic())

		var ack types.FirmwareDownloadCancelAck
		if err := json.Unmarshal(msg.Payload(), &ack); err != nil {
			log.Printf("[ERROR] CANCEL ACK 메시지 파싱 실패: %v", err)
			return
		}

		log.Printf("[CANCEL ACK] Command ID: %s", ack.CommandID)
		log.Printf("[CANCEL ACK] Status: %s", ack.Status)
		log.Printf("[CANCEL ACK] Message: %s", ack.Message)
		log.Printf("[CANCEL ACK] Timestamp: %s", ack.Timestamp)

		// TODO: DB 저장 또는 상태 업데이트 가능
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] CANCEL ACK 구독 실패: %v", token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}
