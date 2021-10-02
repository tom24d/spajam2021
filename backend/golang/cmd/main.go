package main

import (
	"context"
	"log"
	"os"
	"os/signal"

	"github.com/tom24d/spajam2021/backend/golang/pkg"
	"github.com/tom24d/spajam2021/backend/golang/pkg/apis"
)

func main() {
	log.Println("initializing...")

	ctx, stop := signal.NotifyContext(context.Background(), os.Interrupt)
	defer stop()

	c := pkg.New(ctx, apis.NewHandler)

	if err := c.Start(ctx); err != nil {
		log.Fatalf("error main: %v", err)
	}
}
