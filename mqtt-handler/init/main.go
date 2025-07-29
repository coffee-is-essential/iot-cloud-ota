package main

import (
	"mqtt-handler/init/cmd"
)

func main() {
	//client := mqtt.NewClient("tcp://emqx-nlb-bfefab8efb8db52f.elb.ap-northeast-2.amazonaws.com:1883", "mqtt-handler")
	//client.Publish("test/topic", 1, false, "test입니다.")
	cmd.NewCmd("config.toml")
}
