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
docker-compose pull java-backend svelte-frontend
docker-compose stop java-backend svelte-frontend nginx
docker-compose up -d java-backend svelte-frontend nginx

echo "Deploy complete! Running version ${VERSION}"
