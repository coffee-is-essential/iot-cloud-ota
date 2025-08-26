package repository

import (
	"context"
	"log"
)

// InsertChan 채널로부터 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartEventConsumer() {
	for event := range InsertChan {
		ctx := context.TODO()
		err := c.sender.Table("firmware_download_events").
			Symbol("command_id", event.CommandID).
			Symbol("message", event.Message).
			Symbol("status", event.Status).
			Int64Column("device_id", event.DeviceID).
			Int64Column("progress", event.Progress).
			Int64Column("total_bytes", event.TotalBytes).
			Int64Column("download_bytes", event.DownloadBytes).
			Int64Column("speed_kbps", event.SpeedKbps).
			BoolColumn("checksum_verified", event.ChecksumVerified).
			Int64Column("download_time", event.DownloadTime).
			AtNow(ctx)

		if err != nil {
			log.Printf("[ERROR] QuestDB Insert 실패: %v", err)
			continue
		}
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - command_id: %s, device_id: %d, status: %s", event.CommandID, event.DeviceID, event.Status)
		}
	}
}
