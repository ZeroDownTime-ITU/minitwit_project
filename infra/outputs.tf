output "swarm_node_ips" {
  value = digitalocean_droplet.swarm_node[*].ipv4_address
}

output "db_ip" {
  value = digitalocean_droplet.db.ipv4_address
}

output "monitoring_ip" {
  value = digitalocean_droplet.monitoring.ipv4_address
}