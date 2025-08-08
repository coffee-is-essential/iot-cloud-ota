package repository

import (
	"context"
	"log"
	"sync"

	"github.com/questdb/go-questdb-client/v3"
)

var (
	questDBClientInit     sync.Once
	questDBClientInstance *DBClient
)

type DBClient struct {
	sender questdb.LineSender
}

func NewDBClient() *DBClient {
	questDBClientInit.Do(func() {
		questDBClientInstance = &DBClient{}
	})
	return questDBClientInstance
}

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
