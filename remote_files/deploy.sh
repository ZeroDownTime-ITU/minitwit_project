#!/bin/bash
set -e

echo "Deploying version ${VERSION}..."

cd /minitwit

echo "VERSION=${VERSION}" > .env

docker-compose pull
docker-compose up -d --no-deps java-backend svelte-frontend # Keep DB running
docker-compose exec -T nginx nginx -s reload # Refresh Nginx's IP table

echo "Deploy complete! Running version ${VERSION}"