FROM emqx/emqx:latest

USER root

COPY entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

RUN apt-get update && apt-get install -y jq curl && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /opt/emqx/etc/certs && chown -R emqx:emqx /opt/emqx/etc/certs

USER emqx

ENTRYPOINT ["/entrypoint.sh"]
