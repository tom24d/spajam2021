package apis

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"sync"

	"github.com/tom24d/spajam2021/backend/golang/pkg/apis/v1"
	"github.com/tom24d/spajam2021/backend/golang/pkg/config"
)

type handler struct {
	mu sync.Mutex
}

const prefix = "/v1"

func (h *handler) handleRequest(w http.ResponseWriter, r *http.Request) {
	req := v1.Request{}

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		log.Println(err)
		w.WriteHeader(http.StatusInternalServerError)
		return
	}
	defer r.Body.Close()

	log.Println(req.Message)

	w.WriteHeader(http.StatusAccepted)
}

func NewHandler(stopCh <-chan struct{}, mux *http.ServeMux, env config.Accessor) {
	handler := handler{}

	wasIn := make(chan struct{}, 1)
	go func(stopCh <-chan struct{}) {
		wasIn <- struct{}{}
	}(stopCh)
	<-wasIn

	mux.HandleFunc(fmt.Sprintf("%s/queue", prefix), handler.handleRequest)

	log.Println("v1 handler initialization succeeded")
}
