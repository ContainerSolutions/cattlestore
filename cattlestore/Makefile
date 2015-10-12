all: build-app build-container

build-app:
	@go build -o dist/cattlestore main.go

build-container:
	@docker build -t containersolutions/cattlestore .

run:
	@go run main.go
