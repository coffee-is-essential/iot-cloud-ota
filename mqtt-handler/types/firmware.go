package types

import "time"

type FirmwareDeployRequest struct {
	CommandId string      `json:"command_id"`
	Content   Content     `json:"content"`
	Devices   []DeviceIds `json:"devices"`
	Timestamp time.Time   `json:"timestamp"`
}

type DeployCancelRequest struct {
	Devices   []DeviceIds `json:"devices"`
	CommandID string      `json:"commandId"`
	Reason    string      `json:"reason"`
	Timestamp time.Time   `json:"timestamp"`
}

type DeviceIds struct {
	DeviceId int64 `json:"deviceId"`
}

type FirmwareDeployResponse struct {
	*ApiResponse
}

type FirmwareDownloadCommand struct {
	CommandID string    `json:"command_id"`
	Content   Content   `json:"content"`
	Timestamp time.Time `json:"timestamp"`
}

type DownloadCancelCommand struct {
	CommandID string    `json:"command_id"`
	Reason    string    `json:"reason"`
	Timestamp time.Time `json:"timestamp"`
}
