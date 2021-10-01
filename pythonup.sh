#!/bin/bash

set -o errexit

if [ ! -e "${PYPORT}" ]; then
  PYPORT=8080
fi

docker build -f Dockerfile.python -t pybackend .
echo "starting python server with port ${PYPORT}"
docker run -p ${PYPORT}:8080 -d pybackend
