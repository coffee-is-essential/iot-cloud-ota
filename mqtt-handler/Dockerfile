# Build Stage
FROM golang:1.24-alpine AS builder

WORKDIR /app

COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o /app/mqtt-handler ./init

# Final Stage
FROM alpine:latest

WORKDIR /root

COPY --from=builder /app/mqtt-handler .

EXPOSE 8080

CMD ["./mqtt-handler"]
