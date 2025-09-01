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

// Health 네트워크 상태 토픽에 대해 메시지를 처리하는 핸들러를 등록
func (m *MQTTClient) subscribeNetworkStatus(topic string, parseFunc func(message mqtt.Message) (*types.NetworkStatusEvent, error), label string) {
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] %s 수신 - 토픽: %s", label, msg.Topic())

		event, err := parseFunc(msg)
		if err != nil {
			log.Printf("[ERROR] %s 메시지 파싱 실패: %v", label, err)
			return
		}

		repository.NetworkStatusInsertChan <- *event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] %s 구독 실패: %v", label, token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

// 네트워크 상태 메시지 처리
func parseNetworkStatus(msg mqtt.Message) (*types.NetworkStatusEvent, error) {
	var status types.NetworkStatus
	if err := json.Unmarshal(msg.Payload(), &status); err != nil {

		return nil, err
	}

	return buildNetworkStatusEvent(msg.Topic(), status.ConnectionType, status.SignalStrength, status.IpAddress, status.Gateway, status.Timestamp), nil
}

// 이벤트 빌더 - 토픽에서 ID 추출 + 이벤트 생성
func buildNetworkStatusEvent(topic string, connectionType string, signalStrength int64, ipAddress string, gateway string, time time.Time) *types.NetworkStatusEvent {
	topicParts := strings.Split(topic, "/")
	deviceId, _ := strconv.ParseInt(topicParts[1], 10, 64)

	return &types.NetworkStatusEvent{
		DeviceId:       deviceId,
		ConnectionType: connectionType,
		SignalStrength: signalStrength,
		IpAddress:      ipAddress,
		Gateway:        gateway,
		Timestamp:      time,
	}
}
