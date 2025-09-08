package types

import "time"

type SystemStatus struct {
	FirmwareId     int64            `json:"firmware_id"`
	Advertisements []Advertisements `json:"advertisements"`
	System         System           `json:"system"`
	Network        Network          `json:"network"`
	Timestamp      time.Time        `json:"timestamp"`
}

type Advertisements struct {
	Id int64 `json:"id"`
}

type System struct {
	CpuUsage     CpuUsage `json:"cpu_usage"`
	MemoryUsage  float64  `json:"memory_usage"`
	StorageUsage float64  `json:"storage_usage"`
	Uptime       int64    `json:"uptime"`
}

type CpuUsage struct {
	Core0 float64 `json:"core_0"`
	Core1 float64 `json:"core_1"`
}

type Network struct {
	ConnectionType string `json:"connection_type"`
	SignalStrength int64  `json:"signal_strength"`
	LocalIp        string `json:"local_ip"`
	GatewayIp      string `json:"gateway_ip"`
}

type SystemStatusEvent struct {
	DeviceId       int64
	FirmwareId     int64
	Advertisements []Advertisements
	System         System
	Network        Network
	Timestamp      time.Time
}

type ErrorLog struct {
	ErrorTag  string `json:"error_tag"`
	Log       string `json:"log"`
	Timestamp time.Time
}

type ErrorLogEvent struct {
	DeviceId int64
	ErrorLog ErrorLog
}
