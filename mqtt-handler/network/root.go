package network

import (
	"log"
	"net/http"
)

// HTTP 서버를 구성하는 구조체 입니다.
// mux는 HTTP요청을 처리하는 라우터 역할을 합니다.
type Network struct {
	mux *http.ServeMux
}

// 새로운 Network 인스턴스를 생성하고 펌웨어 배포 요청을 처리할 라우팅 경로를 등록합니다.
func NewNetwork() *Network {
	net := &Network{
		mux: http.NewServeMux(),
	}

	newFirmwareRouter(net)
	newAdsRouter(net)
	return net
}

// HTTP 서버를 시작합니다.
// 주어진 포트에서 요청을 수신하며, mux에 등록된 핸들러에 따라 라우팅됩니다.
func (n *Network) ServerStart(port string) error {
	log.Println("서버 시작 [Port: " + port + "]")

	return http.ListenAndServe(port, n.mux)
}
