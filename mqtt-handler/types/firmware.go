package types

type FirmwareDeployRequest struct {
	SignedUrl string       `json:"signedUrl"`
	FileInfo  FileInfo     `json:"fileInfo"`
	Devices   []DeviceInfo `json:"devices"`
}

type FileInfo struct {
	DeploymentId string `json:"deploymentId"`
	Version      string `json:"version"`
	FileHash     string `json:"fileHash"`
	FileSize     int64  `json:"fileSize"`
	ExpiresAt    string `json:"expiresAt"`
	DeployedAt   string `json:"deployedAt"`
}

type DeviceInfo struct {
	DeviceId int `json:"deviceId"`
	GroupId  int `json:"groupId"`
	RegionId int `json:"regionId"`
}

type FirmwareDeployResponse struct {
	*ApiResponse
}
