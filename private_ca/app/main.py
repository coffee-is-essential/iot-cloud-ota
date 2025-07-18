import contextlib
import datetime

from cryptography import x509
from cryptography.hazmat.primitives import hashes, serialization
from cryptography.hazmat.primitives.asymmetric.rsa import RSAPrivateKey
from fastapi import FastAPI, Request

import ca
import config


@contextlib.asynccontextmanager
async def lifespan(app: FastAPI):
    """FastAPI 애플리케이션의 생명주기 관리 함수입니다.
    
    이 함수는 애플리케이션 시작 시 루트 CA를 생성합니다.
    만약 애플리케이션이 종료될 때 추가 작업이 필요하다면,
    이곳에 코드를 추가해주세요.

    Args:
        app: FastAPI 애플리케이션 인스턴스

    Yields:
        None: 이 함수는 컨텍스트 매니저로 사용되며, 애플리케이션의 시작과 종료 시점에 실행됩니다.
    """
    ca.create_root_ca()
    yield
    # NOTE: 종료 시 작업이 필요하다면 여기에 작성해주세요. 현재는 특별한 작업은 필요하지 않습니다.


app = FastAPI(lifespan=lifespan)


@app.post("/api/issue_cert")
async def issue_cert(request: Request):
    """클라이언트로부터 CSR을 받아 CA 키로 서명된 인증서와 루트 CA 인증서를 발급합니다.

    이 엔드포인트는 클라이언트가 제공한 인증 요청(CSR)을 처리하여,
    CA 키로 서명된 인증서와 루트 CA 인증서를 반환합니다.
    클라이언트는 JSON 형식으로 CSR을 포함하여 요청해야 합니다.
    SAN(Subject Alternative Name) 확장이 포함된 CSR을 지원합니다. 없는 경우 무시됩니다.

    Args:
        request (Request): 클라이언트의 요청 객체로, JSON 형식의 CSR을 포함해야 합니다.

    Returns:
        dict: 발급된 인증서와 루트 CA 인증서를 포함하는 JSON 응답.
            - "certificate": CA 키로 서명된 인증서의 PEM 형식 문자열
            - "ca_certificate": 루트 CA 인증서의 PEM 형식 문자열
    """
    data = await request.json()
    csr_pem = data["csr"].encode()
    csr = x509.load_pem_x509_csr(csr_pem)

    with open(config.CA_KEY_FILE, "rb") as f:
        ca_key = serialization.load_pem_private_key(f.read(), None)
        # Ensure the key is an RSA private key
        assert isinstance(ca_key, RSAPrivateKey)

    with open(config.CA_CERT_FILE, "rb") as f:
        ca_cert_bytes = f.read()
        ca_cert = x509.load_pem_x509_certificate(ca_cert_bytes)
        ca_cert_pem = ca_cert_bytes.decode()

    builder = x509.CertificateBuilder().subject_name(csr.subject)\
        .issuer_name(ca_cert.subject)\
        .public_key(csr.public_key())\
        .serial_number(x509.random_serial_number())\
        .not_valid_before(datetime.datetime.now(tz=datetime.timezone.utc))\
        .not_valid_after(datetime.datetime.now(tz=datetime.timezone.utc) + datetime.timedelta(days=365))\

    try:
        san = csr.extensions.get_extension_for_class(
            x509.SubjectAlternativeName).value
        builder = builder.add_extension(san, critical=False)
    except x509.ExtensionNotFound:
        pass  # SAN이 없는 경우 무시합니다.

    cert = builder.sign(ca_key, hashes.SHA256())
    cert_pem = cert.public_bytes(serialization.Encoding.PEM).decode()

    return {
        "certificate": cert_pem,
        "ca_certificate": ca_cert_pem,
    }
