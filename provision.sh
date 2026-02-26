#!/bin/bash

echo "Starting provision..."

# 1. Assign reserved IP
DROPLET_ID=$(curl -s http://169.254.169.254/metadata/v1/id)

curl -X POST \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $DIGITAL_OCEAN_TOKEN" \
  -d "{\"type\":\"assign\",\"droplet_id\":$DROPLET_ID}" \
  "https://api.digitalocean.com/v2/reserved_ips/46.101.70.51/actions"

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