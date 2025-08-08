package main

import (
	"mqtt-handler/init/cmd"
)

func main() {
	//ctx := context.TODO()
	//
	//client, err := questdb.LineSenderFromConf(ctx, "http::addr=localhost:19000;")
	//if err != nil {
	//	panic("Failed to create client")
	//}
	//
	//err = client.Table("trades123").
	//	Symbol("symbol", "test").
	//	Symbol("side", "희희").
	//	Float64Column("price", 123.45).
	//	Float64Column("amount", 1.23456).
	//	AtNow(ctx)
	//
	//if err != nil {
	//	panic("Failed to insert data")
	//}
	//
	//err = client.Flush(ctx)
	//if err != nil {
	//	panic("Failed to flush data")
	//}
	cmd.NewCmd("config.toml")
}
