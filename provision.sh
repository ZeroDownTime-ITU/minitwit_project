#!/bin/bash

echo "Starting provision..."

# 1. INSTALL DOCTL TO SET RESERVED IP
if ! command -v doctl &> /dev/null; then
    curl -sL https://github.com/digitalocean/doctl/releases/download/v1.104.0/doctl-1.104.0-linux-amd64.tar.gz | tar -xzv
    mv doctl /usr/local/bin
fi
export DO_TOKEN=$DIGITAL_OCEAN_KEY 
DROPLET_ID=$(curl -s http://169.254.169.254/metadata/v1/id)
doctl compute reserved-ip-action assign $RESERVED_IP $DROPLET_ID --access-token $DO_TOKEN || echo "IP already assigned"

# 2. MOUNTING
mkdir -p /mnt/volume_fra1_${VOLUME_NUMBER}
mountpoint -q /mnt/volume_fra1_01 || mount -o discard,defaults,noatime /dev/disk/by-id/scsi-0DO_Volume_volume-fra1-${VOLUME_NUMBER} /mnt/volume_fra1_${VOLUME_NUMBER}
grep -q "volume_fra1_01" /etc/fstab || echo "/dev/disk/by-id/scsi-0DO_Volume_volume-fra1-${VOLUME_NUMBER} /mnt/volume_fra1_${VOLUME_NUMBER} ext4 defaults,nofail,discard 0 0" | tee -a /etc/fstab

    # 2.1 CREATE DIR FOR PROMETHEUS AND GRAFANA, GIVE WRITE PERMISSION
    mkdir -p /mnt/volume_fra1_${VOLUME_NUMBER}/prometheus_data
    mkdir -p /mnt/volume_fra1_${VOLUME_NUMBER}/grafana_data
    chown -R 65534:65534 /mnt/volume_fra1_${VOLUME_NUMBER}/prometheus_data
    chown -R 472:472 /mnt/volume_fra1_${VOLUME_NUMBER}/grafana_data

    # 2.1 SET .ENV VARIABLES FOR DOCKER-COMPOSE.YML
    echo "" >> /minitwit/.env
    echo "DOMAIN=${DOMAIN}" >> /minitwit/.env
    echo "VOLUME_NUMBER=${VOLUME_NUMBER}" >> /minitwit/.env

# 3. ADD SSH KEYS
grep -q "mono@monolith" /root/.ssh/authorized_keys || echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE6lS3hLYIvcWHKP3zsh2K6SZBOQJWNwBQspdptT8/Fq mono@monolith" >> /root/.ssh/authorized_keys
grep -q "luismilanengel@Luis-MacBook-Air.local" /root/.ssh/authorized_keys || echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIPE68oskLFw2fC14xIWiZSIA0veODVn42LeswDDWI0R1 luismilanengel@Luis-MacBook-Air.local" >> /root/.ssh/authorized_keys
grep -q "conta@MSI" /root/.ssh/authorized_keys || echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFmDcJ55KcT1ZRW5200BqocDh8kO3jq6xOzXdEZNCl9V conta@MSI" >> /root/.ssh/authorized_keys
grep -q "jbul@itu.dk" /root/.ssh/authorized_keys || echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID2cG2XUbHvk8i7Hmv4ix2KTiM5GAKEbJltl3pvNmCpE jbul@itu.dk" >> /root/.ssh/authorized_keys
grep -q "mathias@minitwit" /root/.ssh/authorized_keys || echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDMW7+/L+y8iZqzIMKEDcu+WY1Jq5AtIW7a//OhTknoa mathias@minitwit" >> /root/.ssh/authorized_keys

grep -q "cd /minitwit" /root/.bashrc || echo "cd /minitwit" >> /root/.bashrc
chmod +x /minitwit/deploy.sh

# The following address an issue in DO's Ubuntu images, which still contain a lock file
sudo killall apt apt-get 2>/dev/null || true
sudo rm -f /var/lib/dpkg/lock-frontend
sudo apt-get update

# 4. INSTALL DOCKER & COMPOSE
if ! command -v docker &> /dev/null; then
    echo "Waiting for apt lock before Docker install..."
    while fuser /var/lib/dpkg/lock-frontend >/dev/null 2>&1 || \
        fuser /var/lib/apt/lists/lock >/dev/null 2>&1; do
        sleep 5
    done
    DEBIAN_FRONTEND=noninteractive curl -fsSL https://get.docker.com | sh
fi
sudo usermod -aG docker root
sudo systemctl start docker
sudo systemctl enable docker

    # 4.1 EXTRA CHECK TO SEE IF THAT ENV FILE IS THERE (I PRAY)
    if [ ! -f /minitwit/.env ]; then 
        echo "Missing environment file. Copy it brother just as written in the ".env.template" file. Destroy the droplet and try again"
        exit 1
    fi

    # 4.2 THIS KEEPS THE FILE SECURED
    sudo chmod 600 /minitwit/.env

# 5. SET UP NGINX WITH HTTP CONFIG
if [ ! -f /minitwit/nginx.conf ]; then
    cp /minitwit/nginx-http.conf /minitwit/nginx.conf
fi

# 6. START APPS THROUGH DOCKER COMPOSE
cd /minitwit
if [ -f "docker-compose.yml" ]; then
    sudo docker compose up -d
    echo "Containers started."
else
    echo "Error: docker-compose.yml not found in /minitwit!"
fi

# 7. GET CERTS IF NEEDED
# Check if certs already exist on the volume
if [ ! -f "/mnt/volume_fra1_${VOLUME_NUMBER}/letsencrypt/live/${DOMAIN}/fullchain.pem" ]; then
    echo "No certs found. Requesting initial certs..."
    docker compose run --rm -T --entrypoint certbot certbot certonly \
        --webroot -w /var/www/certbot \
        -d ${DOMAIN} -d www.${DOMAIN} \
        --email your@email.com --agree-tos --no-eff-email
    echo "Certs issued successfully."
else
    echo "Certs already exist, skipping certbot."
fi

# 8. SWAP NGINX TO SSL CONFIG (HTTPS&HTTP2) AND SET DOMAIN VARIABLE
if [ -f "/mnt/volume_fra1_${VOLUME_NUMBER}/letsencrypt/live/$DOMAIN/fullchain.pem" ]; then
    sed "s/\${DOMAIN}/${DOMAIN}/g" /minitwit/nginx-ssl.conf > /minitwit/nginx.conf
    docker compose exec -T nginx nginx -s reload
fi

# 9. INSTALL NODE EXPORTER

wget -qO- https://github.com/prometheus/node_exporter/releases/download/v1.8.1/node_exporter-1.8.1.linux-amd64.tar.gz | tar xzf - -C /usr/local/bin --strip-components=1 node_exporter-1.8.1.linux-amd64/node_exporter

cat > /etc/systemd/system/node_exporter.service <<EOF
[Unit]
Description=Node Exporter
After=network.target

[Service]
ExecStart=/usr/local/bin/node_exporter
Restart=on-failure

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable --now node_exporter

# provision complete. 
echo "Provisioning complete!"
