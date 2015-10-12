package main // import "github.com/ContainerSolutions/cattlestore"

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
	flag.StringVar(&bind, "bind", ":8080", "ip:port pair where the web server will listen on")
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

	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		var msg string
		if healthy {
			msg = "yay" // passing
		} else {
			msg = "nay" // failing
			http.Error(w, "oops", 500)
		}
		fmt.Fprintf(w, msg)
	})

	log.Printf("Ready to listen on %s", bind)
	log.Fatal(http.ListenAndServe(bind, nil))
}
