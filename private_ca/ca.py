import datetime
import os
import logging

from cryptography.hazmat.primitives import serialization, hashes
from cryptography.hazmat.primitives.asymmetric import rsa
from cryptography import x509

import config

logger = logging.getLogger(__name__)


def create_root_ca():
    """루트 CA 인증서를 생성합니다.

    이 함수는 루트 CA 인증서를 생성하고, PEM 형식으로 저장합니다.
    """
    if os.path.exists(config.CA_CERT_FILE) and os.path.exists(
            config.CA_CERT_FILE):
        logger.info("Root CA already exists.")
        return

    key = rsa.generate_private_key(public_exponent=65537, key_size=2048)

    subject = issuer = x509.Name([
        x509.NameAttribute(x509.NameOID.COUNTRY_NAME, u"KR"),
        x509.NameAttribute(x509.NameOID.ORGANIZATION_NAME,
                           config.ORGANIZATION_NAME),
        x509.NameAttribute(x509.NameOID.COMMON_NAME, u"IoT Cloud OTA Root CA"),
    ])

    cert = x509.CertificateBuilder().subject_name(subject).issuer_name(issuer)\
        .public_key(key.public_key())\
        .serial_number(x509.random_serial_number())\
        .not_valid_before(datetime.datetime.now(tz=datetime.timezone.utc))\
        .not_valid_after(datetime.datetime.now(tz=datetime.timezone.utc) + datetime.timedelta(days=3650))\
        .add_extension(x509.BasicConstraints(ca=True, path_length=None), critical=True)\
        .sign(key, hashes.SHA256())

    with open(config.CA_KEY_FILE, 'wb') as f:
        f.write(
            key.private_bytes(
                encoding=serialization.Encoding.PEM,
                format=serialization.PrivateFormat.TraditionalOpenSSL,
                encryption_algorithm=serialization.NoEncryption()))

    with open(config.CA_CERT_FILE, 'wb') as f:
        f.write(cert.public_bytes(serialization.Encoding.PEM))
