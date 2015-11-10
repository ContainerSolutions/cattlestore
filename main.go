package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"log"
	"net/http"
	"sync/atomic"
	"time"
)

const max uint64 = 10

var (
	delayRead uint
	delay     time.Duration
	bind      string
	ops       uint64 = 0
)

type Message struct {
	Ops uint64
	Max uint64
}

func init() {
	flag.UintVar(&delayRead, "delay", 1500, "the number of milliseconds the web server will take to finish serving a request")
	flag.StringVar(&bind, "bind", ":8080", "ip:port pair the web server will listen on")
}

func marshal(w http.ResponseWriter, o uint64, m uint64) {
	if b, err := json.Marshal(Message{o, m}); err != nil {
		http.Error(w, "Couldn't marshal message", 500)
	} else {
		fmt.Fprintf(w, "%s", b)
	}
}

func main() {
	flag.Parse()
	delay = time.Duration(delayRead) * time.Millisecond

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		tmpOps := atomic.LoadUint64(&ops)

		if tmpOps >= max {
			log.Fatal(fmt.Sprintf("I am so done with you after %d requests", max))
		}

		marshal(w, tmpOps, max)

		log.Print(fmt.Sprintf("tock (%d/%d)", tmpOps+1, max))
		atomic.AddUint64(&ops, 1)
		time.Sleep(delay)
	})

	http.HandleFunc("/info", func(w http.ResponseWriter, r *http.Request) {
		marshal(w, atomic.LoadUint64(&ops), max)
	})

	log.Printf("Ready to serve %d times, delayed %s, on address %s", max, delay, bind)
	log.Fatal(http.ListenAndServe(bind, nil))
}
