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
DEBIAN_FRONTEND=noninteractive curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker root
sudo systemctl start docker
sudo systemctl enable docker

# EXTRA CHECK TO SEE IF THAT ENV FILE IS THERE (I PRAY)
if [ ! -f /minitwit/.env ]; then 
    echo "Missing environment file. Copy it brother just as written in the ".env.template" file"
    exit 1
fi

# THIS KEEPS THE FILE SECURED
sudo chmod 600 /minitwit/.env

# 4. SET UP NGINX WITH HTTP CONFIG
cp /minitwit/nginx-http.conf /minitwit/nginx.conf

# 5. START APPS THROUGH DOCKER COMPOSE
cd /minitwit
if [ -f "docker-compose.yml" ]; then
    sudo docker compose up -d
    echo "Containers started."
else
    echo "Error: docker-compose.yml not found in /minitwit!"
fi

echo "Provisioning complete!"