output "swarm_node_private_ips" {
  value = digitalocean_droplet.swarm_node[*].ipv4_address_private
}

output "db_private_ip" {
  value = digitalocean_droplet.db.ipv4_address_private
}

output "monitoring_private_ip" {
  value = digitalocean_droplet.monitoring.ipv4_address_private
}