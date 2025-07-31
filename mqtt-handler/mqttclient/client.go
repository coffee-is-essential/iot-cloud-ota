package mqttclient

import (
	"fmt"
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
)

var messagePubHandler mqtt.MessageHandler = func(client mqtt.Client, message mqtt.Message) {
	fmt.Printf("Received message: %s from topic: %s\n", message.Payload(), message.Topic())
}

var connectHandler mqtt.OnConnectHandler = func(client mqtt.Client) {
	fmt.Println("Connected")
}

var connectLostHandler mqtt.ConnectionLostHandler = func(client mqtt.Client, err error) {
	fmt.Printf("Connect loss: %v", err)
}

type MQTTClient struct {
	mqttClient mqtt.Client
}

func NewMqtt() *MQTTClient {
	return &MQTTClient{}
}

// NewMqtt는 브로커에 연결된 Mqtt 인스턴스를 생성합니다.
func (m *MQTTClient) Connect(brokerURL string, clientId string) {
	opts := mqtt.NewClientOptions()
	opts.AddBroker(brokerURL)
	opts.SetClientID(clientId)
	opts.SetDefaultPublishHandler(messagePubHandler)
	opts.OnConnect = connectHandler
	opts.OnConnectionLost = connectLostHandler
	
	client := mqtt.NewClient(opts)
	if token := client.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("MQTT 브로커 연결 실패: %v", token.Error())
	}
	fmt.Printf("%s 가 브로커 [%s]에 연결됨\n", opts.ClientID, opts.Servers[0].String())

	m.mqttClient = client
	log.Println("MQTT 연결 성공")
}
