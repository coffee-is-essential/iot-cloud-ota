# IoT Cloud OTA를 위한 사설 CA (Private Certificate Authority)

이 프로젝트는 IoT 장치의 안전한 OTA(Over-the-Air) 업데이트 및 클라우드 통신을 위해 사설 인증 기관(CA)을 운영하는 간단한 웹 서비스입니다. [FastAPI](https://fastapi.tiangolo.com/)를 기반으로 구현되었으며, 클라이언트로부터 인증서 서명 요청(CSR)을 받아 서명된 인증서를 발급하는 기능을 제공합니다.

애플리케이션이 시작될 때 CA 개인 키(`ca_key.pem`)와 루트 인증서(`ca_cert.pem`)가 없으면 자동으로 생성합니다.

## 주요 기능

- **사설 루트 CA 자동 생성**: 서버 시작 시 필요한 CA 인증서와 개인 키를 자동으로 생성합니다.
- **CSR 서명**: 클라이언트가 제출한 CSR을 기반으로 장치 인증서를 발급합니다.
- **SAN(Subject Alternative Name) 지원**: CSR에 SAN 확장이 포함된 경우, 발급되는 인증서에도 해당 정보를 포함합니다.
- **Docker 지원**: Docker를 통해 간편하게 서비스를 배포하고 실행할 수 있습니다.

## 프로젝트 구조

```
.
├── app/
│   ├── main.py       # FastAPI 애플리케이션, API 엔드포인트 정의
│   ├── ca.py         # 루트 CA 생성 로직
│   └── config.py     # 기본 설정 값 (기관 이름, 파일 경로 등)
├── Dockerfile        # Docker 이미지 빌드를 위한 설정 파일
├── requirements.txt  # Python 의존성 목록
└── README.md         # 프로젝트 설명 파일
```

## 설치 및 실행

### 1. 로컬 환경에서 직접 실행

**요구사항**: Python 3.10 이상

```bash
# 1. 프로젝트 클론 및 디렉토리 이동
git clone <repository_url>
cd private_ca

# 2. 가상 환경 생성 및 활성화
python -m venv venv
source venv/bin/activate  # macOS/Linux
# venv\Scripts\activate  # Windows

# 3. 의존성 설치
pip install -r requirements.txt

# 4. FastAPI 서버 실행
# 포트는 필요에 따라 변경할 수 있습니다.
cd app/
uvicorn main:app --host 0.0.0.0 --port 8000
```

서버가 시작되면, 프로젝트 루트 디렉토리에 `ca_key.pem`과 `ca_cert.pem` 파일이 자동으로 생성됩니다.

### 2. Docker를 이용한 실행

**요구사항**: Docker 설치

```bash
# 1. Docker 이미지 빌드
docker build -t private-ca .

# 2. Docker 컨테이너 실행
# -p 8000:80: 호스트의 8000번 포트를 컨테이너의 80번 포트와 매핑합니다.
# -v $(pwd)/certs:/app: 호스트의 certs 디렉토리를 컨테이너의 /app 디렉토리와 마운트하여
#                      생성된 CA 인증서와 키를 영속적으로 보관합니다.
mkdir -p certs  # 인증서를 저장할 디렉토리 생성
docker run --rm -d -p 8000:80 -v $(pwd)/certs:/app --name private-ca-container private-ca
```

컨테이너가 실행되면, 호스트의 `certs` 디렉토리에 `ca_key.pem`과 `ca_cert.pem` 파일이 생성된 것을 확인할 수 있습니다.

## API 사용법

### `POST /api/issue_cert`

클라이언트로부터 받은 CSR(인증서 서명 요청)을 서명하여 새로운 인증서를 발급하고, 루트 CA 인증서와 함께 반환합니다.

- **Request Body**:

  `application/json` 형식으로 CSR을 포함해야 합니다.

  ```json
  {
    "csr": "-----BEGIN CERTIFICATE REQUEST-----\nMIIC...\n-----END CERTIFICATE REQUEST-----\n"
  }
  ```

- **Success Response (200 OK)**:

  발급된 장치 인증서와 CA의 루트 인증서를 반환합니다.

  ```json
  {
    "certificate": "-----BEGIN CERTIFICATE-----\nMIID...\n-----END CERTIFICATE-----\n",
    "ca_certificate": "-----BEGIN CERTIFICATE-----\nMIID...\n-----END CERTIFICATE-----\n"
  }
  ```

- **Example (using `curl`)**:

  ```bash
  # CSR이 포함된 JSON 파일(request.json) 생성
  echo '{
    "csr": "'""$(cat device.csr | sed 's/$/\n/' | tr -d '\n')"'""
  }' > request.json

  # API 호출
  curl -X POST http://localhost:8000/api/issue_cert \
  -H "Content-Type: application/json" \
  -d @request.json
  ```

```

```

