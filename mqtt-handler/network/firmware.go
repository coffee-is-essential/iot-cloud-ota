package network

import (
	"encoding/json"
	"fmt"
	"mqtt-handler/mqttclient"
	"mqtt-handler/types"
	"net/http"
	"sync"
)

// firmwareRouterInit: firmwareRouter 싱글톤 초기화를 위한 sync.Once
// firmwareRouterInstance: 실제 싱글톤 인스턴스
var (
	firmwareRouterInit     sync.Once
	firmwareRouterInstance *firmwareRouter
)

// 펌웨어 배포 관련 라우팅 로직을 담당하는 구조체
type firmwareRouter struct {
	router     *Network
	mqttClient *mqttclient.MQTTClient
}

// firmwareRouter를 한 번만 초기화하고, 요청 경로에 대한 핸들러를 등록합니다.
func newFirmwareRouter(router *Network) *firmwareRouter {
	firmwareRouterInit.Do(func() {
		firmwareRouterInstance = &firmwareRouter{
			router:     router,
			mqttClient: mqttclient.NewMqttClient(),
		}
		router.firmwareDeployPOST("/api/firmwares/deployment", firmwareRouterInstance.firmwareDeploy)
		router.firmwareDeployPOST("/api/firmwares/deployment/cancel", firmwareRouterInstance.cancelFirmwareDeploy)

	})

	return firmwareRouterInstance
}

// 클라이언트의 펌웨어 배포 요청을 처리하는 엔드포인트입니다.
func (f *firmwareRouter) firmwareDeploy(w http.ResponseWriter, r *http.Request) {
	var req types.FirmwareDeployRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("파싱 오류"),
		})
		return
	}

	if req.SignedUrl == "" || req.FileInfo.Version == "" || len(req.Devices) == 0 {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("필수 필드 누락"),
		})
		return
	}

	PrintDownloadLog(&req)

	f.mqttClient.PublishDownloadRequest(&req)
	f.router.okResponse(w, types.FirmwareDeployResponse{
		ApiResponse: types.NewApiResponse("배포 요청 성공"),
	})
}

// 클라이언트의 펌웨어 배포 취소 요청을 처리하는 엔드포인트입니다.
func (f *firmwareRouter) cancelFirmwareDeploy(w http.ResponseWriter, r *http.Request) {
	var req types.FirmwareDeployCancelRequest
	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("파싱 오류"),
		})
		return
	}

	if req.CommandID == "" || req.Reason == "" || len(req.Devices) == 0 {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("필수 필드 누락"),
		})
		return
	}

	PrintDownloadCancelLog(&req)

	f.mqttClient.PublishDownloadCancelRequest(&req)
	f.router.okResponse(w, types.FirmwareDeployResponse{
		ApiResponse: types.NewApiResponse("배포 요청 성공"),
	})
}

// POST 요청만 허용하는 핸들러로 path를 등록합니다.
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

// 디버깅용 요청 로그 출력 함수
func PrintDownloadLog(req *types.FirmwareDeployRequest) {
	fmt.Println("Signed URL: ", req.SignedUrl)
	fmt.Println("Deployment ID: ", req.FileInfo.DeploymentId)
	fmt.Println("Version: ", req.FileInfo.Version)
	fmt.Println("hash: ", req.FileInfo.FileHash)
	fmt.Println("FileSize", req.FileInfo.FileSize)
	fmt.Println("Expired At", req.FileInfo.ExpiresAt)
	fmt.Println("deployed At", req.FileInfo.DeployedAt)
	for _, device := range req.Devices {
		fmt.Printf("[Device] - Device ID: %d\n", device.DeviceId)
	}
}

// 디버깅용 요청 로그 출력 함수
func PrintDownloadCancelLog(req *types.FirmwareDeployCancelRequest) {
	fmt.Println("Command ID: ", req.CommandID)
	fmt.Println("Reason: ", req.Reason)
	for _, device := range req.Devices {
		fmt.Printf("[Device] - Device ID: %d\n", device.DeviceId)
	}
}
