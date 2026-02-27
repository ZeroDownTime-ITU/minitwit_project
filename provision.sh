#!/bin/bash

echo "Starting provision..."

# 1. Assign reserved IP
DROPLET_ID=$(curl -s http://169.254.169.254/metadata/v1/id)

curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DIGITAL_OCEAN_TOKEN" \
  -d "{\"type\":\"assign\",\"droplet_id\":$DROPLET_ID}" \
  "https://api.digitalocean.com/v2/reserved_ips/46.101.70.51/actions"

# Mount DO db volume to our droplet 
mkdir -p /mnt/volume_fra1_01; 
mount -o discard,defaults /dev/disk/by-id/scsi-0DO_Volume_volume-fra1-01 /mnt/volume_fra1_01; 
echo /dev/disk/by-id/scsi-0DO_Volume_volume-fra1-01 /mnt/volume_fra1_01 ext4 defaults,nofail,discard 0 0 | 
sudo tee -a /etc/fstab


# Wait for cloud-init to finish holding apt
echo "Waiting for apt lock to be released..."
while fuser /var/lib/dpkg/lock-frontend >/dev/null 2>&1; do
    sleep 5
done

# 2. Update and Install Dependencies
sudo apt-get update

sudo apt-get install -y docker.io docker-compose nginx certbot python3-certbot-nginx

# 3. Setup Docker permissions
sudo usermod -aG docker root

# 4. Configure Nginx (using the correct filename from your ls output)
if [ -f "/minitwit/nginx.conf" ]; then
    sudo cp /minitwit/nginx.conf /etc/nginx/sites-available/minitwit
    sudo ln -sf /etc/nginx/sites-available/minitwit /etc/nginx/sites-enabled/default
    sudo systemctl restart nginx
    echo "Nginx configured."
else
    echo "Error: /minitwit/nginx.conf not found!"
fi

# 5. Start the App via Docker Compose
cd /minitwit
if [ -f "docker-compose.yml" ]; then
    sudo docker-compose up -d
    echo "Containers started."
else
    echo "Error: docker-compose.yml not found in /minitwit!"
fi

# 5. Setup SSL Cert (not needed if no .app domain)

echo "Setting up SSL certificate..."

certbot --nginx -d zerodt.live -d www.zerodt.live \
  --non-interactive \
  --agree-tos \
  --email your@email.com \
  --redirect

echo "Provisioning complete!"

echo "cd /minitwit" >> /root/.bashrc

chmod +x /minitwit/deploy.sh

# 6. Add ssh-keys 
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE6lS3hLYIvcWHKP3zsh2K6SZBOQJWNwBQspdptT8/Fq mono@monolith" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIPE68oskLFw2fC14xIWiZSIA0veODVn42LeswDDWI0R1 luismilanengel@Luis-MacBook-Air.local" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFmDcJ55KcT1ZRW5200BqocDh8kO3jq6xOzXdEZNCl9V conta@MSI" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID2cG2XUbHvk8i7Hmv4ix2KTiM5GAKEbJltl3pvNmCpE jbul@itu.dk" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDMW7+/L+y8iZqzIMKEDcu+WY1Jq5AtIW7a//OhTknoa mathias@minitwit" >> /root/.ssh/authorized_keys
