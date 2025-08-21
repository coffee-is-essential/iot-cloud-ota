package main

import (
	"mqtt-handler/init/cmd"
)

func main() {
	cmd.NewCmd()
	print("MQTT Handler started successfully!\n")
}
