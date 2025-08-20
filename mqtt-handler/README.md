# mqtt-handler

## 🥵 실행 방법

### 1. `init` 디렉토리로 이동

```azure
cd init
```

### 2. 환경 변수 설정

- 다음과 같은 환경 변수가 설정되어야 합니다.

```azure
export SERVER_PORT=":8080"
export MQTT_BROKER_URL="tcp://localhost:1883"
export MQTT_CLIENT_ID="mqtt-handler"
export QUESTDB_CONF="http::addr=localhost:9000"
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
