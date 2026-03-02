#!/bin/bash

echo "Starting provision..."

# Install doctl
curl -sL https://github.com/digitalocean/doctl/releases/download/v1.104.0/doctl-1.104.0-linux-amd64.tar.gz | tar -xzv
mv doctl /usr/local/bin

export DO_TOKEN=$DIGITAL_OCEAN_KEY 

DROPLET_ID=$(curl -s http://169.254.169.254/metadata/v1/id)
# Use --access-token flag to keep it non-interactive
doctl compute reserved-ip-action assign 46.101.70.51 $DROPLET_ID --access-token $DO_TOKEN || echo "IP already assigned"

sudo apt-get update

# The following address an issue in DO's Ubuntu images, which still contain a lock file
sudo killall apt apt-get
sudo rm /var/lib/dpkg/lock-frontend

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

# Force Nginx to use HTTP/2: this finds the 'listen 443 ssl' line Certbot just made and adds 'http2' to it
sudo sed -i 's/listen 443 ssl;/listen 443 ssl http2;/g' /etc/nginx/sites-available/minitwit
sudo systemctl reload nginx

echo "Provisioning complete!"

echo "cd /minitwit" >> /root/.bashrc

chmod +x /minitwit/deploy.sh

# 6. Add ssh-keys 
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE6lS3hLYIvcWHKP3zsh2K6SZBOQJWNwBQspdptT8/Fq mono@monolith" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIPE68oskLFw2fC14xIWiZSIA0veODVn42LeswDDWI0R1 luismilanengel@Luis-MacBook-Air.local" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFmDcJ55KcT1ZRW5200BqocDh8kO3jq6xOzXdEZNCl9V conta@MSI" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID2cG2XUbHvk8i7Hmv4ix2KTiM5GAKEbJltl3pvNmCpE jbul@itu.dk" >> /root/.ssh/authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIDMW7+/L+y8iZqzIMKEDcu+WY1Jq5AtIW7a//OhTknoa mathias@minitwit" >> /root/.ssh/authorized_keys
