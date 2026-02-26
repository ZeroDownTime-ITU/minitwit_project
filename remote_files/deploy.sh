#!/bin/bash
set -e

echo "Deploying version ${VERSION}..."

cd /minitwit

echo "VERSION=${VERSION}" > .env

docker-compose pull
docker-compose down --remove-orphans
docker-compose up -d

echo "Deploy complete! Running version ${VERSION}"