all: build-app build-container

build-app:
	@go fmt *.go
	@go build -o dist/cattlestore main.go

build-container:
	@docker build -t containersolutions/cattlestore .

run:
	@go run main.go
