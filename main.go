package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/braintree/manners"
	"log"
	"math/rand"
	"net/http"
	"sync/atomic"
	"time"
)

var (
	bind string
	ops  uint64 = 0
)

type Message struct {
	Ops uint64 `json:"ops"`
	Max int    `json:"max"`
}

func init() {
	flag.StringVar(&bind, "bind", ":8080", "ip:port pair the web server will listen on")
}

func marshal(w http.ResponseWriter, o uint64, m int) {
	if b, err := json.Marshal(Message{o, m}); err != nil {
		http.Error(w, "Couldn't marshal message", 500)
	} else {
		fmt.Fprintf(w, "%s", b)
	}
}

func cant_take_it_anymore(max int) {
	pingTicker := time.NewTicker(1 * time.Second)
	defer func() {
		pingTicker.Stop()
	}()

	for {
		select {
		case <-pingTicker.C:
			if atomic.LoadUint64(&ops) >= uint64(max) {
				manners.Close()
			}
		}
	}
}

func main() {
	flag.Parse()

	var max int = 10 + rand.New(rand.NewSource(time.Now().UnixNano())).Intn(21)

	go cant_take_it_anymore(max)

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		tmpOps := atomic.LoadUint64(&ops)

		marshal(w, tmpOps, max)

		log.Print(fmt.Sprintf("tock (%d/%d)", tmpOps+1, max))

		atomic.AddUint64(&ops, 1)
	})

	http.HandleFunc("/info", func(w http.ResponseWriter, r *http.Request) {
		marshal(w, atomic.LoadUint64(&ops), max)
	})

	log.Printf("Ready to serve %d times on address %s", max, bind)
	log.Fatal(manners.ListenAndServe(bind, nil))
}
