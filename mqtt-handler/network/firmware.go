package network

import (
	"encoding/json"
	"fmt"
	"mqtt-handler/mqttclient"
	"mqtt-handler/types"
	"net/http"
	"sync"
)

// firmwareRouter 싱글톤 초기화를 위한 변수들
var (
	firmwareRouterInit     sync.Once
	firmwareRouterInstance *firmwareRouter
)

// firmwareRouter는 펌웨어 관련 라우팅 기능을 담당합니다.
type firmwareRouter struct {
	router     *Network
	mqttClient *mqttclient.MQTTClient
}

// newFirmwareRouter는 firmwareRouter를 한 번만 초기화하고,
// 요청 경로에 대한 핸들러를 등록합니다.
func newFirmwareRouter(router *Network) *firmwareRouter {
	firmwareRouterInit.Do(func() {
		firmwareRouterInstance = &firmwareRouter{
			router:     router,
			mqttClient: mqttclient.NewMqttClient(),
		}
		router.firmwareDeployPOST("/api/firmwares/deployment", firmwareRouterInstance.firmwareDeploy)

	})

	return firmwareRouterInstance
}

// firmwareDeploy는 펌웨어 배포 요청을 처리하는 핸들러입니다.
func (f *firmwareRouter) firmwareDeploy(w http.ResponseWriter, r *http.Request) {
	var req types.FirmwareDeployRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("파싱 오류"),
		})
		return
	}

	if req.SignedUrl == "" || req.FileInfo.Version == "" || len(req.TopicList) == 0 {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("필수 필드 누락"),
		})
		return
	}

	PrintLog(&req)

	f.mqttClient.PublishDownloadRequest(&req)
	f.router.okResponse(w, types.FirmwareDeployResponse{
		ApiResponse: types.NewApiResponse("배포 요청 성공"),
	})
}

func (n *Network) firmwareDeployPOST(path string, handler http.HandlerFunc) {
	postOnlyHandler := func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			n.failedResponse(w, types.FirmwareDeployResponse{
				ApiResponse: types.NewApiResponse("잘못된 접근입니다."),
			})
			return
		}
		handler(w, r)
	}

	n.mux.HandleFunc(path, postOnlyHandler)
}

func PrintLog(req *types.FirmwareDeployRequest) {
	fmt.Println("signed URL: ", req.SignedUrl)
	fmt.Println("Deployment ID: ", req.FileInfo.DeploymentId)
	fmt.Println("Version: ", req.FileInfo.Version)
	fmt.Println("hash: ", req.FileInfo.FileHash)
	fmt.Println("FileSize", req.FileInfo.FileSize)
	fmt.Println("Expired At", req.FileInfo.ExpiresAt)
	fmt.Println("deployed At", req.FileInfo.DeployedAt)
	for _, topic := range req.TopicList {
		fmt.Println("Publish to topic:", topic.Topic)
	}
}
