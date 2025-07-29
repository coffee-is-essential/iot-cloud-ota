package types

type ApiResponse struct {
	Description string `json:"description"`
}

func NewApiResponse(description string) *ApiResponse {
	return &ApiResponse{
		Description: description,
	}
}
