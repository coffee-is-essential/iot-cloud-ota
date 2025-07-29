// 서버에 기본적인 설정을 하기 위한 패키지 입니다.
// TOML 포맷의 설정 파일을 읽어 서버 설정값을 로드합니다.
package config

import (
	"github.com/naoina/toml"
	"os"
)

// TOML 파일로부터 읽어들일 설정값의 구조를 정의합니다.
type Config struct {
	Server struct {
		Port string
	}
}

// 주어진 filePath의 TOML 설정 파일을 읽어 Config 객체로 파싱합니다.
func NewConfig(filePath string) *Config {
	con := new(Config)

	if file, err := os.Open(filePath); err != nil {
		panic(err)
	} else if err = toml.NewDecoder(file).Decode(con); err != nil {
		panic(err)
	} else {
		return con
	}
}
