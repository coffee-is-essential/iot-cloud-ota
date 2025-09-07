package repository

import (
	"context"
	"log"
)

// InsertChan 채널로부터 다운로드 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartEventConsumer() {
	for event := range DownloadInsertChan {
		ctx := context.TODO()
		err := c.sender.Table("download_events").
			Symbol("command_id", event.CommandID).
			Symbol("message", event.Message).
			Symbol("status", event.Status).
			Int64Column("device_id", event.DeviceID).
			Int64Column("progress", event.Progress).
			Int64Column("total_bytes", event.TotalBytes).
			Int64Column("download_bytes", event.DownloadBytes).
			Float64Column("speed_kbps", event.SpeedKbps).
			BoolColumn("checksum_verified", event.ChecksumVerified).
			Int64Column("download_seconds", event.DownloadSeconds).
			At(ctx, event.Timestamp.UTC())

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

// InsertChan 채널로부터 시스템 상태 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartSystemStatusConsumer() {
	for event := range SystemStatusInsertChan {
		ctx := context.TODO()

		// 1. system_status 저장
		if err := c.sender.Table("system_status").
			Int64Column("device_id", event.DeviceId).
			Float64Column("cpu_core_0", event.System.CpuUsage.Core0).
			Float64Column("cpu_core_1", event.System.CpuUsage.Core1).
			Float64Column("memory_usage", event.System.MemoryUsage).
			Float64Column("storage_usage", event.System.StorageUsage).
			Int64Column("uptime", event.System.Uptime).
			At(ctx, event.Timestamp.UTC()); err != nil {
			log.Printf("[ERROR] system_status Insert 실패: %v", err)
		}

		// 2. network_status 저장
		if err := c.sender.Table("network_status").
			Int64Column("device_id", event.DeviceId).
			StringColumn("connection_type", event.Network.ConnectionType).
			Int64Column("signal_strength", event.Network.SignalStrength).
			StringColumn("local_ip", event.Network.LocalIp).
			StringColumn("gateway_ip", event.Network.GatewayIp).
			At(ctx, event.Timestamp.UTC()); err != nil {
			log.Printf("[ERROR] network_status Insert 실패: %v", err)
		}

		// 3. advertisement_status 저장
		for _, ad := range event.Advertisements {
			err := c.sender.Table("advertisement_status").
				Int64Column("device_id", event.DeviceId).
				Int64Column("ad_id", ad.Id).
				At(ctx, event.Timestamp.UTC())
			if err != nil {
				log.Printf("[ERROR] advertisement_status Insert 실패: %v", err)
			}
		}

		// 4. firmware_status 저장
		if err := c.sender.Table("firmware_status").
			Int64Column("device_id", event.DeviceId).
			Int64Column("firmware_id", event.FirmwareId).
			At(ctx, event.Timestamp.UTC()); err != nil {
			log.Printf("[ERROR] firmware_status Insert 실패: %v", err)
		}

		// 최종 flush
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - device_id: %d (system + network + %d ads)",
				event.DeviceId, len(event.Advertisements))
		}
	}
}

// InsertChan 채널로부터 에러 로그 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartErrorLogConsumer() {
	for event := range ErrorLogInsertChan {
		ctx := context.TODO()
		err := c.sender.Table("error_logs").
			Int64Column("device_id", event.DeviceId).
			StringColumn("error_tag", event.ErrorLog.ErrorTag).
			StringColumn("log", event.ErrorLog.Log).
			At(ctx, event.ErrorLog.Timestamp.UTC())

		if err != nil {
			log.Printf("[ERROR] QuestDB Insert 실패: %v", err)
			continue
		}
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - device_id: %d, error_tag: %s", event.DeviceId, event.ErrorLog.ErrorTag)
		}
	}
}

func (c *DBClient) StartAllConsumer() {
	go c.StartEventConsumer()
	go c.StartSystemStatusConsumer()
	go c.StartErrorLogConsumer()
}
