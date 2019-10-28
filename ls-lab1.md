# Virtualization

## Q1. Make sure that Ubuntu 18 04 64-bit is installed on your server Use the PXE service to start the installation process if needed Partition your disks such that you have at least 200 GB of unpartitioned space

1. reinstall ubuntu-18.04
2. partition
   1. 20GB for `/` on SSD
   2. 100GB for Q2
3. `sudo apt update && sudo apt upgrade`
4. `sudo apt install neovim python3-neovim zsh`
5. `chsh -s /usr/bin/zsh arccy`
6. `ssh-keygen -t ed25519`
7. edit `/etc/ssh/sshd_config` to `PermitRootLogin no`, `PasswordAuthentication no`
8. add `xterm-kitty` terminfo

## Q2. Install the lvm2 package and create a physical volume using 100 GB of the 200 GB free space reserved before On top of the physical volume create a volume group called VolumeGroupXen Here you will store the virtual machine images We will create the logical volumes later Hints pvcreate vgcreate pvdisplay

1. `sudo aot install lvm2`
2. `sudo pvcreate /dev/sdb2`
3. `sudo vgcreate VolumeGroupXen /dev/sdb2`

output: `arccy@nevers ~ % sudo pvdisplay`:

```
  --- Physical volume ---
  PV Name               /dev/sdb2
  VG Name               VolumeGroupXen
  PV Size               100.00 GiB / not usable 4.00 MiB
  Allocatable           yes
  PE Size               4.00 MiB
  Total PE              25599
  Free PE               25599
  Allocated PE          0
  PV UUID               tHk9mI-KAeU-OU86-ix8d-3tRq-WSBc-w1H2hQ
```

## Q3. Install the xen-hypervisor-4 9-amd64 package and if needed configure the system such that the Xen kernel is booted by default Check with dmesg whether the correct kernel booted after rebooting

1. `sudo apt install xen-hypervisor-4.9-amd64`
2. `sudo update-grub`
3. `sudo reboot`

output: `arccy@nevers ~ % sudo xl list`

```
Name                                        ID   Mem VCPUs	State	Time(s)
Domain-0                                     0 15992     8     r-----      13.8`
```

## Q4. Install bridge-utils and use brctl to manually create a bridge named xenbr0 Do not add any interfaces to it we will use routing instead of switching to connect the VMs to the Internet

1. `sudo apt install bridge-utils`
2. `sudo brctl addbr xenbr0`

output: `arccy@nevers ~ % brctl show`

```
bridge name	bridge id		STP enabled	interfaces
xenbr0		8000.000000000000	no`
```

## Q5. When creating the bridge Linux will also create a network interface called xenbr0 that connects your server to that bridge The IPv4 addresses to use for your VMs are those in the /28 subnet which is routed to your server (see SNE students mailing list) Assign the first free IPv4 address from your /28 subnet to this xenbr0 network interface using ifconfig

1. `sudo ip addr add 145.100.111.0 dev xenbr0`

output: `arccy@nevers ~ % ip addr`

```
1: lo: <LOOPBACK,UP,LOWER_UP> mtu 65536 qdisc noqueue state UNKNOWN group default qlen 1000
    link/loopback 00:00:00:00:00:00 brd 00:00:00:00:00:00
    inet 127.0.0.1/8 scope host lo
       valid_lft forever preferred_lft forever
    inet6 ::1/128 scope host
       valid_lft forever preferred_lft forever
2: eno1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP group default qlen 1000
    link/ether 34:17:eb:f0:dc:e3 brd ff:ff:ff:ff:ff:ff
    inet 145.100.104.117/27 brd 145.100.104.127 scope global dynamic eno1
       valid_lft 72876sec preferred_lft 72876sec
    inet6 fe80::3617:ebff:fef0:dce3/64 scope link
       valid_lft forever preferred_lft forever
3: eno2: <BROADCAST,MULTICAST> mtu 1500 qdisc noop state DOWN group default qlen 1000
    link/ether 34:17:eb:f0:dc:e4 brd ff:ff:ff:ff:ff:ff
4: xenbr0: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default qlen 1000
    link/ether 2e:26:ca:68:9e:d9 brd ff:ff:ff:ff:ff:ff
    inet 145.100.111.1/28 brd 145.100.111.15 scope global xenbr0
       valid_lft forever preferred_lft forever
    inet6 fe80::2c26:caff:fe68:9ed9/64 scope link
       valid_lft forever preferred_lft forever
```

