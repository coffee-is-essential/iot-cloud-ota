package types

import "time"

type SystemStatus struct {
	CpuUsage    float64   `json:"cpu_usage"`
	MemoryUsage float64   `json:"memory_usage"`
	DiskUsage   float64   `json:"disk_usage"`
	Temperature float64   `json:"temperature"`
	Uptime      int64     `json:"uptime"`
	Timestamp   time.Time `json:"timestamp"`
}

type SystemStatusEvent struct {
	DeviceId    int64
	CpuUsage    float64
	MemoryUsage float64
	DiskUsage   float64
	Temperature float64
	Uptime      int64
	Timestamp   time.Time
}

type NetworkStatus struct {
	ConnectionType string    `json:"connection_type"`
	SignalStrength int64     `json:"signal_strength"`
	IpAddress      string    `json:"ip_address"`
	Gateway        string    `json:"gateway"`
	Timestamp      time.Time `json:"timestamp"`
}

type NetworkStatusEvent struct {
	DeviceId       int64
	ConnectionType string
	SignalStrength int64
	IpAddress      string
	Gateway        string
	Timestamp      time.Time
}

type HealthStatus struct {
	OverallStatus   string    `json:"overall_status"`
	FirmwareVersion string    `json:"firmware_version"`
	LastReboot      time.Time `json:"last_reboot"`
	ErrorCount      int64     `json:"error_count"`
	WarningCount    int64     `json:"warning_count"`
	Timestamp       time.Time `json:"timestamp"`
}

type HealthStatusEvent struct {
	DeviceId        int64
	OverallStatus   string
	FirmwareVersion string
	LastReboot      time.Time
	ErrorCount      int64
	WarningCount    int64
	Timestamp       time.Time
}
