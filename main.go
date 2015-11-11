package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/braintree/manners"
	"log"
	"net/http"
	"sync/atomic"
)

const max uint64 = 10

var (
	bind string
	ops  uint64 = 0
)

type Message struct {
	Ops uint64
	Max uint64
}

func init() {
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

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		tmpOps := atomic.LoadUint64(&ops)

		marshal(w, tmpOps, max)

		log.Print(fmt.Sprintf("tock (%d/%d)", tmpOps+1, max))

		if tmpOps >= max {
			manners.Close()
		}

		atomic.AddUint64(&ops, 1)
	})

	http.HandleFunc("/info", func(w http.ResponseWriter, r *http.Request) {
		marshal(w, atomic.LoadUint64(&ops), max)
	})

	log.Printf("Ready to serve %d times on address %s", max, bind)
	log.Fatal(manners.ListenAndServe(bind, nil))
}
