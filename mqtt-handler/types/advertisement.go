package types

import "time"

type AdsDeployRequest struct {
	CommandId string      `json:"command_id"`
	Contents  []Content   `json:"contents"`
	Devices   []DeviceIds `json:"devices"`
	Timestamp time.Time   `json:"timestamp"`
}

type Content struct {
	SignedUrl SignedUrl `json:"signed_url"`
	FileInfo  FileInfo  `json:"file_info"`
}

type FileInfo struct {
	Id       int64  `json:"id"`
	FileHash string `json:"file_hash"`
	Size     int64  `json:"size"`
}

type SignedUrl struct {
	Url     string `json:"url"`
	Timeout int64  `json:"timeout"`
}

type AdsDeployResponse struct {
	*ApiResponse
}

type AdsDownloadCommand struct {
	CommandID string    `json:"command_id"`
	Contents  []Content `json:"contents"`
	Timestamp time.Time `json:"timestamp"`
}
