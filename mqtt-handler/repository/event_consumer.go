package repository

import (
	"context"
	"log"
)

// InsertChan 채널로부터 다운로드 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartEventConsumer() {
	for event := range DownloadInsertChan {
		ctx := context.TODO()
		err := c.sender.Table("firmware_download_events").
			Symbol("command_id", event.CommandID).
			Symbol("message", event.Message).
			Symbol("status", event.Status).
			Int64Column("device_id", event.DeviceID).
			Int64Column("progress", event.Progress).
			Int64Column("total_bytes", event.TotalBytes).
			Int64Column("download_bytes", event.DownloadBytes).
			Float64Column("speed_kbps", event.SpeedKbps).
			BoolColumn("checksum_verified", event.ChecksumVerified).
			Float64Column("download_time", event.DownloadTime).
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
		err := c.sender.Table("system_status").
			Int64Column("device_id", event.DeviceId).
			Float64Column("cpu_usage", event.CpuUsage).
			Float64Column("memory_usage", event.MemoryUsage).
			Float64Column("disk_usage", event.DiskUsage).
			Int64Column("uptime", event.Uptime).
			At(ctx, event.Timestamp.UTC())

		if err != nil {
			log.Printf("[ERROR] QuestDB Insert 실패: %v", err)
			continue
		}
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - device_id: %d, cpu_usage: %f, memory_usage: %f, disk_usage: %f, uptime: %d", event.DeviceId, event.CpuUsage, event.MemoryUsage, event.DiskUsage, event.Uptime)
		}
	}
}

// InsertChan 채널로부터 네트워크 상태 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartNetworkStatusConsumer() {
	for event := range NetworkStatusInsertChan {
		ctx := context.TODO()
		err := c.sender.Table("network_status").
			Symbol("connection_type", event.ConnectionType).
			Symbol("ip_address", event.IpAddress).
			Symbol("gateway", event.Gateway).
			Int64Column("device_id", event.DeviceId).
			Int64Column("signal_strength", event.SignalStrength).
			At(ctx, event.Timestamp.UTC())

		if err != nil {
			log.Printf("[ERROR] QuestDB Insert 실패: %v", err)
			continue
		}
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - device_id: %d, connection_type: %s, signal_strenght: %d, ip_address: %s, gateway: %s, timestamp: %s", event.DeviceId, event.ConnectionType, event.SignalStrength, event.IpAddress, event.Gateway, event.Timestamp)
		}
	}
}

// InsertChan 채널로부터 health 상태 이벤트를 지속적으로 수신하여 QuestDB에 기록하는 백그라운드 consumer 함수입니다.
func (c *DBClient) StartHealthStatusConsumer() {
	for event := range HealthStatusInsertChan {
		ctx := context.TODO()
		err := c.sender.Table("health_status").
			Symbol("overall_status", event.OverallStatus).
			Symbol("firmware_version", event.FirmwareVersion).
			Int64Column("device_id", event.DeviceId).
			Int64Column("error_count", event.ErrorCount).
			Int64Column("warning_count", event.WarningCount).
			TimestampColumn("last_reboot", event.LastReboot.UTC()).
			At(ctx, event.Timestamp.UTC())

		if err != nil {
			log.Printf("[ERROR] QuestDB Insert 실패: %v", err)
			continue
		}
		if err := c.sender.Flush(ctx); err != nil {
			log.Printf("[ERROR] QuestDB Flush 실패: %v", err)
		} else {
			log.Printf("[DB] Insert 성공 - device_id: %d, overall_status: %s, firmware_version: %s, error_count %d, warning_count: %d, last_reboot: %s, timestamp: %s",
				event.DeviceId, event.OverallStatus, event.FirmwareVersion, event.ErrorCount, event.WarningCount, event.LastReboot, event.Timestamp)
		}
	}
}

func (c *DBClient) StartAllConsumer() {
	go c.StartEventConsumer()
	go c.StartSystemStatusConsumer()
	go c.StartNetworkStatusConsumer()
	go c.StartHealthStatusConsumer()
}
