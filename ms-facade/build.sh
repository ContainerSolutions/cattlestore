#! /usr/bin/env bash

# clean
MVN_CMD="mvn -B package"
MVN_CONTAINER_VERSION="maven:3.3-jdk-8"

docker run --rm \
	-v "$PWD":/usr/src/project \
	-w /usr/src/project \
	-v "$HOME"/.m2:/root/.m2 \
	$MVN_CONTAINER_VERSION \
	$MVN_CMD && \
\
docker build -t facade .
