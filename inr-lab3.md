# LXD and IPv6

## Q1. Create a new Xen image as per Lab 1 running Ubuntu 18 04 Bionic with 2G of RAM We will configure this image to run pogo and we will use this same image for future INR lab exercises

```
sudo -i xen-create-image --force --memory=4096mb --size=99gb --vcpus=2 --dist=bionic --bridge=xenbr0 --ip=145.100.111.2 --gateway=145.100.111.1 --hostname=g1.nevers.prac.os3.nl --lvm=VolumeGroupXen --mirror=http://nl.archive.ubuntu.com/ubuntu/
```

## Q2. Once the machine is up and running install the screen package and use ssh-copy-id to enable ssh key login to this machine from your workstation Start a screen session Try to get familiarized with the screen keyboard commands What does screen ls do ? Hint http //www linuxjournal com/article/6340 is a good reference for screen beginners

`screen -ls`: list running screen sessions

## Q3. Install git clone the git repository and check out branch u1804 as described in https //gitlab os3 nl/Networking/pogo/blob/u1804/README md Install the required dependencies for pogo

```
sudo apt install git lxd lxc screen bridge-utils tcpdump net-tools man curl btrfs-tools systemd-container
git clone https://gitlab.os3.nl/Networking/pogo
cd pogo
git checkout new-pogo
```

## Q4. In the directory you will find the create-base-container sh script that upon execution will create a container named ogobase Open the create-base-container sh with a text editor and try to understand what it does Execute the script as root Once the script finishes check that the base container has been created using the lxc command Do not dive into the rest of the scripts for now

```
sudo ./create-base-container.sh
```

## Q5.

### a. Clone the basecontainer using the lxc toolset and name this lx1 Start this the container and check that everything works ==

```
sudo lxc list
sudo lxc copy ogobase lx1
sudo lxc start lx1
sudo lxc exec -t --env TERM=xterm-256color lx1 -- bash
```

### b. Stop and delete the lx1 container using the lxc command from the host machine ==

```
sudo lxc stop lx1
sudo lxc delete lx1
```

### c. Investigate what other functionalities lxc provides and briefly describe each function in your log enumerate

- start, stop, restart, launch: start/stop container(s)
- list, info: info about container(s)
- move, snapshot, restore, copy: container state management
- exec, console: exec in/connect to container
- network: lxc network management
- storage: lxc storage volume management
- file: file in container management
- image, publish: backing image management
- remote, cluster: multi host management
- profile, config: lxc config

## Q6. What nictypes are available for Linux containers and how do they work?

- physical: pass through a physical device to a container
- bridged: create virtual device and connect to bridge on host
- macvlan: clone existing network with different MAC address
- ipvlan: clone existing network with different IP address
- p2p: create virtual device pair (guest and host)
- sriov: "Passes a virtual function of an SR-IOV enabled physical network device into the container."

- https://github.com/lxc/lxd/blob/master/doc/containers.md

## Q7. Investigate openvswitch-switch and then set up the simple network depicted in Figure 1 so that both instances can ping each other via IPv4 successfully

- openvswitch: larger, more feature rich, multilayer switch
- ip bridge: simpler layer 2 switch

### Not working

```
sudo ip link add lxc0 type bridge
sudo lxc copy ogobase lx2
sudo lxc copy ogobase lx3
sudo lxc config device add lx2 eth1 nic name=eth1 nictype=bridged parent=lxc0
sudo lxc config device add lx3 eth1 nic name-eth1 nictype=bridged parent=lxc0
sudo lxc config device set lx2 eth1 ipv4.address 10.0.0.2
sudo lxc config device set lx3 eth1 ipv4.address 10.0.0.3
sudo lxc start lx2 lx3
```

- http://www.fiber-optic-transceiver-module.com/ovs-vs-linux-bridge-who-is-the-winner.html

## Q8. Using the configurations/other/simple cfg as guidelines create a config file that replicates the network in Figure 2. Show this config file in your logs, including the values of a b x and y. Also create a simple network diagram depicting the subnets and the IPs configured on each host interface

=== Q9. Create the network environment by using python pogo py create config cfg ===

=== Q10. Bring up the network using python pogo py start config cfg and do the following: Inspect the IP configuration (addresses routing table) for all A B R (IPv4 and IPv6). Check connectivity between A-R B-R A-B over IPv4 and IPv6. For IPv6 use both the link local and the global addresses. Add IPv4 and IPv6 static routes on A and B such that there is connectivity between the two Show A can reach B via IPv4 and IPv6 ===

=== Q11. Stop and start your network using pogo ===

=== Q12. On R configure and enable the radvd daemon using the IP blocks mentioned in the previous task Explain all the configuration parameters used As with the previous task inspect the IP configuration do a connectivity check and explain the differences ===

=== Q13. Explain how the IPv6 address received by host A was derived ===

=== Q14. Why wasn't it necessary to manually add routes? ===

=== Q15. Stop the radvd service and see if the network breaks Explain why ===

=== Q16. Check the tcpdump path on the host system It should contain pcap files of the last pogo run These dumps should contain all the relevant packets regarding auto-configuration ===

=== Q17. Explain the auto-negotiation process that takes place over the A-R segment using the packet trace as supporting material Decode and explain all the interesting packets ===

=== Q18. Bonus: Remove all the IP addressing from the network config file Install and configure avahi for IPv6 on R and hosts such that the SSH service on A is accessible from B by just by typing ssh hostA local Do not use any /etc/hosts You will need to build a new base container for this (and cannot use IPv4) ===

=== Q19. Bonus: Fork the network script on Gitlab and add any extra functionality that you find useful (eg sanity check for config files easier config syntax status check for running networks etc ) ===
