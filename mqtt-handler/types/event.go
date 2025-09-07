package types

import "time"

type DownloadEvent struct {
	CommandID        string
	DeviceID         int64
	Message          string
	Status           string
	Progress         int64
	TotalBytes       int64
	DownloadBytes    int64
	SpeedKbps        float64
	ChecksumVerified bool
	DownloadSeconds  int64
	Timestamp        time.Time
}

type DownloadCancelAck struct {
	CommandID string    `json:"command_id"`
	Status    string    `json:"status"`
	Timestamp time.Time `json:"timestamp"`
}

type DownloadResult struct {
	CommandID        string    `json:"command_id"`
	Status           string    `json:"status"`
	Message          string    `json:"message"`
	ChecksumVerified bool      `json:"checksum_verified"`
	DownloadSeconds  int64     `json:"download_seconds"`
	Timestamp        time.Time `json:"timestamp"`
}

type DownloadProgress struct {
	CommandID       string    `json:"command_id"`
	Progress        int64     `json:"progress"`
	DownloadedBytes int64     `json:"downloaded_bytes"`
	TotalBytes      int64     `json:"total_bytes"`
	SpeedKbps       float64   `json:"speed_kbps"`
	Timestamp       time.Time `json:"timestamp"`
}

type DownloadRequestAck struct {
	CommandID string    `json:"command_id"`
	Status    string    `json:"status"`
	Timestamp time.Time `json:"timestamp"`
}
