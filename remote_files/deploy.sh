#!/bin/bash
set -e

echo "Deploying version ${VERSION}..."

cd /minitwit

#Check if we already are running this version (unless we use --force)
CURRENT_VERSION=$(grep "^VERSION=" .env 2>/dev/null | cut -d'=' -f2)
if [ "$CURRENT_VERSION" = "$VERSION" ] && [ "$1" != "--force" ]; then
  echo "Already running version ${VERSION}, skipping deploy. Use --force to redeploy."
  exit 0
fi

# Pull new image before we stop anything
docker-compose pull java-backend svelte-frontend
docker-compose stop java-backend svelte-frontend nginx
docker-compose up -d java-backend svelte-frontend nginx

# Update VERSION in .env if it exists, otherwise append it
if grep -q "^VERSION=" .env 2>/dev/null; then
  sed -i "s/^VERSION=.*/VERSION=${VERSION}/" .env
else
  echo "VERSION=${VERSION}" >> .env
fi

echo "Deploy complete! Running version ${VERSION}"