#!/bin/bash

echo "Updating packages..."

# Install dependencies
apt-get update -y
apt-get install -y ca-certificates curl gnupg git
# Add Docker's official GPG key and repo
install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(. /etc/os-release && echo $VERSION_CODENAME) stable" | tee /etc/apt/sources.list.d/docker.list
# Install Docker + Compose plugin
apt-get update
apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

echo "Docker installation complete!"

echo "Starting Docker..."

systemctl enable --now docker
usermod -aG docker vagrant # allow us to execute docker commands, without using sudo

echo "Cloning and starting app..."
git clone https://github.com/ZeroDownTime-ITU/minitwit_project.git vagrant/minitwit_project

cd vagrant/minitwit_project/minitwit-java
docker compose up -d

echo "Provisioning finished! App is starting in the background."