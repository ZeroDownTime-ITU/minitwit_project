terraform {
  required_providers {
    digitalocean = {  # declare a dependency on the DigitalOcean provider
      source  = "digitalocean/digitalocean"  # registry path to the official DigitalOcean provider
      version = "~> 2.0"  # allow any 2.x patch/minor version but not a major version bump
    }
  }
}

provider "digitalocean" {
  token = var.do_token  # authenticate with the DigitalOcean API using the token variable
}

# Create three identical swarm nodes that form the Docker Swarm cluster
resource "digitalocean_droplet" "swarm_node" {
  count    = 3  # create three droplets, accessible as swarm_node[0], swarm_node[1], swarm_node[2]
  name     = "swarm-node-${count.index + 1}"  # name each droplet swarm-node-1, swarm-node-2, swarm-node-3
  image    = "ubuntu-24-04-x64"  # base OS image: Ubuntu 24.04 LTS (64-bit)
  size     = "s-1vcpu-2gb"  # smallest droplet size: 1 vCPU, 2 GB RAM
  region   = "fra1"  # deploy in the Frankfurt 1 datacenter
  ssh_keys = [var.ssh_key_fingerprint]  # allow SSH access using this pre-registered key fingerprint
  tags   = ["swarm"]  # tag so the Ansible dynamic inventory places this droplet in the 'swarm' group
}

# Create a single dedicated droplet for the PostgreSQL database
resource "digitalocean_droplet" "db" {
  name     = "minitwit-db"  # human-readable name for the database droplet
  image    = "ubuntu-24-04-x64"  # base OS image: Ubuntu 24.04 LTS (64-bit)
  size     = "s-1vcpu-2gb"  # smallest droplet size: 1 vCPU, 2 GB RAM
  region   = "fra1"  # deploy in the Frankfurt 1 datacenter (same region as swarm nodes for low-latency private networking)
  ssh_keys = [var.ssh_key_fingerprint]  # allow SSH access using this pre-registered key fingerprint
  tags   = ["db"]  # tag so the Ansible dynamic inventory places this droplet in the 'db' group
}

# Create a single dedicated droplet for the monitoring stack (Prometheus, Grafana, Loki)
resource "digitalocean_droplet" "monitoring" {
  name     = "minitwit-monitoring"  # human-readable name for the monitoring droplet
  image    = "ubuntu-24-04-x64"  # base OS image: Ubuntu 24.04 LTS (64-bit)
  size     = "s-1vcpu-2gb"  # smallest droplet size: 1 vCPU, 2 GB RAM
  region   = "fra1"  # deploy in the Frankfurt 1 datacenter (same region for private network access)
  ssh_keys = [var.ssh_key_fingerprint]  # allow SSH access using this pre-registered key fingerprint
  tags   = ["monitoring"]  # tag so the Ansible dynamic inventory places this droplet in the 'monitoring' group
}

# Look up the pre-existing persistent block volume for the database (not created here, must already exist)
data "digitalocean_volume" "volume-db" {
  name   = "volume-db"  # the name of the existing volume in DigitalOcean
  region = "fra1"  # must specify the region to locate the volume
}

# Look up the pre-existing persistent block volume for monitoring data (not created here, must already exist)
data "digitalocean_volume" "volume-monitoring" {
  name   = "volume-monitoring"  # the name of the existing volume in DigitalOcean
  region = "fra1"  # must specify the region to locate the volume
}

# Attach the database block volume to the db droplet so Postgres data persists beyond droplet replacements
resource "digitalocean_volume_attachment" "db_volume_attachment" {
  droplet_id = digitalocean_droplet.db.id  # reference the db droplet created above
  volume_id  = data.digitalocean_volume.volume-db.id  # reference the existing volume looked up above
}

# Attach the monitoring block volume to the monitoring droplet so metrics and logs persist beyond droplet replacements
resource "digitalocean_volume_attachment" "monitoring_volume_attachment" {
  droplet_id = digitalocean_droplet.monitoring.id  # reference the monitoring droplet created above
  volume_id  = data.digitalocean_volume.volume-monitoring.id  # reference the existing volume looked up above
}
