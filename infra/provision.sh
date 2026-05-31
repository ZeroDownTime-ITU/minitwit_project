#!/bin/bash
set -e

cd terraform
tofu apply -auto-approve

echo "Waiting for droplets to boot..."
cd ../ansible
ansible -i inventory.digitalocean.yml all -m wait_for_connection --timeout=120

ansible-playbook -i inventory.digitalocean.yml site.yml