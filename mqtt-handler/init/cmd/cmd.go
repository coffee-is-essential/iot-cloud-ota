// 전체 시스템의 주요 컴포넌트를 초기화하고 연결하는 역할을 합니다.
package cmd

import (
	"mqtt-handler/config"
	"mqtt-handler/mqttclient"
	"mqtt-handler/network"
)

// 애플리케이션의 핵심 구성 요소들을 담고 있는 컨테이너입니다.
// config: TOML 설정 파일로부터 로드된 설정 정보를 보관
// network: HTTP 서버와 관련된 기능들을 관리
type Cmd struct {
	config     *config.Config
	network    *network.Network
	mqttClient *mqttclient.MQTTClient
}

// Cmd 구조체를 초기화하고, HTTP 서버를 시작합니다.
func NewCmd(filePath string) *Cmd {
	c := &Cmd{
		config:     config.NewConfig(filePath),
		network:    network.NewNetwork(),
		mqttClient: mqttclient.NewMqttClient(),
	}

	c.mqttClient.Connect(c.config.MqttBroker.Url, c.config.MqttBroker.ClientId)
	c.network.ServerStart(c.config.Server.Port)

	return c
}
