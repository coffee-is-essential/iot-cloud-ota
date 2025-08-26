// 서버에 기본적인 설정을 하기 위한 패키지 입니다.
// 환경 변수를 읽어 서버 설정값을 로드합니다.
package config

import (
	"os"
)

// 환경 변수로부터 읽어들일 설정값의 구조를 정의합니다.
type Config struct {
	Server struct {
		Port string
	}
	MqttBroker struct {
		Url      string
		ClientId string
	}

	QuestDB struct {
		Conf string
	}
}

// NewConfig는 환경 변수로부터 설정값을 읽어 Config 객체를 생성합니다.
func NewConfig() *Config {
	conf := new(Config)

	conf.Server.Port = getEnv("SERVER_PORT", ":8080")
	conf.MqttBroker.Url = getEnv("MQTT_BROKER_URL", "tcp://localhost:1883")
	conf.MqttBroker.ClientId = getEnv("MQTT_CLIENT_ID", "local-mqtt-handler")
	conf.QuestDB.Conf = getEnv("QUESTDB_CONF", "http::addr=localhost:9000")

	return conf
}

// getEnv는 환경 변수에서 값을 읽어오고, 해당 키가 없을 경우 fallback 값을 반환합니다.
func getEnv(key, fallback string) string {
	if value, exists := os.LookupEnv(key); exists {
		return value
	}
	return fallback
}
