package main // import "github.com/containersol/cattlestore"

import (
	"flag"
	"fmt"
	"log"
	"net/http"
	"time"
)

var (
	delayRead int
	delay     time.Duration
	bind      string
)

func init() {
	flag.IntVar(&delayRead, "delay", 10, "the amount of seconds the web server will take to finish serving a request")
	flag.StringVar(&bind, "bind", ":8080", "ip:port pair the web server will listen on")
	delay = time.Duration(delayRead) * time.Second
}

func main() {
	flag.Parse()

	healthy := true

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		log.Print("tick")
		healthy = false
		time.Sleep(delay)
		healthy = true
		fmt.Fprintf(w, "tock")
	})

	log.Printf("Ready to listen on %s", bind)
	log.Fatal(http.ListenAndServe(bind, nil))
}