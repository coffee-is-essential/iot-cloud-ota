package repository

import (
	"context"
	"log"
	"mqtt-handler/types"
	"sync"

	"github.com/questdb/go-questdb-client/v3"
)

// DB 클라이언트 싱글톤 초기화를 위한 sync.Once 및 인스턴스
var (
	questDBClientInit     sync.Once
	questDBClientInstance *DBClient
)

// 이벤트 처리를 위한 비동기 채널 (버퍼: 1000개)
var InsertChan = make(chan types.FirmwareDownloadEvent, 1000)

// DBClient는 QuestDB와 통신하기 위한 객체입니다.
type DBClient struct {
	sender questdb.LineSender
}

// 싱글톤 패턴으로 DBClient 인스턴스를 한 번만 생성합니다.
func NewDBClient() *DBClient {
	questDBClientInit.Do(func() {
		questDBClientInstance = &DBClient{}
	})
	return questDBClientInstance
}

// QuestDB에 연결을 수행하고, 연결 성공 시 LineSender를 초기화합니다.
func (c *DBClient) Connect(ctx context.Context, configStr string) error {
	sender, err := questdb.LineSenderFromConf(ctx, configStr)
	if err != nil {
		log.Fatalf("[QuestDB] 연결 실패")
		return err
	}
	c.sender = sender
	log.Println("[QuestDB] 연결 완료")
	return nil
}
