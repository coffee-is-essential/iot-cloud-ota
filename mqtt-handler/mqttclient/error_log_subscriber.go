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

// 에러 로그 메시지 구독 및 처리
func (m *MQTTClient) subscribeErrorLog(topic string, parseFunc func(mqtt.Message) (*types.ErrorLogEvent, error), label string) {
	handler := func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("[MQTT] %s 수신 - 토픽: %s", label, msg.Topic())

		event, err := parseFunc(msg)
		if err != nil {
			log.Printf("[ERROR] %s 메시지 파싱 실패: %v", label, err)
			return
		}

		repository.ErrorLogInsertChan <- *event
	}

	token := m.mqttClient.Subscribe(topic, 1, handler)
	token.Wait()
	if token.Error() != nil {
		log.Printf("[ERROR] %s 구독 실패: %v", label, token.Error())
	} else {
		log.Printf("[MQTT] 구독 성공: %s", topic)
	}
}

// 에러 로그 메시지 파싱
func parseErrorLog(msg mqtt.Message) (*types.ErrorLogEvent, error) {
	var errorLog types.ErrorLog
	if err := json.Unmarshal(msg.Payload(), &errorLog); err != nil {

		return nil, err
	}

	return buildErrorLogEvent(msg.Topic(), errorLog), nil
}

// 이벤트 빌더 - 토픽에서 ID 추출 + 이벤트 생성
func buildErrorLogEvent(topic string, status types.ErrorLog) *types.ErrorLogEvent {
	topicParts := strings.Split(topic, "/")
	deviceId, _ := strconv.ParseInt(topicParts[1], 10, 64)

	return &types.ErrorLogEvent{
		DeviceId: deviceId,
		ErrorLog: status,
	}
}
