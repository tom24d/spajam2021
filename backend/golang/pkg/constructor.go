package pkg

import (
	"context"
	"fmt"
	"log"
	"net/http"

	"github.com/rs/cors"

	"github.com/kelseyhightower/envconfig"

	"github.com/tom24d/spajam2021/backend/golang/pkg/config"
)

type impl struct {
	config.EnvConfig

	mux *http.ServeMux
}

func New(ctx context.Context, ctors ...func(c <-chan struct{}, mux *http.ServeMux, env config.Accessor)) *impl {

	env := &config.EnvConfig{}
	if err := envconfig.Process("", env); err != nil {
		log.Fatalf("failed to process env var: %v", err)
	}

	mux := http.NewServeMux()

	for _, h := range ctors {
		h(ctx.Done(), mux, env)
	}

	return &impl{
		EnvConfig: *env,
		mux:       mux,
	}
}

func (impl *impl) Start(ctx context.Context) error {
	// TODO
	h := cors.AllowAll().Handler(impl.mux)
	server := &http.Server{Addr: fmt.Sprintf(":%d", impl.GetPort()), Handler: h}

	log.Printf("start to listen on: %d", impl.GetPort())

	go func() {
		if err := server.ListenAndServe(); err != nil {
			log.Fatalf("error while starting server: %v", err)
		}
	}()

	<-ctx.Done()

	server.SetKeepAlivesEnabled(false)
	return server.Close()
}
