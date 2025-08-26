package main

import (
	"log"
	"mqtt-handler/init/cmd"
	"os"
)

func main() {
	log.SetOutput(os.Stdout)
	cmd.NewCmd()
}
