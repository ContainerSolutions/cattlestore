package main // import "github.com/ContainerSolutions/cattlestore"

import (
	"fmt"
	"html"
	"log"
	"net/http"
	"time"
)

func main() {
	health := true

	http.HandleFunc("/tick", func(w http.ResponseWriter, r *http.Request) {
		log.Print("tick")
		health = false
		time.Sleep(20 * time.Second)
		health = true
		fmt.Fprintf(w, "Hello, %q", html.EscapeString(r.URL.Path))
	})

	http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
		var msg string
		if health == true {
			msg = "0" // passing
		} else {
			msg = "2" // failing
			http.Error(w, "oops", 500)
		}
		fmt.Fprintf(w, msg)
	})

	log.Print("Ready...")
	log.Fatal(http.ListenAndServe(":8080", nil))
}
