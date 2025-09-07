package network

import (
	"encoding/json"
	"log"
	"mqtt-handler/mqttclient"
	"mqtt-handler/types"
	"net/http"
	"sync"
)

// advertisementRouterInit: advertisementRouter 싱글톤 초기화를 위한 sync.Once
var (
	advertisementRouterInit     sync.Once
	advertisementRouterInstance *advertisementRouter
)

// advertisementRouter: 광고 배포 관련 라우팅 로직을 담당하는 구조체
type advertisementRouter struct {
	router     *Network
	mqttClient *mqttclient.MQTTClient
}

// advertisementRouter를 한 번만 초기화하고, 요청 경로에 대한 핸들러를 등록합니다.
func newAdsRouter(router *Network) *advertisementRouter {
	advertisementRouterInit.Do(func() {
		advertisementRouterInstance = &advertisementRouter{
			router:     router,
			mqttClient: mqttclient.NewMqttClient(),
		}
		router.adsDeployPOST("/api/advertisements/deployment", advertisementRouterInstance.sendAdvertisement)
	})

	return advertisementRouterInstance
}

// 광고 배포 요청을 처리하는 핸들러 함수
func (f *advertisementRouter) sendAdvertisement(w http.ResponseWriter, r *http.Request) {
	var req types.AdsDeployRequest

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		f.router.failedResponse(w, types.AdsDeployResponse{
			ApiResponse: types.NewApiResponse("파싱 오류"),
		})
		return
	}

	if len(req.Devices) == 0 || len(req.Contents) == 0 || req.CommandId == "" {
		f.router.failedResponse(w, types.AdsDeployResponse{
			ApiResponse: types.NewApiResponse("필수 필드 누락"),
		})
		return
	}

	PrintDownloadAdsRequestLog(&req)

	f.mqttClient.PublishAdsDownloadRequest(&req)
	f.router.okResponse(w, types.AdsDeployResponse{
		ApiResponse: types.NewApiResponse("광고 배포 요청 성공"),
	})

}

// 광고 배포 요청을 처리하는 POST 전용 라우팅 핸들러 등록 함수
func (n *Network) adsDeployPOST(path string, handler http.HandlerFunc) {
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

// PrintDownloadAdsRequestLog: 광고 배포 요청 로그를 출력하는 함수
func PrintDownloadAdsRequestLog(req *types.AdsDeployRequest) {
	log.Println("================ 광고 배포 요청 ================")
	log.Printf("Command ID: %s\n", req.CommandId)
	log.Printf("Contents Count: %d\n", len(req.Contents))
	for i, content := range req.Contents {
		log.Printf("  Content %d:\n", i+1)
		log.Printf("    Signed URL: %s\n", content.SignedUrl.Url)
		log.Printf("    Timeout: %d minutes\n", content.SignedUrl.Timeout)
		log.Printf("    File Info - ID: %d, Hash: %s, Size: %d bytes\n",
			content.FileInfo.Id, content.FileInfo.FileHash, content.FileInfo.Size)
	}
	for _, device := range req.Devices {
		log.Printf("Device ID: %d\n", device.DeviceId)
	}
	log.Printf("Timestamp: %s\n", req.Timestamp)
	log.Println("==============================================")
}
