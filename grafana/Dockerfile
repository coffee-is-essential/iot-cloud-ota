# Dockerfile
FROM grafana/grafana-oss:latest

# QuestDB 플러그인 설치
ENV GF_INSTALL_PLUGINS=questdb-questdb-datasource

# 프로비저닝 및 대시보드 파일 복사
COPY ./provisioning /etc/grafana/provisioning
COPY ./dashboards /etc/grafana/dashboards
