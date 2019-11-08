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
lxc list
lxc copy ogobase lx1
lxc start lx1
lxc exec -t --env TERM=xterm-256color lx1 -- bash
```

### b. Stop and delete the lx1 container using the lxc command from the host machine ==

```
lxc stop lx1
lxc delete lx1
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

```
sudo ip link add br0 type bridge
sudo ip link set br0 up
lxc copy ogobase lx2

lxc config device add lx2 eth1 nic nictype=bridged parent=br0 name=eth1
lxc config device add lx3 eth1 nic nictype=bridged parent=br0 name=eth1
lxc config set lx2 raw.lxc 'lxc.net.0.ipv4.address = 10.0.0.2/8'
lxc config set lx3 raw.lxc 'lxc.net.0.ipv4.address = 10.0.0.3/8'
lxc start lx2 lx3

lxc stop lx2 lx3
lxc delete lx2 lx3
sudo ip link delete dev lxc0
```

```
arccy@g1» sudo lxc exec -t --env TERM=xterm-256color lx2 -- bash
root@lx2:~# ping 10.0.0.3
PING 10.0.0.3 (10.0.0.3) 56(84) bytes of data.
64 bytes from 10.0.0.3: icmp_seq=1 ttl=64 time=0.044 ms
64 bytes from 10.0.0.3: icmp_seq=2 ttl=64 time=0.015 ms
64 bytes from 10.0.0.3: icmp_seq=3 ttl=64 time=0.016 ms
^C
--- 10.0.0.3 ping statistics ---
3 packets transmitted, 3 received, 0% packet loss, time 2035ms
rtt min/avg/max/mdev = 0.015/0.025/0.044/0.013 ms
```

```
arccy@g1» lxc list
+---------+---------+-----------------+------+------------+-----------+
|  NAME   |  STATE  |      IPV4       | IPV6 |    TYPE    | SNAPSHOTS |
+---------+---------+-----------------+------+------------+-----------+
| lx2     | RUNNING | 10.0.0.2 (eth1) |      | PERSISTENT | 0         |
+---------+---------+-----------------+------+------------+-----------+
| lx3     | RUNNING | 10.0.0.3 (eth1) |      | PERSISTENT | 0         |
+---------+---------+-----------------+------+------------+-----------+
| ogobase | STOPPED |                 |      | PERSISTENT | 0         |
+---------+---------+-----------------+------+------------+-----------+
```

- http://www.fiber-optic-transceiver-module.com/ovs-vs-linux-bridge-who-is-the-winner.html
- https://superuser.com/questions/1047891/lxd-containers-and-networking-with-static-ip

## Q8. Using the configurations/other/simple cfg as guidelines create a config file that replicates the network in Figure 2. Show this config file in your logs, including the values of a b x and y. Also create a simple network diagram depicting the subnets and the IPs configured on each host interface

- first name = `sean`
- last name = `liao`
- S1
  - ipv4: `4.4.0.0/23`
  - ipv6: `2001:db8:700:500::/64`
- S1
  - ipv4: `4.4.82.0/23`
  - ipv6: `2001:db8:700:529::/64`

```
[global]
switches = 2

[hostA]
#role can be router
role=host
eth12 = 0,4.4.0.1/23,2001:0db8:700:500::1/64,00:00:00:00:00:01

[hostR]
role=router
eth12=0,4.4.1.2/23,2001:0db8:700:500::2/64,00:00:00:00:00:12
eth23=1,4.4.82.2/23,2001:0db8:700:529::2/64,00:00:00:00:00:23

[hostB]
role=host
eth23=1,4.4.82.3/23,2001:0db8:700:529::3/64,00:00:00:00:00:03
```

## Q9. Create the network environment by using python pogo py create config cfg

- `sudo ./pogo -b -c simple.cfg`