## Q6. Test whether you can reach the address on the bridge interface from outside of your machine

from laptop:

```
» ping -c 3 145.100.111.0
PING 145.100.111.0 (145.100.111.0) 56(84) bytes of data.
64 bytes from 145.100.111.0: icmp_seq=1 ttl=62 time=1.45 ms
64 bytes from 145.100.111.0: icmp_seq=2 ttl=62 time=2.79 ms
64 bytes from 145.100.111.0: icmp_seq=3 ttl=62 time=1.76 ms

--- 145.100.111.0 ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2004ms
rtt min/avg/max/mdev = 1.449/1.998/2.790/0.573 ms
```

## Q7. Edit /etc/netplan/01-netcfg yaml such that xenbr0 will persist across system reboots Use the Debian/Ubuntu way

1. edit `/etc/netplan/01-netcfg.yaml`
2. `sudo sysctl net.ipv4.ip_forward=1`
3. edit `/etc/xen-tools/xen-tools.conf`: `bridge = xenbr0`

```
network:
  bridges:
    xenbr0:
      addresses:
        - "145.100.111.1/28"
```

## Q8. Install xen-tools and use xen-create-image to create a Ubuntu virtual machine that has the following characteristics. Note this virtual machine will be reachable from the Internet so don't use dummy passwords and protect your ssh daemon (man hosts allow)

1. `sudo apt install xen-tools`
2. follow pogo instructions
3. `sudo xen-create-image --memory=1024mb --size=10gb --swap=1024mb --vcpus=2 --dist=bionic --bridge=xenbr0 --ip=145.100.111.2 --gateway=145.100.111.1 --hostname=guest-01 --lvm=VolumeGroupXen --password=###`
4. `sudo xl create /etc/xen/guest-01.cfg`

output:

```
WARNING:  No netmask address specified!

General Information
--------------------
Hostname       :  guest-01
Distribution   :  bionic
Mirror         :  http://archive.ubuntu.com/ubuntu
Partitions     :  swap            1024mb (swap)
                  /               10gb  (ext4)
Image type     :  full
Memory size    :  1024mb
Bootloader     :  pygrub

Networking Information
----------------------
IP Address 1   : 145.100.111.2 [MAC: 00:16:3E:76:C6:56]
Gateway        : 145.100.111.1

Removing /dev/VolumeGroupXen/guest-01-swap - since we're forcing the install
Sleeping a few seconds to avoid LVM race conditions...
Removing /dev/VolumeGroupXen/guest-01-disk - since we're forcing the install
Sleeping a few seconds to avoid LVM race conditions...

Creating swap on /dev/VolumeGroupXen/guest-01-swap
Done

Creating ext4 filesystem on /dev/VolumeGroupXen/guest-01-disk
Done
Installation method: debootstrap
Done

Running hooks
Done

No role scripts were specified.  Skipping

Creating Xen configuration file
Done

No role scripts were specified.  Skipping
Setting up root password
Generating a password for the new guest.
All done


Logfile produced at:
	/var/log/xen-tools/guest-01.log

Installation Summary
---------------------
Hostname        :  guest-01
Distribution    :  bionic
MAC Address     :  00:16:3E:76:C6:56
IP Address(es)  :  145.100.111.2
SSH Fingerprint :  SHA256:cgdArNAxqNwmquEHfy+tmTMgEGL/ZMO7q0cVRXp8ITw (DSA)
SSH Fingerprint :  SHA256:1346wNeQ34kwIeQEN005tS8wM0diH5ERpweawDJV4TM (ECDSA)
SSH Fingerprint :  SHA256:DsgSKeaRu4kSnrQ/dFJvYfQzc0fUiSTL6qKINStzOfg (ED25519)
SSH Fingerprint :  SHA256:HmJAKIrp+qoeMWHZl59CThUtyqIli45aEq4tDq5UyfQ (RSA)
Root Password   :  ###
```

