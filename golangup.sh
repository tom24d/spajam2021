#!/bin/bash

set -o errexit

if [ ! -e "${GOPORT}" ]; then
  GOPORT=8080
fi

docker build -f Dockerfile.golang -t gobackend .
echo "starting golang server with port ${GOPORT}"
docker run -p ${GOPORT}:8080 -d gobackend
