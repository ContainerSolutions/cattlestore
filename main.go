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
	Ops uint64
	Max int
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

func main() {
	var max int = 10
	add := rand.New(rand.NewSource(time.Now().UnixNano())).Intn(20)
	max += add
	log.Printf("added: %d, for max %d", add, max)

	flag.Parse()

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		tmpOps := atomic.LoadUint64(&ops)

		marshal(w, tmpOps, max)

		log.Print(fmt.Sprintf("tock (%d/%d)", tmpOps+1, max))

		if tmpOps >= uint64(max) {
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
