package mqttclient

import (
	"encoding/json"
	"log"
	"mqtt-handler/repository"
	"mqtt-handler/types"
	"strconv"
	"strings"
	"time"

	mqtt "github.com/eclipse/paho.mqtt.golang"
)

// 공통 구독 함수 - 다양한 펌웨어 토픽에 대해 메시지를 처리하는 핸들러를 등록
func (m *MQTTClient) subscribe(topic string, parseFunc func(mqtt.Message) (*types.DownloadEvent, error), label string) {
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] %s 수신 - 토픽: %s", label, msg.Topic())

		event, err := parseFunc(msg)
		if err != nil {
			log.Printf("[ERROR] %s 메시지 파싱 실패: %v", label, err)
			return
		}

		repository.DownloadInsertChan <- *event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] %s 구독 실패: %v", label, token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

// 펌웨어 다운로드 요청 수신 확인(ACK) 메시지 처리
func parseDownloadRequestAck(msg mqtt.Message) (*types.DownloadEvent, error) {
	var ack types.DownloadRequestAck
	if err := json.Unmarshal(msg.Payload(), &ack); err != nil {

		return nil, err
	}

	return buildDownloadEvent(msg.Topic(), ack.CommandID, "[Download Request Received]", ack.Status,
		0, 0, 0, 0, false, 0, ack.Timestamp), nil
}

// 다운로드 진행 중(Progress) 메시지 처리
func parseDownloadProgress(msg mqtt.Message) (*types.DownloadEvent, error) {
	var progress types.DownloadProgress
	if err := json.Unmarshal(msg.Payload(), &progress); err != nil {

		return nil, err
	}

	return buildDownloadEvent(msg.Topic(), progress.CommandID, "[Download in progress]", "IN_PROGRESS",
		progress.Progress, progress.TotalBytes, progress.DownloadedBytes, progress.SpeedKbps, false, 0, progress.Timestamp), nil
}

// 다운로드 결과(Result) 메시지 처리
func parseDownloadResult(msg mqtt.Message) (*types.DownloadEvent, error) {
	var result types.DownloadResult
	if err := json.Unmarshal(msg.Payload(), &result); err != nil {

		return nil, err
	}

	return buildDownloadEvent(msg.Topic(), result.CommandID, result.Message, result.Status,
		0, 0, 0, 0, result.ChecksumVerified, result.DownloadSeconds, result.Timestamp), nil
}

// 다운로드 취소 응답 메시지 처리
func parseDownloadCancelAck(msg mqtt.Message) (*types.DownloadEvent, error) {
	var ack types.DownloadCancelAck
	if err := json.Unmarshal(msg.Payload(), &ack); err != nil {

		return nil, err
	}

	return buildDownloadEvent(msg.Topic(), ack.CommandID, "[Download Cancel Request Received]", ack.Status,
		0, 0, 0, 0, false, 0, ack.Timestamp), nil
}

// 공통 이벤트 빌더 - 토픽에서 ID 추출 + 이벤트 생성
func buildDownloadEvent(topic, commandID, message, status string, progress int64, totalBytes int64, downloadBytes int64, speedKbps float64, verified bool, downloadTime int64, timestamp time.Time) *types.DownloadEvent {
	topicParts := strings.Split(topic, "/")
	deviceId, _ := strconv.ParseInt(topicParts[1], 10, 64)

	return &types.DownloadEvent{
		CommandID:        commandID,
		DeviceID:         deviceId,
		Message:          message,
		Status:           status,
		Progress:         progress,
		TotalBytes:       totalBytes,
		DownloadBytes:    downloadBytes,
		SpeedKbps:        speedKbps,
		ChecksumVerified: verified,
		DownloadSeconds:  downloadTime,
		Timestamp:        timestamp,
	}
}
