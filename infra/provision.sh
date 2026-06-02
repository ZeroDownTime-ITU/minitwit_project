#!/bin/bash
set -e  # exit immediately if any command fails

cd terraform  # enter the terraform directory to run infrastructure commands
tofu apply -auto-approve  # create/update all cloud resources without prompting for confirmation

echo "Waiting for droplets to boot..."  # print a status message so the operator knows why there's a pause
cd ../ansible  # move to the ansible directory for configuration management
ansible -i inventory.digitalocean.yml all -m wait_for_connection --timeout=120  # poll every host until SSH is available, timeout after 120 seconds

ansible-playbook -i inventory.digitalocean.yml site.yml  # run the main playbook to configure and deploy everything on all servers
