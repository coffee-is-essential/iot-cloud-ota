package mqttclient

import (
	"encoding/json"
	"fmt"
	"log"
	"mqtt-handler/repository"
	"mqtt-handler/types"
	"sync"
)

/*
MQTT 클라이언트를 사용해 지정된 토픽으로 JSON 형태의 펌웨어 요청 메시지를 전송합니다.
토픽 ID: v1/{그룹 ID}/{기기 ID}/firmware/download/request
*/
func (m *MQTTClient) PublishDownloadRequest(req *types.FirmwareDeployRequest) {
	var wg sync.WaitGroup
	command := types.FirmwareDownloadCommand{
		CommandID: req.FileInfo.DeploymentId,
		SignedURL: req.SignedUrl,
		Version:   req.FileInfo.Version,
		Checksum:  req.FileInfo.FileHash,
		Size:      req.FileInfo.FileSize,
		Timeout:   req.FileInfo.ExpiresAt,
		Timestamp: req.FileInfo.DeployedAt,
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
			topic := fmt.Sprintf("v1/%d/%d/%d/firmware/download/request", d.RegionId, d.GroupId, d.DeviceId)
			token := m.mqttClient.Publish(topic, 2, false, payload)

			event := types.FirmwareDownloadEvent{
				CommandID:        command.CommandID,
				GroupID:          d.GroupId,
				RegionID:         d.RegionId,
				DeviceID:         d.DeviceId,
				Message:          "Published to Device",
				Status:           "WAITING",
				Progress:         0,
				TotalBytes:       command.Size,
				DownloadBytes:    0,
				SpeedKbps:        0,
				ChecksumVerified: false,
				DownloadTime:     0,
			}

			repository.InsertChan <- event
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