## Q10. Bring up the network using python pogo py start config cfg and do the following: Inspect the IP configuration (addresses routing table) for all A B R (IPv4 and IPv6). Check connectivity between A-R B-R A-B over IPv4 and IPv6. For IPv6 use both the link local and the global addresses. Add IPv4 and IPv6 static routes on A and B such that there is connectivity between the two Show A can reach B via IPv4 and IPv6

- `sudo ./pogo -s -c simple.cfg`
- hostA: `ip r add 4.4.0.0/23 via 4.4.0.2`
- hostA: `ip r add 2001:db8:700:500::/58 via 2001:db8:700:500::2`
- hostB: `ip r add 4.4.0.0/23 via 4.4.82.2`
- hostB: `ip r add 2001:db8:700:500::/58 via 2001:db8:700:529::2`

tests (`ping -c 1 ip`, `ping -c 1 ip%eth`)

- hostA -> hostR
  - ipv4: `64 bytes from 4.4.0.2: icmp_seq=1 ttl=64 time=0.023 ms`
  - ipv6: `64 bytes from 2001:db8:700:500::2: icmp_seq=1 ttl=64 time=0.024 ms`
  - ipv6 ll: `64 bytes from fe80::216:3eff:fea1:2e3e%eth12: icmp_seq=1 ttl=64 time=0.029 ms`
- hostR -> hostA
  - ipv4: `64 bytes from 4.4.0.1: icmp_seq=1 ttl=64 time=0.025 ms`
  - ipv6: `64 bytes from 2001:db8:700:500::1: icmp_seq=1 ttl=64 time=0.030 ms`
  - ipv6 ll: `64 bytes from fe80::216:3eff:fee3:6342%eth12: icmp_seq=1 ttl=64 time=0.030 ms`
- hostA -> hostB
  - ipv4: `64 bytes from 4.4.82.3: icmp_seq=1 ttl=63 time=0.047 ms`
  - ipv6: `64 bytes from 2001:db8:700:529::3: icmp_seq=1 ttl=63 time=0.086 ms`
- hostB -> hostA
  - ipv4: `64 bytes from 4.4.0.1: icmp_seq=1 ttl=63 time=0.033 ms`
  - ipv6: `64 bytes from 2001:db8:700:500::1: icmp_seq=1 ttl=63 time=0.048 ms`
- hostB -> hostR
  - ipv4: `64 bytes from 4.4.82.2: icmp_seq=1 ttl=64 time=0.022 ms`
  - ipv6: `64 bytes from 2001:db8:700:529::2: icmp_seq=1 ttl=64 time=0.030 ms`
  - ipv6 ll: `64 bytes from fe80::216:3eff:feb3:2796%eth23: icmp_seq=1 ttl=64 time=0.029 ms`
- hostR -> hostB
  - ipv4: `64 bytes from 4.4.82.3: icmp_seq=1 ttl=64 time=0.025 ms`
  - ipv6: `64 bytes from 2001:db8:700:529::3: icmp_seq=1 ttl=64 time=0.029 ms`
  - ipv6 ll: `64 bytes from fe80::216:3eff:fe43:d676%eth23: icmp_seq=1 ttl=64 time=0.028 ms`

```
new-pogo arccy@g1» lxc list
+---------+---------+------------------+-----------------------------+------------+-----------+
|  NAME   |  STATE  |       IPV4       |            IPV6             |    TYPE    | SNAPSHOTS |
+---------+---------+------------------+-----------------------------+------------+-----------+
| hostA   | RUNNING | 4.4.0.1 (eth12)  | 2001:db8:700:500::1 (eth12) | PERSISTENT | 0         |
+---------+---------+------------------+-----------------------------+------------+-----------+
| hostB   | RUNNING | 4.4.82.3 (eth23) | 2001:db8:700:529::3 (eth23) | PERSISTENT | 0         |
+---------+---------+------------------+-----------------------------+------------+-----------+
| hostR   | RUNNING | 4.4.82.2 (eth23) | 2001:db8:700:529::2 (eth23) | PERSISTENT | 0         |
|         |         | 4.4.0.2 (eth12)  | 2001:db8:700:500::2 (eth12) |            |           |
+---------+---------+------------------+-----------------------------+------------+-----------+
| ogobase | STOPPED |                  |                             | PERSISTENT | 0         |
+---------+---------+------------------+-----------------------------+------------+-----------+
```

