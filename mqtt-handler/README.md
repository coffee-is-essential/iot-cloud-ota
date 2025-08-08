# mqtt-handler

## 🥵 실행 방법
### 1. `init` 디렉토리로 이동
```azure
cd init
```
### 
### 2. `config.toml` 파일 생성
- 아래와 같이 config.toml 파일을 만들어주세요.
```azure
# 서버 설정 (예: ":8000")
[server]
port = ""

# MQTT 브로커 설정 (예: "tcp://broker.example.com:1883")
[mqttBroker]
url = ""

# MQTT 클라이언트 ID. (예: "go-mqtt-handler")
clientId = ""

# QuestDB 연결 설정 (예: "http::addr=localhost:9000;") 
[questDB]
conf = ""

```
### 3. 빌드 또는 실행
- 빌드 후 실행
```azure
go build main.go
./main
```
- 바로 실행
```azure
go run main.go
```