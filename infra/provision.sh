#!/bin/bash
set -e

cd infra/terraform
tofu apply -auto-approve

cd ../ansible
ansible-playbook -i inventory.digitalocean.yml site.yml