## Q11. Stop and start your network using pogo

- `sudo ./pogo -s -c simple.cfg`
- `sudo ./pogo -s -c simple.cfg`

## Q12. On R configure and enable the radvd daemon using the IP blocks mentioned in the previous task Explain all the configuration parameters used As with the previous task inspect the IP configuration do a connectivity check and explain the differences

- hostA -> hostB: `64 bytes from 2001:db8:700:529:216:3eff:fe43:d676: icmp_seq=1 ttl=63 time=0.103 ms`
- hostB -> hostA: `64 bytes from 2001:db8:700:500:216:3eff:fee3:6342: icmp_seq=1 ttl=63 time=0.041 ms`

- edit: `radvd.conf`
- `systemctl start radvd`

```
interface eth12 {
  AdvSendAdvert on;
  prefix 2001:db8:700:500::/64 {
  };
};

interface eth23 {
  AdvSendAdvert on;
  prefix 2001:db8:700:529::/64 {
  };
};
```

## Q13. Explain how the IPv6 address received by host A was derived

```
180: eth12@if181: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP group default qlen 1000
    link/ether 00:16:3e:e3:63:42 brd ff:ff:ff:ff:ff:ff link-netnsid 0
    inet 4.4.0.1/23 brd 4.4.1.255 scope global eth12
       valid_lft forever preferred_lft forever
    inet6 2001:db8:700:500:216:3eff:fee3:6342/64 scope global dynamic mngtmpaddr noprefixroute
       valid_lft 86250sec preferred_lft 14250sec
    inet6 2001:db8:700:500::1/64 scope global
       valid_lft forever preferred_lft forever
    inet6 fe80::216:3eff:fee3:6342/64 scope link
       valid_lft forever preferred_lft forever
```

## Q14. Why wasn't it necessary to manually add routes?

## Q15. Stop the radvd service and see if the network breaks Explain why

- `systemctl stop radvd`
- hostA -> hostB: `64 bytes from 2001:db8:700:529:216:3eff:fe43:d676: icmp_seq=1 ttl=63 time=0.036 ms`
- hostB -> hostA: `64 bytes from 2001:db8:700:500:216:3eff:fee3:6342: icmp_seq=1 ttl=63 time=0.038 ms`
- radvd AdvDefaultLifetime: 3 \* MaxRtrAdvInterval( 300 secs )

```
ip -6 r
2001:db8:700:500::/64 dev eth12 proto ra metric 100 pref medium
2001:db8:700:500::/64 dev eth12 proto kernel metric 256 pref medium
fe80::/64 dev eth12 proto kernel metric 256 pref medium
default via fe80::216:3eff:fea1:2e3e dev eth12 proto ra metric 100 pref medium
```

- https://linux.die.net/man/5/radvd.conf

## Q16. Check the tcpdump path on the host system It should contain pcap files of the last pogo run These dumps should contain all the relevant packets regarding auto-configuration

- `mv /tmp/pogo-* -t .`
- `tcpdump -r pogo-bridge0.pcap`

## Q17. Explain the auto-negotiation process that takes place over the A-R segment using the packet trace as supporting material Decode and explain all the interesting packets

