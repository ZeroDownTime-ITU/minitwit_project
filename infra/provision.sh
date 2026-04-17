#!/bin/bash
set -e

cd terraform
tofu apply -auto-approve

echo "Waiting for droplets to boot..."
sleep 10

cd ../ansible
ansible-playbook -i inventory.digitalocean.yml site.yml