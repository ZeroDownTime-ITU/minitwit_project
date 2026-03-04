#!/bin/bash

echo "Starting provision..."

# 1. INSTALL DOCTL TO SET RESERVED IP
curl -sL https://github.com/digitalocean/doctl/releases/download/v1.104.0/doctl-1.104.0-linux-amd64.tar.gz | tar -xzv
mv doctl /usr/local/bin
export DO_TOKEN=$DIGITAL_OCEAN_KEY 
DROPLET_ID=$(curl -s http://169.254.169.254/metadata/v1/id)
# Use --access-token flag to keep it non-interactive
doctl compute reserved-ip-action assign $RESERVED_IP $DROPLET_ID --access-token $DO_TOKEN || echo "IP already assigned"

# 2. MOUNTING
mkdir -p /mnt/volume_fra1_01
mount -o discard,defaults,noatime /dev/disk/by-id/scsi-0DO_Volume_volume-fra1-01 /mnt/volume_fra1_01
echo '/dev/disk/by-id/scsi-0DO_Volume_volume-fra1-01 /mnt/volume_fra1_01 ext4 defaults,nofail,discard 0 0' | tee -a /etc/fstab

# 3. ADD SSH KEYS
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE6lS3hLYIvcWHKP3zsh2K6SZBOQJWNwBQspdptT8/Fq mono@monolith" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIPE68oskLFw2fC14xIWiZSIA0veODVn42LeswDDWI0R1 luismilanengel@Luis-MacBook-Air.local" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFmDcJ55KcT1ZRW5200BqocDh8kO3jq6xOzXdEZNCl9V conta@MSI" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID2cG2XUbHvk8i7Hmv4ix2KTiM5GAKEbJltl3pvNmCpE jbul@itu.dk" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDMW7+/L+y8iZqzIMKEDcu+WY1Jq5AtIW7a//OhTknoa mathias@minitwit" >> /root/.ssh/authorized_keys

echo "cd /minitwit" >> /root/.bashrc
chmod +x /minitwit/deploy.sh

sudo apt-get update
# The following address an issue in DO's Ubuntu images, which still contain a lock file
sudo killall apt apt-get
sudo rm /var/lib/dpkg/lock-frontend
echo "Waiting for apt lock..."
while fuser /var/lib/dpkg/lock-frontend >/dev/null 2>&1 || \
      fuser /var/lib/apt/lists/lock >/dev/null 2>&1; do
    sleep 5
done

# 3. INSTALL DOCKER & COMPOSE
sudo apt-get install -y docker.io docker-compose
sudo usermod -aG docker root

# THIS KEEPS THE FILE SECURED
sudo chmod 600 /minitwit/.env

# 4. SET UP NGINX WITH HTTP CONFIG
cp /minitwit/nginx-http.conf /minitwit/nginx.conf

# 5. START APPS THROUGH DOCKER COMPOSE
cd /minitwit
if [ -f "docker-compose.yml" ]; then
    sudo docker-compose up -d
    echo "Containers started."
else
    echo "Error: docker-compose.yml not found in /minitwit!"
fi

# 6. GET CERTS IF NEEDED
# Check if certs already exist on the volume
if [ ! -f "/mnt/volume_fra1_01/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
    echo "No certs found. Requesting initial certs..."
    docker-compose run --rm -T --entrypoint certbot certbot certonly \
        --webroot -w /var/www/certbot \
        -d $DOMAIN -d www.$DOMAIN \
        --email your@email.com --agree-tos --no-eff-email
    echo "Certs issued successfully."
else
    echo "Certs already exist, skipping certbot."
fi

# 7. SWAP NGINX TO SSL CONFIG (HTTPS&HTTP2)
cp /minitwit/nginx-ssl.conf /minitwit/nginx.conf
docker-compose exec -T nginx nginx -s reload

echo "Provisioning complete!"