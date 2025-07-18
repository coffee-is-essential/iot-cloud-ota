#!/bin/bash
set -e

EMQX_IP=$(hostname -i | awk '{print $1}')
PRIVATE_CA_HOST="http://private-ca.internal:80/api/issue_cert"

# 1. 인증서 관련 파일 경로 설정
EMQX_CERT_DIR="/opt/emqx/etc/certs"
EMQX_PRIVATE_KEY="${EMQX_CERT_DIR}/emqx.key"
EMQX_CSR="${EMQX_CERT_DIR}/emqx.csr"
EMQX_SAN="${EMQX_CERT_DIR}/emqx-san.cnf"

EMQX_CONF="/opt/emqx/etc/emqx.conf"

EMQX_CERT="${EMQX_CERT_DIR}/emqx.crt"
CA_CERT="${EMQX_CERT_DIR}/ca.crt"

# 2. CSR이 존재하지 않으면 생성
if [ ! -f "${EMQX_PRIVATE_KEY}" ]; then
  echo "Generating private key..."
  openssl genpkey -algorithm RSA -out ${EMQX_PRIVATE_KEY} -pkeyopt rsa_keygen_bits:2048
  echo "Private key generated at ${EMQX_PRIVATE_KEY}"
fi

if [ ! -f "${EMQX_CSR}" ]; then
  echo "Generating CSR..."

  # SAN 파일 생성, DNS 이름은 NLB_HOST로 설정
  cat <<EOF > ${EMQX_SAN}
[ req ]
default_bits       = 2048
prompt             = no
default_md         = sha256
distinguished_name = dn
req_extensions     = req_ext

[ dn ]
CN = emqx

[ req_ext ]
subjectAltName = @alt_names

[ alt_names ]
IP.1 = ${EMQX_IP}
DNS.1 = ${NLB_HOST}
EOF

  openssl req -new -key ${EMQX_PRIVATE_KEY} -out ${EMQX_CSR} -config ${EMQX_SAN}
  echo "CSR is successfully generated."
fi

# 3. 사설CA로부터 인증서 요청
if [ ! -f "${EMQX_CERT}" ]; then
  echo "Requesting certificate from Private CA..."
  CSR_PEM="$(cat "${EMQX_CSR}")"

  RESPONSE=$(curl -sSL -X POST \
    -H "Content-Type: application/json" \
    -d "{\"csr\": \"${CSR_PEM//$'\n'/\\n}\"}" \
    "${PRIVATE_CA_HOST}")

  if ! echo "$RESPONSE" | grep -q 'certificate'; then
    echo "Failed to obtain certificate from Private CA: $RESPONSE"
    exit 1
  fi

  echo "$RESPONSE" | jq -r '.certificate' > "${EMQX_CERT}"
  echo "$RESPONSE" | jq -r '.ca_certificate' > "${CA_CERT}"

  if [ ! -s "${EMQX_CERT}" ]; then
    echo "Failed to write certificate to ${EMQX_CERT}"
    exit 1
  fi

  if [ ! -s "${CA_CERT}" ]; then
    echo "Failed to write CA certificate to ${CA_CERT}"
    exit 1
  fi

  echo "Certificate successfully obtained and saved."
fi

# 4. EMQX 설정 파일에 TLS 설정 추가
cat >> "${EMQX_CONF}" <<EOF
listeners.ssl.default {
  bind = "0.0.0.0:8883"
  ssl_options {
    cacertfile = "${CA_CERT}"
    certfile = "${EMQX_CERT}"
    keyfile = "${EMQX_PRIVATE_KEY}"
    verify = verify_peer
    fail_if_no_peer_cert = true
  }
}
EOF

echo "EMQX configuration updated with SSL settings."
# 5. EMQX 시작
exec emqx foreground
