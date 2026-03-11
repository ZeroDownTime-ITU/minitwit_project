#!/bin/bash
set -e

echo "Deploying version ${VERSION}..."

cd /minitwit

# Update VERSION in .env if it exists, otherwise append it
if grep -q "^VERSION=" .env 2>/dev/null; then
  sed -i "s/^VERSION=.*/VERSION=${VERSION}/" .env
else
  echo "VERSION=${VERSION}" >> .env
fi

# Pull new image before we stop anything
docker compose pull java-backend svelte-frontend prometheus
docker compose down java-backend svelte-frontend nginx prometheus
docker compose up -d java-backend svelte-frontend nginx prometheus

# start prometheus node exporter for server hardware specs
bash /minitwit/start-node-exporter.sh

echo "Deploy complete! Running version ${VERSION}"