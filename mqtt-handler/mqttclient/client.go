package mqttclient

import (
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
	"mqtt-handler/repository"
	"sync"
	"time"
)

// mqttClientInit: 초기화를 한 번만 수행하기 위한 sync.Once
// mqttClientInstance: 전역 MQTTClient 인스턴스
var (
	mqttClientInit     sync.Once
	mqttClientInstance *MQTTClient
)

// MQTTClient는 MQTT 클라이언트와 DB 클라이언트를 포함하는 구조체입니다.
type MQTTClient struct {
	mqttClient mqtt.Client
	dbClient   *repository.DBClient
}

// 메시지 수신 시 호출되는 기본 핸들러
var messagePubHandler mqtt.MessageHandler = func(client mqtt.Client, message mqtt.Message) {
	log.Printf("Received message: %s from topic: %s\n", message.Payload(), message.Topic())
}

// MQTT 연결이 끊겼을 때 호출되는 핸들러
var connectLostHandler mqtt.ConnectionLostHandler = func(client mqtt.Client, err error) {
	log.Printf("Connect loss: %v", err)
}

// NewMqttClient는 MQTTClient 인스턴스를 한 번만 생성합니다.
func NewMqttClient() *MQTTClient {
	mqttClientInit.Do(func() {
		mqttClientInstance = &MQTTClient{
			dbClient: repository.NewDBClient(),
		}
	})

	return mqttClientInstance
}

// 주어진 brokerURL과 clientId로 MQTT 브로커에 연결합니다.
func (m *MQTTClient) Connect(brokerURL string, clientId string) {
	opts := mqtt.NewClientOptions()
	opts.AddBroker(brokerURL)
	opts.SetClientID(clientId)
	opts.SetDefaultPublishHandler(messagePubHandler)
	opts.SetConnectionLostHandler(connectLostHandler)

	// 안정성 튜닝
	opts.SetKeepAlive(30 * time.Second)
	opts.SetPingTimeout(10 * time.Second)
	opts.SetWriteTimeout(10 * time.Second)
	opts.SetConnectTimeout(10 * time.Second)

	// 끊겨도 자동 복구
	opts.SetAutoReconnect(true)
	opts.SetConnectRetry(true)
	opts.SetConnectRetryInterval(3 * time.Second)
	opts.SetMaxReconnectInterval(30 * time.Second)

	// 세션/구독 유지
	opts.CleanSession = false
	opts.SetResumeSubs(true)
	opts.SetProtocolVersion(4)

	// 재연결/최초 연결 시 구독
	opts.OnConnect = func(c mqtt.Client) {
		log.Println("[MQTT] connected, subscribing...")
		m.SubscribeAllTopics()
	}

	client := mqtt.NewClient(opts)

	if token := client.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("[MQTT] 브로커 연결 실패: %v", token.Error())
	}
	log.Printf("%s 가 브로커 [%s]에 연결됨\n", opts.ClientID, opts.Servers[0].String())
	m.mqttClient = client
	log.Println("[MQTT] 연결 성공")
}

// 전체 구독 시작 - 모든 관련 토픽을 한 번에 등록
func (m *MQTTClient) SubscribeAllTopics() {
	m.subscribe("v1/+/update/request/ack", parseDownloadRequestAck, "[ACK]")
	m.subscribe("v1/+/update/progress", parseDownloadProgress, "[PROGRESS]")
	m.subscribe("v1/+/update/result", parseDownloadResult, "[RESULT]")
	m.subscribe("v1/+/update/cancel/ack", parseDownloadCancelAck, "[CANCEL ACK]")
	m.subscribeSystemStatus("v1/+/status/system", parseSystemStatus, "[SYSTEM STATUS]")
	m.subscribeErrorLog("v1/+/status/error_log", parseErrorLog, "[ERROR LOG]")
}
