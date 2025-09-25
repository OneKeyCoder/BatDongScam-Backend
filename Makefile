.PHONY: build up down

# Build the Docker containers
build:
	docker-compose -f docker/docker-compose.yaml --env-file .env build

# Start the Docker containers
up:
	docker-compose -f docker/docker-compose.yaml --env-file .env up -d

# Stop and remove the Docker containers
down:
	docker-compose -f docker/docker-compose.yaml --env-file .env down

# Stop, Build and Start the Docker containers
rebuild: down build up
