package types

type FirmwareDeployRequest struct {
	SignedUrl string      `json:"signedUrl"`
	FileInfo  FileInfo    `json:"fileInfo"`
	Devices   []DeviceIds `json:"devices"`
}

type FirmwareDeployCancelRequest struct {
	Devices   []DeviceIds `json:"devices"`
	CommandID string      `json:"commandId"`
	Reason    string      `json:"reason"`
}

type FileInfo struct {
	DeploymentId string `json:"deploymentId"`
	Version      string `json:"version"`
	FileHash     string `json:"fileHash"`
	FileSize     int64  `json:"fileSize"`
	ExpiresAt    string `json:"expiresAt"`
	DeployedAt   string `json:"deployedAt"`
}

type DeviceIds struct {
	DeviceId int64 `json:"deviceId"`
}

type FirmwareDeployResponse struct {
	*ApiResponse
}

type FirmwareDownloadCommand struct {
	CommandID string `json:"command_id"`
	SignedURL string `json:"signed_url"`
	Version   string `json:"version"`
	Checksum  string `json:"checksum"`
	Size      int64  `json:"size"`
	Timeout   string `json:"timeout"`
	Timestamp string `json:"timestamp"`
}

type FirmwareDownloadCancelCommand struct {
	CommandID string `json:"command_id"`
	Reason    string `json:"reason"`
}

type FirmwareDownloadRequestAck struct {
	CommandID string `json:"command_id"`
	Status    string `json:"status"`
	Message   string `json:"message"`
	Timestamp string `json:"timestamp"`
}

type FirmwareDownloadProgress struct {
	CommandID       string  `json:"command_id"`
	Progress        int64   `json:"progress"`
	DownloadedBytes int64   `json:"downloaded_bytes"`
	TotalBytes      int64   `json:"total_bytes"`
	SpeedKbps       float64 `json:"speed_kbps"`
	EtaSeconds      int64   `json:"eta_seconds,omitempty"`
	Timestamp       string  `json:"timestamp"`
}

type FirmwareDownloadResult struct {
	CommandID        string  `json:"command_id"`
	Status           string  `json:"status"`
	Message          string  `json:"message"`
	ChecksumVerified bool    `json:"checksum_verified"`
	DownloadTime     float64 `json:"download_time"`
	Timestamp        string  `json:"timestamp"`
}

type FirmwareDownloadCancelAck struct {
	CommandID string `json:"command_id"`
	Status    string `json:"status"`
	Message   string `json:"message"`
	Timestamp string `json:"timestamp"`
}

type FirmwareDownloadEvent struct {
	CommandID        string
	DeviceID         int64
	Message          string
	Status           string
	Progress         int64
	TotalBytes       int64
	DownloadBytes    int64
	SpeedKbps        float64
	ChecksumVerified bool
	DownloadTime     float64
}
