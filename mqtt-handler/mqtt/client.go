package mqtt

import (
	mqtt "github.com/eclipse/paho.mqtt.golang"
	"log"
)

/*
MQTT 브로커에 연결된 클라이언트를 생성하여 반환합니다.

Parameters:
- brokerURL: MQTT 브로커의 주소
- clientId: 클라이언트를 식별할 고유 ID

Returns:
- mqtt.Client: 연결에 성공한 MQTT 클라이언트 객체
*/
func NewClient(brokerURL string, clientId string) mqtt.Client {
	opts := mqtt.NewClientOptions().
		AddBroker(brokerURL).
		SetClientID(clientId)

	// 기본 핸들러 설정: 명시되지 않은 토픽의 메시지를 수신했을 때 동작함
	opts.SetDefaultPublishHandler(func(client mqtt.Client, msg mqtt.Message) {
		log.Printf("Unhandled message [%s]: %s", msg.Topic(), msg.Payload())
	})

	client := mqtt.NewClient(opts)
	if token := client.Connect(); token.Wait() && token.Error() != nil {
		log.Fatalf("MQTT 연결 실패: %v", token.Error())
	}

	log.Println("MQTT 연결 성공")
	return client
}
