package types

type FirmwareDeployRequest struct {
	SignedUrl string      `json:"signedUrl"`
	FileInfo  FileInfo    `json:"fileInfo"`
	TopicList []TopicItem `json:"topicList"`
}

type FileInfo struct {
	DeploymentId string `json:"deploymentId"`
	Version      string `json:"version"`
	FileHash     string `json:"fileHash"`
	FileSize     int64  `json:"fileSize"`
	ExpiresAt    string `json:"expiresAt"`
	DeployedAt   string `json:"deployedAt"`
}

type TopicItem struct {
	Topic string `json:"topic"`
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

type FirmwareAck struct {
	CommandID string `json:"command_id"`
	Status    string `json:"status"`
	Message   string `json:"message"`
	Timestamp string `json:"timestamp"`
}

type FirmwareProgress struct {
	CommandID       string `json:"command_id"`
	Progress        int64  `json:"progress"`
	DownloadedBytes int64  `json:"downloaded_bytes"`
	TotalBytes      int64  `json:"total_bytes"`
	SpeedKbps       int64  `json:"speed_kbps"`
	EtaSeconds      int64  `json:"eta_seconds,omitempty"`
	Timestamp       string `json:"timestamp"`
}
