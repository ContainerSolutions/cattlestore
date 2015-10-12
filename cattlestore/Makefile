all: build-app build-container

build-app:
	@go build -o dist/cattlestore rest.go

build-container:
	@docker build -t containersolutions/cattlestore .