## Q9. The MAC address starts with 00 16 3E Explain why this prefix is used

This is the prefix assigned to the makers of Xen, Xensource, Inc

- https://www.macmonster.co.uk/macoui=00163E

## Q10. Start the virtual machine and login to its console and test network connectivity

```
$ ping 8.8.8.8
PING 8.8.8.8 (8.8.8.8) 56(84) bytes of data.
64 bytes from 8.8.8.8: icmp_seq=1 ttl=56 time=0.745 ms
64 bytes from 8.8.8.8: icmp_seq=2 ttl=56 time=0.743 ms
64 bytes from 8.8.8.8: icmp_seq=3 ttl=56 time=0.697 ms
^C
--- 8.8.8.8 ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2037ms
rtt min/avg/max/mdev = 0.697/0.728/0.745/0.031 ms
```

## Q11. Use xl to find information about the running VM and then stop it and start it again

### info

xl top

```
xentop - 20:44:53   Xen 4.9.2
2 domains: 1 running, 1 blocked, 0 paused, 0 crashed, 0 dying, 0 shutdown
Mem: 16734804k total, 16722744k used, 12060k free    CPUs: 8 @ 2112MHz
      NAME  STATE   CPU(sec) CPU(%)     MEM(k) MEM(%)  MAXMEM(k) MAXMEM(%) VCPUS NETS NETTX(k) NETRX(k) VBDS   VBD_OO   VBD_RD   VBD_WR  VBD_RSECT  VBD_WSECT SSID
  Domain-0 -----r        632    0.3   15446944   92.3   no limit       n/a     8    0        0        0    0        0        0        0          0          0    0
  guest-01 --b---          7    0.0    1048576    6.3    1049600       6.3     2    1      113       34    2        0     2495     1077     112010      31608    0
```

xl list

```
Name                                        ID   Mem VCPUs	State	Time(s)
Domain-0                                     0 15084     8     r-----     633.6
guest-01                                     4  1024     2     -b----       7.7
```

### stop

`xl pause guest-01 && xl list`

```
Name                                        ID   Mem VCPUs	State	Time(s)
Domain-0                                     0 15084     8     r-----     633.9
guest-01                                     4  1024     2     -bp---       7.8
```

### start

`xl unpause guest-01 && xl list`

```
Name                                        ID   Mem VCPUs	State	Time(s)
Domain-0                                     0 15084     8     r-----     634.2
guest-01                                     4  1024     2     -b----       7.8
```

## Q12. Configure your system such that Guest-01 is auto-started after a reboot

1. `sudo mkdir /etc/xen/auto`
2. `sudo ln -s /etc/xen/guest-01.cfg /etc/xen/auto`

- https://askubuntu.com/questions/196444/how-do-i-auto-start-xen-guests-on-boot

## Q13. Briefly explain the following terms DomU Dom0 PCI pass-through Which is which in your situation? Is PCI pass-through used and if so for what?

- DomU
- Dom0
- PCI passthrough

## Q14. deboostrap rinse and rpmstrap can be used to aid in the creation of virtual machine images In fact xen-create-image can use all of them under the hood When would you use one over the others?

## Q15. How do you think that the virtual machine communicates with the outside network in your setup? Draw a simple network diagram showing at least the network cards the bridges and any routers that might be present Don't forget to label everything with IP addresses and names
