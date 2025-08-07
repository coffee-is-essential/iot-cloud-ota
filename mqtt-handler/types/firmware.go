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
