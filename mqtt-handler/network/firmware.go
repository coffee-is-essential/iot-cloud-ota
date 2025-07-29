package network

import (
	"encoding/json"
	"fmt"
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
	router *Network
	// TODO MQTT Service 추가
}

// newFirmwareRouter는 firmwareRouter를 한 번만 초기화하고,
// 요청 경로에 대한 핸들러를 등록합니다.
func newFirmwareRouter(router *Network) *firmwareRouter {
	firmwareRouterInit.Do(func() {
		firmwareRouterInstance = &firmwareRouter{
			router: router,
		}
		router.firmwareDeployPOST("/api/firmwares/deployment", firmwareRouterInstance.firmwareDeploy)

	})

	return firmwareRouterInstance
}

// firmwareDeploy는 펌웨어 배포 요청을 처리하는 핸들러입니다.
func (f *firmwareRouter) firmwareDeploy(w http.ResponseWriter, r *http.Request) {
	var req types.FirmwareDeployRequest

	decoder := json.NewDecoder(r.Body)
	if err := decoder.Decode(&req); err != nil {
		f.router.failedResponse(w, types.FirmwareDeployResponse{
			ApiResponse: types.NewApiResponse("펌웨어 배포 요청 오류"),
		})
		return
	}

	fmt.Println("=== Firmware Request Received ===")
	fmt.Println("Signed URL:", req.SignedUrl)
	fmt.Println("Version:", req.FileInfo.Version)
	fmt.Println("Deployment ID:", req.FileInfo.DeploymentId)
	fmt.Println("Expires At:", req.FileInfo.ExpiresAt)
	fmt.Println("Devices:")
	for _, d := range req.Devices {
		fmt.Printf(" - DeviceID: %d, GroupID: %d, RegionID: %d\n", d.DeviceId, d.GroupId, d.RegionId)
	}
	fmt.Println("=================================")

	f.router.okResponse(w, types.FirmwareDeployResponse{
		ApiResponse: types.NewApiResponse("배포 요청 성공"),
	})
}

func (n *Network) firmwareDeployPOST(path string, handler http.HandlerFunc) {
	postOnlyHandler := func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodPost {
			n.failedResponse(w, types.FirmwareDeployResponse{
				ApiResponse: types.NewApiResponse("Method Not Allowed"),
			})
			return
		}
		handler(w, r)
	}

	n.mux.HandleFunc(path, postOnlyHandler)
}
