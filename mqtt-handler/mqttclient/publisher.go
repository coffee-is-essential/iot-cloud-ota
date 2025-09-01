package mqttclient

import (
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"mqtt-handler/repository"
	"mqtt-handler/types"
	"sync"
)

// MQTT 클라이언트를 사용해 지정된 토픽으로 JSON 형태의 펌웨어 배포 요청 메시지를 전송합니다.
func (m *MQTTClient) PublishDownloadRequest(req *types.FirmwareDeployRequest) {
	var buf bytes.Buffer

	var wg sync.WaitGroup
	command := types.FirmwareDownloadCommand{
		CommandID: req.FileInfo.DeploymentId,
		SignedURL: req.SignedUrl,
		Version:   req.FileInfo.Version,
		Checksum:  req.FileInfo.FileHash,
		Size:      req.FileInfo.FileSize,
		Timeout:   req.Timeout,
		Timestamp: req.FileInfo.DeployedAt,
	}

	enc := json.NewEncoder(&buf)
	enc.SetEscapeHTML(false)
	if err := enc.Encode(command); err != nil {
		log.Printf("[ERROR] JSON 직렬화 실패: %v", err)
		return
	}
	payload := buf.String()

	for _, deviceInfo := range req.Devices {
		wg.Add(1)

		deviceInfoCopy := deviceInfo
		go func(d types.DeviceIds) {
			defer wg.Done()
			topic := fmt.Sprintf("v1/%d/firmware/download/request", d.DeviceId)
			token := m.mqttClient.Publish(topic, 2, false, payload)

			event := types.FirmwareDownloadEvent{
				CommandID:        command.CommandID,
				DeviceID:         d.DeviceId,
				Message:          "Download Command",
				Status:           "WAITING",
				Progress:         0,
				TotalBytes:       command.Size,
				DownloadBytes:    0,
				SpeedKbps:        0,
				ChecksumVerified: false,
				DownloadTime:     0,
			}

			repository.DownloadInsertChan <- event
			token.Wait()
			if token.Error() != nil {
				log.Printf("[MQTT] Publish 실패: %s → %v", topic, token.Error())
			} else {
				log.Printf("[MQTT] Publish 성공: %s", topic)
			}
		}(deviceInfoCopy)
	}

	wg.Wait()
}

// MQTT 클라이언트를 사용해 지정된 토픽으로 JSON 형태의 펌웨어 배포 취소 요청 메시지를 전송합니다.
func (m *MQTTClient) PublishDownloadCancelRequest(req *types.FirmwareDeployCancelRequest) {
	var wg sync.WaitGroup

	command := types.FirmwareDownloadCancelCommand{
		CommandID: req.CommandID,
		Reason:    req.Reason,
	}

	payload, err := json.Marshal(command)
	if err != nil {
		log.Printf("[ERROR] JSON 직렬화 실패: %v", err)
		return
	}

	for _, deviceInfo := range req.Devices {
		wg.Add(1)

		deviceInfoCopy := deviceInfo
		go func(d types.DeviceIds) {
			defer wg.Done()
			topic := fmt.Sprintf("v1/%d/firmware/download/cancel", d.DeviceId)
			token := m.mqttClient.Publish(topic, 2, false, payload)

			event := types.FirmwareDownloadEvent{
				CommandID:        command.CommandID,
				DeviceID:         d.DeviceId,
				Message:          command.Reason,
				Status:           "CANCELED",
				Progress:         0,
				TotalBytes:       0,
				DownloadBytes:    0,
				SpeedKbps:        0,
				ChecksumVerified: false,
				DownloadTime:     0,
			}

			repository.DownloadInsertChan <- event
			token.Wait()
			if token.Error() != nil {
				log.Printf("[MQTT] Publish 실패: %s → %v", topic, token.Error())
			} else {
				log.Printf("[MQTT] Publish 성공: %s", topic)
			}
		}(deviceInfoCopy)
	}

	wg.Wait()
}
