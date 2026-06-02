variable "do_token" {}  # DigitalOcean API token used to authenticate all API calls; passed via TF_VAR_do_token or a .tfvars file
variable "ssh_key_fingerprint" {}  # fingerprint of an SSH key already registered in your DigitalOcean account; added to all droplets to allow root SSH access
