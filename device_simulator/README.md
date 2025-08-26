# IoT 디바이스 펌웨어 OTA (Over-the-Air) 업데이트 시뮬레이터

이 프로젝트는 MQTT를 통해 펌웨어 업데이트 명령을 수신하고 지정된 URL에서 펌웨어를 다운로드할 수 있는 IoT 디바이스를 시뮬레이션합니다. 다운로드 진행 상황을 보고하고, 체크섬을 사용하여 파일 무결성을 검증하며, 최종 결과를 서버로 다시 전송합니다.

## 주요 기능

- **MQTT 통신**: MQTT 브로커에 연결하여 명령을 수신하고 상태 업데이트를 전송합니다.
- **보안 펌웨어 다운로드**: 사전 서명된 URL에서 HTTPS를 사용하여 펌웨어를 다운로드합니다.
- **비동기 다운로드**: 메인 프로세스를 차단하지 않도록 별도의 스레드에서 펌웨어 다운로드를 처리합니다.
- **진행 상황 보고**: 다운로드 진행 상황(퍼센티지, 속도, 예상 완료 시간)을 MQTT 토픽에 게시합니다.
- **체크섬 검증**: SHA256 체크섬을 사용하여 다운로드된 파일의 무결성을 검증합니다.
- **상태 알림**: 다운로드의 최종 상태(성공, 실패, 시간 초과 등)를 서버에 다시 보고합니다.
- **설정 가능**: 디바이스 ID와 MQTT 브로커 세부 정보를 환경 변수를 통해 설정할 수 있습니다.

## 사전 요구 사항

- Python 3.7+
- pip

## 설치

1.  저장소를 복제하거나 소스 코드를 다운로드합니다.
2.  `requirements.txt`를 사용하여 필요한 Python 패키지를 설치합니다:

    ```bash
    pip install -r requirements.txt
    ```

## 설정

시뮬레이터는 환경 변수를 사용하여 설정됩니다.

| 환경 변수             | 설명                                            | 기본값               |
| --------------------- | ----------------------------------------------- | -------------------- |
| `DEVICE_ID`           | 시뮬레이션된 디바이스의 고유 식별자입니다.      | `1`                  |
| `BROKER_URL`          | MQTT 브로커의 URL입니다.                        | `test.mosquitto.org` |
| `BROKER_PORT`         | MQTT 브로커의 포트입니다.                       | `1883`               |
| `DOWNLOAD_CHUNK_SIZE` | 펌웨어 다운로드를 위한 청크 크기(바이트)입니다. | `102400` (100KB)     |

**예시:**

```bash
export DEVICE_ID="device-001"
export BROKER_URL="your-mqtt-broker.com"
export BROKER_PORT=1883
```

## 사용법

`main.py` 스크립트를 실행하여 디바이스 시뮬레이터를 시작합니다:

```bash
python main.py
```

시뮬레이터는 MQTT 브로커에 연결하고 펌웨어 다운로드 요청 토픽을 구독합니다. 메시지를 기다리며 도착하는 대로 처리합니다.

시뮬레이터를 중지하려면 `Ctrl+C`를 누르세요.

## MQTT 통신 프로토콜

디바이스는 다음 MQTT 토픽과 메시지 형식을 사용하여 서버와 통신합니다. 토픽의 `{device_id}`는 실제 디바이스 ID로 교체되어야 합니다.

### 1. 펌웨어 다운로드 요청 (서버 -> 디바이스)

서버가 이 토픽으로 메시지를 보내 디바이스에 새 펌웨어 버전을 다운로드하도록 명령합니다.

- **토픽**: `v1/{device_id}/firmware/download/request`
- **페이로드 (JSON)**:

  ```json
  {
    "command_id": "cmd-12345",
    "signed_url": "https://your-s3-bucket.s3.amazonaws.com/firmware.bin?AWSAccessKeyId=...",
    "version": "1.1.0",
    "checksum": "sha256:f2ca1bb6c7e907d06dafe4687e579fce76b37e4e93b7605022da52e6ccc26fd2",
    "size": 1048576,
    "timeout": 600
  }
  ```

### 2. 요청 승인 (디바이스 -> 서버)

다운로드 요청을 받은 후 디바이스가 승인을 보냅니다.

- **토픽**: `v1/{device_id}/firmware/download/request/ack`
- **페이로드 (JSON)**:

  ```json
  {
    "command_id": "cmd-12345",
    "status": "ACKNOWLEDGED",
    "message": "펌웨어 다운로드 요청을 수신했습니다",
    "timestamp": "2023-10-27T10:00:00Z"
  }
  ```

### 3. 다운로드 진행 상황 (디바이스 -> 서버)

다운로드 중에 디바이스가 주기적으로 진행 상황 업데이트를 보냅니다.

- **토픽**: `v1/{device_id}/firmware/download/progress`
- **페이로드 (JSON)**:

  ```json
  {
    "command_id": "cmd-12345",
    "progress": 50,
    "downloaded_bytes": 524288,
    "total_bytes": 1048576,
    "speed_kbps": 512.5,
    "eta_seconds": 1,
    "timestamp": "2023-10-27T10:00:05Z"
  }
  ```

### 4. 다운로드 결과 (디바이스 -> 서버)

다운로드가 완료된 후 (또는 실패한 후) 디바이스가 최종 결과를 보냅니다.

- **토픽**: `v1/{device_id}/firmware/download/result`
- **페이로드 (JSON)**:

  ```json
  {
    "command_id": "cmd-12345",
    "status": "SUCCESS", // SUCCESS, FAILED, CANCELLED, TIMEOUT
    "message": "다운로드가 성공적으로 완료되었습니다",
    "checksum_verified": true,
    "download_time": 10.5,
    "timestamp": "2023-10-27T10:00:10Z"
  }
  ```