```
14:21:14.917496 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:15.261484 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:15.654794 IP6 :: > ff02::1:ffa1:2e3e: ICMP6, neighbor solicitation, who has fe80::216:3eff:fea1:2e3e, length 32
14:21:15.685482 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:15.985529 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:21:16.017481 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:21:16.605482 IP6 :: > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:16.605512 IP6 :: > ff02::1:ffe3:6342: ICMP6, neighbor solicitation, who has fe80::216:3eff:fee3:6342, length 32
14:21:16.669508 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 4 group record(s), length 88
14:21:16.681481 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:17.533485 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 4 group record(s), length 88
14:21:17.629507 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:17.629531 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:17.661484 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:17.821485 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 1 group record(s), length 28
14:21:18.266109 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:18.266467 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:18.277482 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 5 group record(s), length 108
14:21:18.653490 IP6 :: > ff02::1:ff00:2: ICMP6, neighbor solicitation, who has 2001:db8:700:500::2, length 32
14:21:18.697480 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:21:18.733219 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:18.733306 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:18.909506 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 5 group record(s), length 108
14:21:19.208018 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:19.325505 IP6 :: > ff02::1:ff00:1: ICMP6, neighbor solicitation, who has 2001:db8:700:500::1, length 32
14:21:19.645485 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:21:19.693639 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:21.065539 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:21.629538 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:22.458255 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:22.496427 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:24.749150 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:25.200129 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:29.704597 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:30.474672 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:32.024345 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:32.662221 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:43.800235 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:45.859042 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:21:46.629323 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:21:47.087335 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:22:11.900094 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:22:14.782732 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:22:16.237398 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:22:17.661043 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:23:09.689750 IP6 fe80::216:3eff:fee3:6342 > ip6-allrouters: ICMP6, router solicitation, length 16
14:23:12.062312 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:23:14.883420 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:23:19.198268 IP6 fe80::216:3eff:fea1:2e3e > ip6-allrouters: ICMP6, router solicitation, length 16
14:24:27.394170 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:24:27.409497 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 5 group record(s), length 108
14:24:27.409499 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:24:27.557536 IP6 :: > ff02::1:ffa1:2e3e: ICMP6, neighbor solicitation, who has 2001:db8:700:500:216:3eff:fea1:2e3e, length 32
14:24:27.837517 IP6 :: > ff02::1:ffe3:6342: ICMP6, neighbor solicitation, who has 2001:db8:700:500:216:3eff:fee3:6342, length 32
14:24:28.029506 IP6 fe80::216:3eff:fee3:6342 > ff02::16: HBH ICMP6, multicast listener report v2, 2 group record(s), length 48
14:24:28.061505 IP6 fe80::216:3eff:fea1:2e3e > ff02::16: HBH ICMP6, multicast listener report v2, 5 group record(s), length 108
14:24:29.575852 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:24:45.592255 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:25:01.609749 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:25:01.816220 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:25:07.283714 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:25:26.850851 IP6 2001:db8:700:500:216:3eff:fee3:6342 > 2001:db8:700:529:216:3eff:fe43:d676: ICMP6, echo request, seq 1, length 64
14:25:26.850921 IP6 fe80::216:3eff:fea1:2e3e > ff02::1:ffe3:6342: ICMP6, neighbor solicitation, who has 2001:db8:700:500:216:3eff:fee3:6342, length 32
14:25:26.850931 IP6 2001:db8:700:500:216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor advertisement, tgt is 2001:db8:700:500:216:3eff:fee3:6342, length 32
14:25:26.850936 IP6 2001:db8:700:529:216:3eff:fe43:d676 > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, echo reply, seq 1, length 64
14:25:26.877648 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:25:31.997530 IP6 fe80::216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor solicitation, who has fe80::216:3eff:fea1:2e3e, length 32
14:25:31.997595 IP6 fe80::216:3eff:fea1:2e3e > fe80::216:3eff:fee3:6342: ICMP6, neighbor advertisement, tgt is fe80::216:3eff:fea1:2e3e, length 24
14:25:37.117505 IP6 fe80::216:3eff:fea1:2e3e > fe80::216:3eff:fee3:6342: ICMP6, neighbor solicitation, who has fe80::216:3eff:fee3:6342, length 32
14:25:37.117543 IP6 fe80::216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor advertisement, tgt is fe80::216:3eff:fee3:6342, length 24
14:26:06.287814 IP6 2001:db8:700:529:216:3eff:fe43:d676 > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, echo request, seq 1, length 64
14:26:06.287825 IP6 2001:db8:700:500:216:3eff:fee3:6342 > 2001:db8:700:529:216:3eff:fe43:d676: ICMP6, echo reply, seq 1, length 64
14:26:11.421531 IP6 fe80::216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor solicitation, who has fe80::216:3eff:fea1:2e3e, length 32
14:26:11.421543 IP6 fe80::216:3eff:fea1:2e3e > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, neighbor solicitation, who has 2001:db8:700:500:216:3eff:fee3:6342, length 32
14:26:11.421607 IP6 fe80::216:3eff:fea1:2e3e > fe80::216:3eff:fee3:6342: ICMP6, neighbor advertisement, tgt is fe80::216:3eff:fea1:2e3e, length 24
14:26:11.421612 IP6 2001:db8:700:500:216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor advertisement, tgt is 2001:db8:700:500:216:3eff:fee3:6342, length 24
14:26:44.968643 IP6 fe80::216:3eff:fea1:2e3e > ip6-allnodes: ICMP6, router advertisement, length 56
14:26:59.796304 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:27:18.805173 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:28:10.386753 IP6 2001:db8:700:500:216:3eff:fee3:6342 > 2001:db8:700:529:216:3eff:fe43:d676: ICMP6, echo request, seq 1, length 64
14:28:10.386784 IP6 2001:db8:700:529:216:3eff:fe43:d676 > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, echo reply, seq 1, length 64
14:28:12.085285 IP6 2001:db8:700:500:216:3eff:fee3:6342 > 2001:db8:700:529:216:3eff:fe43:d676: ICMP6, echo request, seq 1, length 64
14:28:12.085311 IP6 2001:db8:700:529:216:3eff:fe43:d676 > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, echo reply, seq 1, length 64
14:28:15.581530 IP6 fe80::216:3eff:fea1:2e3e > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, neighbor solicitation, who has 2001:db8:700:500:216:3eff:fee3:6342, length 32
14:28:15.581551 IP6 fe80::216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor solicitation, who has fe80::216:3eff:fea1:2e3e, length 32
14:28:15.581638 IP6 2001:db8:700:500:216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor advertisement, tgt is 2001:db8:700:500:216:3eff:fee3:6342, length 24
14:28:15.581664 IP6 fe80::216:3eff:fea1:2e3e > fe80::216:3eff:fee3:6342: ICMP6, neighbor advertisement, tgt is fe80::216:3eff:fea1:2e3e, length 24
14:28:20.701512 IP6 fe80::216:3eff:fea1:2e3e > fe80::216:3eff:fee3:6342: ICMP6, neighbor solicitation, who has fe80::216:3eff:fee3:6342, length 32
14:28:20.701615 IP6 fe80::216:3eff:fee3:6342 > fe80::216:3eff:fea1:2e3e: ICMP6, neighbor advertisement, tgt is fe80::216:3eff:fee3:6342, length 24
14:28:30.663399 IP6 2001:db8:700:529:216:3eff:fe43:d676 > 2001:db8:700:500:216:3eff:fee3:6342: ICMP6, echo request, seq 1, length 64
14:28:30.663408 IP6 2001:db8:700:500:216:3eff:fee3:6342 > 2001:db8:700:529:216:3eff:fe43:d676: ICMP6, echo reply, seq 1, length 64
14:28:51.383944 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:29:07.479974 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:31:01.950933 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:31:16.941846 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:33:09.395121 IP6 fe80::216:3eff:fee3:6342.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req
14:33:12.085236 IP6 fe80::216:3eff:fea1:2e3e.dhcpv6-client > ff02::1:2.dhcpv6-server: dhcp6 inf-req

```

## Q18. Bonus: Remove all the IP addressing from the network config file Install and configure avahi for IPv6 on R and hosts such that the SSH service on A is accessible from B by just by typing ssh hostA local Do not use any /etc/hosts You will need to build a new base container for this (and cannot use IPv4)

## Q19. Bonus: Fork the network script on Gitlab and add any extra functionality that you find useful (eg sanity check for config files easier config syntax status check for running networks etc )
