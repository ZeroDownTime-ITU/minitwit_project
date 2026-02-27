# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = 'digital_ocean'
  config.ssh.private_key_path = '~/.ssh/id_ed25519'

  config.vm.define "minitwit" do |minitwit|
    minitwit.vm.synced_folder "./remote_files", "/minitwit", type: "rsync"

    minitwit.vm.provider :digital_ocean do |provider, override|
      # Force rsync and disable other types to stop the SMB/NFS prompts
      override.nfs.functional = false
      override.vm.allowed_synced_folder_types = :rsync

      provider.monitoring = true
      provider.token = ENV["DIGITAL_OCEAN_KEY"]
      provider.ssh_key_name = ENV["SSH_KEY_NAME"] 
      provider.image = "ubuntu-22-04-x64"
      provider.region = "fra1"
      provider.size = "s-1vcpu-1gb"
      provider.setup = false 
    end
    
    minitwit.vm.provision "shell", path: "provision.sh", env: {
      "DIGITAL_OCEAN_KEY" => ENV["DIGITAL_OCEAN_KEY"]
    }
  end
end