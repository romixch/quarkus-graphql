#!/bin/sh

docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus.graphql .

docker run -i --rm -p 8080:8080 quarkus/quarkus.graphql