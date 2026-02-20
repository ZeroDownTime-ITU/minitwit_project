# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.define "minitwit" do |minitwit|
    minitwit.vm.box = "digital_ocean"
    minitwit.vm.provider :digital_ocean do |provider|
      provider.token = ENV["DIGITAL_OCEAN_KEY"]
      provider.ssh_key_name = 'Magnus_Mac'

      provider.image = "ubuntu-22-04-x64"
      provider.region = "fra1"
      provider.size = "s-1vcpu-1gb"
    end
  # CHANGE THIS LINE IF THE SSH FOLDER IS DIFFERENT. 
  config.ssh.private_key_path = '~/.ssh/id_ed25519'
  config.vm.synced_folder ".", "/vagrant", type: "rsync"

  end
  config.vm.provision "shell", inline: <<-SHELL
  echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIGJJ6uOxzj07JWt/8aqbwkH3qMIu615PfU0ZvZCHkovi magnusbergstedt@Mac" >> /root/.ssh/authorized_keys
  echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIE6lS3hLYIvcWHKP3zsh2K6SZBOQJWNwBQspdptT8/Fq mono@monolith" >> /root/.ssh/authorized_keys
  # CHANGE THIS TO THE NEW SSH
  echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIOnNkEKfn3FGWFV4W8X39RzFMtF9bYUATHtHGpINPIH3 luismilanengel@Luis-MacBook-Air.local" >> /root/.ssh/authorized_keys
  echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIFmDcJ55KcT1ZRW5200BqocDh8kO3jq6xOzXdEZNCl9V conta@MSI" >> /root/.ssh/authorized_keys
  echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAID2cG2XUbHvk8i7Hmv4ix2KTiM5GAKEbJltl3pvNmCpE jbul@itu.dk" >> /root/.ssh/authorized_keys
  echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAACAQDZmlyxqe1cHR+mt1bvPTK/qy0xhaW/AuCjeq9SvfOi
  fshImR/9JhtiT8mo+N7hmTA+xrFxe44NGqKyEwBDCmSaAH8nQRWe1KLlvWGjnfB5m4aNMywhQYmBw04uCwjzTQ
  2EfSJzZxqW2+Daca3GkmxvlmMvrC9+M+Dn+XgF2DgJ3SrJyLW6K8m9MZ8PwKDooyEcNktkv+fDpDYDpyV4X7eW
  WRCmVZG1urw0phLGnhAzP3Z5qvPxr0ETuA2ll499eiOmCoa1EulowjLKhUi/5EFckEBVfJd27fZFZwcxxzNuTL
  fD0WO0IzLm8BdZRETnMGHxSRl5nHh81G+4e8fV6vhqhp/TObECH7PV5U3Gv3KdGEY1a5EhfN62a//CU/7mxc1s
  vxs0GwJc0VOf0h5r6PWoyfC0qZNOlSA3Kg7FdctXJku2ML62+798Qz9NjMeC0LpyBJpryaa985rplRWTX2rmx5
  AHHA+PF3UYJav3Xup9qj6iKX8fUu7OUcNNUqUbQhC5y/X/ulIYBFI9NppJnA8vCIdRyR3gpAsQdDhXC7kaJ69W
  F5XGF9QlqZa6MixKFutVcF52KKdhQgU2E4eupzmNcZC5mv2/EvDtMx86ILtLviX+G/zTKJ8VGXSNsNpHogh49O
  cfrgWc4R01m4Mgy9v/Vu7afxhkOqh+sGFaAhXuQw==                                              mathiassogaard@MacBook-Air-tilhrende-Mathias.local" >> /root/.ssh/authorized_keys

  SHELL
end