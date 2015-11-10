all: build-container

build-app:
	@go fmt *.go
	@go build -o dist/cattlestore main.go

build-container:
	@GOOS=linux GOARCH=amd64 CGO_ENABLED=0 go build -o dist/cattlestore main.go
	@docker build -t containersolutions/cattlestore .

run:
	@go run main.go
