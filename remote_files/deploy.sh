#!/bin/bash

echo "Deploying latest images..."

cd /minitwit

docker-compose down --remove-orphans
docker-compose pull
docker-compose up -d

echo "Deploy complete!"