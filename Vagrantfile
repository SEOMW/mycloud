# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
    config.vm.box = "ubuntu/focal64"
    config.vm.define "ctrl" do |ctrl|
        ctrl.vm.hostname = "kube-controller"
        ctrl.vm.provider "virtualbox" do |vb|
            vb.name = "kube-controller"
            vb.cpus = 4
            vb.memory = 8192
        end
        ctrl.vm.network "private_network", ip: "192.168.10.11"
        # ctrl.vm.provision "shell", inline: <<-SCRIPT
        #     sudo apt update -y && sudo apt -y full-upgrade
        #     sudo apt install -y gnupg2 software-properties-common apt-transport-https ca-certificates
        #     curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
        #     sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
        #     curl -fsSL https://dl.k8s.io/apt/doc/apt-key.gpg | sudo apt-key add -
        #     echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list
        #     sudo apt update -y
        #     sudo apt install -y containerd.io
        #     sudo mkdir -p /etc/containerd
        #     sudo containerd config default | sudo tee /etc/containerd/config.toml > /dev/null
        #     sudo apt -y install kubelet kubeadm kubectl
        #     sudo apt-mark hold kubelet kubeadm kubectl
        #     sudo sed -i '/swap/s/^/#/' /etc/fstab
        #     sudo swapoff -a
        #     sudo mount -a
        #     sudo su - -c "echo 'net.bridge.bridge-nf-call-ip6tables = 1' >> /etc/sysctl.d/kubernetes.conf"
        #     sudo su - -c "echo 'net.bridge.bridge-nf-call-iptables = 1' >> /etc/sysctl.d/kubernetes.conf"
        #     sudo su - -c "echo 'net.ipv4.ip_forward = 1' >> /etc/sysctl.d/kubernetes.conf"
        #     sudo su - -c "echo 'overlay' >> /etc/modules-load.d/containerd.conf"
        #     sudo su - -c "echo 'br_netfilter' >> /etc/modules-load.d/containerd.conf"
        #     sudo modprobe overlay
        #     sudo modprobe br_netfilter
        #     sudo sysctl --system
        #     sudo systemctl restart containerd
        #     sudo systemctl enable containerd
        #     sudo systemctl restart kubelet
        #     sudo systemctl enable kubelet
        # SCRIPT
    end

    config.vm.define "wn1" do |wn1|
        wn1.vm.hostname = "kube-worker-node1"
        wn1.vm.provider "virtualbox" do |vb|
            vb.name = "kube-worker-node1"
            vb.cpus = 4
            vb.memory = 4096
        end
        wn1.vm.network "private_network", ip: "192.168.10.12"
    end

    config.vm.define "wn2" do |wn2|
        wn2.vm.hostname = "kube-worker-node2"
        wn2.vm.provider "virtualbox" do |vb|
            vb.name = "kube-worker-node2"
            vb.cpus = 4
            vb.memory = 4096
            vb.customize ["modifyvm", :id, "--nested-hw-virt", "on"]
        end
        wn2.vm.network "private_network", ip: "192.168.10.13"
    end

    # nginx loadbalance 기능 사용해서 NodePort 로 공개된 모든 노드에 서비스가
    # 되도록 해보기.
    # public network 로 옆 전우/친구/형/누나/동생들과 서로 웹 페이지 나오는지 확인해보기.
    # 할 수 있으면 이전에 사용한 static web template 도 시도해보세요.
    config.vm.define "dock" do |dock|
        dock.vm.hostname = "docker-registry"
        dock.vm.provider "virtualbox" do |vb|
            vb.name = "docker-registry"
            vb.cpus = 2
            vb.memory = 4096
        end
        dock.vm.network "private_network", ip: "192.168.10.14"
        dock.vm.network "public_network"
    end
end