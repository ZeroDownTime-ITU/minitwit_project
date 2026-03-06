# -*- mode: ruby -*-
# vi: set ft=ruby :

#USE VAGRANT UP <NAME OF VM.DEFINE ("test" in this case)> TO START THE VM
#USE VAGRANT DESTROY <NAME OF VM.DEFINE ("test" in this case)> TO DESTROY THE VM

Vagrant.configure("2") do |config|
  config.vm.box = 'digital_ocean'
  config.ssh.private_key_path = '~/.ssh/id_ed25519'

  config.vm.define "test" do |minitwit|
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
      provider.volumes = [
        "cbaf4805-1639-11f1-a59c-0a58ac12ea84" # Change this to your own volume ID, which can be found by running 
          # curl -X GET "https://api.digitalocean.com/v2/volumes" \ -H "Authorization: Bearer $DIGITAL_OCEAN_KEY"
      ]
    end
    
    minitwit.vm.provision "shell", path: "provision.sh", env: {
      "DIGITAL_OCEAN_KEY" => ENV["DIGITAL_OCEAN_KEY"],
      "RESERVED_IP"       => "129.212.140.147",
    }
  end
end
