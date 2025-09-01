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

// 시스템 상태 토픽에 대해 메시지를 처리하는 핸들러를 등록
func (m *MQTTClient) subscribeSystemStatus(topic string, parseFunc func(mqtt.Message) (*types.SystemStatusEvent, error), label string) {
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] %s 수신 - 토픽: %s", label, msg.Topic())

		event, err := parseFunc(msg)
		if err != nil {
			log.Printf("[ERROR] %s 메시지 파싱 실패: %v", label, err)
			return
		}

		repository.SystemStatusInsertChan <- *event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] %s 구독 실패: %v", label, token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

// 시스템 상태 메시지 처리
func parseSystemStatus(msg mqtt.Message) (*types.SystemStatusEvent, error) {
	var status types.SystemStatus
	if err := json.Unmarshal(msg.Payload(), &status); err != nil {

		return nil, err
	}

	return buildSystemStatusEvent(msg.Topic(), status.CpuUsage, status.MemoryUsage, status.DiskUsage, status.Temperature, status.Uptime, status.Timestamp), nil
}

// 이벤트 빌더 - 토픽에서 ID 추출 + 이벤트 생성
func buildSystemStatusEvent(topic string, cpuUsage float64, memoryUsage float64, diskUsage float64, temperature float64, upTime int64, timestamp time.Time) *types.SystemStatusEvent {
	topicParts := strings.Split(topic, "/")
	deviceId, _ := strconv.ParseInt(topicParts[1], 10, 64)

	return &types.SystemStatusEvent{
		DeviceId:    deviceId,
		CpuUsage:    cpuUsage,
		MemoryUsage: memoryUsage,
		DiskUsage:   diskUsage,
		Temperature: temperature,
		Uptime:      upTime,
		Timestamp:   timestamp,
	}
}
