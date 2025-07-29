package network

import (
	"encoding/json"
	"net/http"
)

// 요청이 성공적으로 처리되었을 때 JSON 응답을 반환합니다.
func (n *Network) okResponse(w http.ResponseWriter, result interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusOK)
	_ = json.NewEncoder(w).Encode(result)
}

// 요청 처리 중 오류가 발생했을 때 JSON 에러 응답을 반환합니다.
func (n *Network) failedResponse(w http.ResponseWriter, result interface{}) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(http.StatusBadRequest)
	_ = json.NewEncoder(w).Encode(result)
}
