package mqttclient

import (
	"encoding/json"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/repository"
	"mqtt-handler/types"
	"strconv"
	"strings"
	"time"
)

// Health 상태 토픽에 대해 메시지를 처리하는 핸들러를 등록
func (m *MQTTClient) subscribeHealthStatus(topic string, parseFunc func(mqtt.Message) (*types.HealthStatusEvent, error), label string) {
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] %s 수신 - 토픽: %s", label, msg.Topic())

		event, err := parseFunc(msg)
		if err != nil {
			log.Printf("[ERROR] %s 메시지 파싱 실패: %v", label, err)
			return
		}

		repository.HealthStatusInsertChan <- *event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] %s 구독 실패: %v", label, token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

// Health 상태 메시지 처리
func parseHealthStatus(msg mqtt.Message) (*types.HealthStatusEvent, error) {
	var status types.HealthStatus
	if err := json.Unmarshal(msg.Payload(), &status); err != nil {

		return nil, err
	}

	return buildHealthStatusEvent(msg.Topic(), status.OverallStatus, status.FirmwareVersion, status.LastReboot, status.ErrorCount, status.WarningCount, status.Timestamp), nil
}

// 이벤트 빌더 - 토픽에서 ID 추출 + 이벤트 생성
func buildHealthStatusEvent(topic string, overallStatus string, firmwareVersion string, lastReboot time.Time, errorCount int64, warningCount int64, timestamp time.Time) *types.HealthStatusEvent {
	topicParts := strings.Split(topic, "/")
	deviceId, _ := strconv.ParseInt(topicParts[1], 10, 64)

	return &types.HealthStatusEvent{
		DeviceId:        deviceId,
		OverallStatus:   overallStatus,
		FirmwareVersion: firmwareVersion,
		LastReboot:      lastReboot,
		ErrorCount:      errorCount,
		WarningCount:    warningCount,
		Timestamp:       timestamp,
	}
}
