# VLANs and STP Template

## Q1. What command do you need to type to add a VLAN id 5 to eth0 ?

```
ip link add link eth0 name eth0.5 type vlan id 5
```

## Q2. You will now create the network depicted in Figure 1. Login to each host using screen and configure them manually

```
h1:
  ip l add link eth0 name eth0.10 type vlan id 10
  ip l set eth0.10 up
  ip a add dev eth0.10 10.0.10.1/24
  ip l add link eth0 name eth0.30 type vlan id 30
  ip l set eth0.30 up
  ip a add dev eth0.30 10.0.30.1/24
h2:
  ip l add link eth0 name eth0.10 type vlan id 10
  ip l set eth0.10 up
  ip a add dev eth0.10 10.0.10.2/24
  ip l add link eth0 name eth0.20 type vlan id 20
  ip l set eth0.20 up
  ip a add dev eth0.20 10.0.20.2/24
  ip l add link eth0 name eth0.30 type vlan id 30
  ip l set eth0.30 up
  ip a add dev eth0.30 10.0.30.2/24
b1:
  ip l add name bridge0 type bridge
  ip l set bridge0 up
  ip l add link eth0 name eth0.10 type vlan id 10
  ip l set eth0.10 up
  ip l set dev eth0.10 master bridge0
  ip l add link eth1 name eth1.10 type vlan id 10
  ip l set eth1.10 up
  ip l set dev eth1.10 master bridge0
b2:
  ip l add link eth1 name eth1.20 type vlan id 20
  ip l set eth1.20 up
  ip a add dev eth1.20 10.0.20.1/24
b3:
  ip l add name bridge0 type bridge
  ip l set bridge0 up
  ip l add link eth0 name eth0.30 type vlan id 30
  ip l set eth0.30 up
  ip l set dev eth0.30 master bridge0
  ip l add link eth1 name eth1.30 type vlan id 30
  ip l set eth1.30 up
  ip l set dev eth1.30 master bridge0
```

## Q3. Once you have have configured everything perform the following ping tests

```
root@host1:~# ping -c 1 10.0.10.2
PING 10.0.10.2 (10.0.10.2) 56(84) bytes of data.
64 bytes from 10.0.10.2: icmp_seq=1 ttl=64 time=0.036 ms

--- 10.0.10.2 ping statistics ---
1 packets transmitted, 1 received, 0% packet loss, time 0ms
rtt min/avg/max/mdev = 0.036/0.036/0.036/0.000 ms
```

```
root@host2:~# ping -c 1 10.0.30.2
PING 10.0.30.2 (10.0.30.2) 56(84) bytes of data.
64 bytes from 10.0.30.2: icmp_seq=1 ttl=64 time=0.014 ms

--- 10.0.30.2 ping statistics ---
1 packets transmitted, 1 received, 0% packet loss, time 0ms
rtt min/avg/max/mdev = 0.014/0.014/0.014/0.000 ms
```

## Q4. Explain the structure of a Ethernet frame that contains an ICMP echo request. Explain all the VLAN related fields in detail

| start | length | value          | purpose          |
| ----- | ------ | -------------- | ---------------- |
| 0     | 6      | 0016 3e3c 2bb3 | dst addr         |
| 6     | 6      | 0016 3e52 21ec | src addr         |
| 12    | 2      | 8100           | ethertype 802.1Q |
| 14    | 2      | 0014           | vlan tag: 20     |

```
14:13:07.457283 00:16:3e:3c:2b:b3 > 00:16:3e:52:21:ec, ethertype 802.1Q (0x8100), length 102: vlan 20, p 0, ethertype IPv4, (tos 0x0, ttl 64, id 49140, offset 0, flags [DF], proto ICMP (1), length 84)
    10.0.20.1 > 10.0.20.2: ICMP echo request, id 432, seq 1, length 64
	0x0000:  0016 3e52 21ec 0016 3e3c 2bb3 8100 0014  ..>R!...><+.....
	0x0010:  0800 4500 0054 bff4 4000 4001 3eb2 0a00  ..E..T..@.@.>...
	0x0020:  1401 0a00 1402 0800 3a66 01b0 0001 f3bd  ........:f......
	0x0030:  ca5d 0000 0000 38fa 0600 0000 0000 1011  .]....8.........
	0x0040:  1213 1415 1617 1819 1a1b 1c1d 1e1f 2021  ...............!
	0x0050:  2223 2425 2627 2829 2a2b 2c2d 2e2f 3031  "#$%&'()*+,-./01
	0x0060:  3233 3435 3637                           234567
```

- https://en.wikipedia.org/wiki/IEEE_802.1Q

## Q5. The containers that pogo creates have Reverse Path Filtering. What is Reverse Path Filtering? Turn RPF off using the following commands description

Reverse Path Filtering: filtering incoming packets based on the expected return path of a reply

## Q6. Add static routes on H1 H2 B2 to allow B2 to reach H1 over both VLANs Display the routing tables on H1 H2 and B2 and the output of ping from B2 to 10 0 10 1 and 10 0 30 1

```
h1:
  none
h2:
  ip r add 10.0.20.0/24 via 10.0.10.1
b2:
  ip r add 10.0.10.0/24 via 10.0.20.1
  ip r add 10.0.30.0/24 via 10.0.20.1
```

notes:

- `10.0.20.2 -> 10.0.10.1` stable
- `10.0.20.2 -> 10.0.30.1` high packet loss: 98%
- solution: give all virtual interfaces their own unique mac addresses

```
root@bridge2:~# ping 10.0.30.1
PING 10.0.30.1 (10.0.30.1) 56(84) bytes of data.
64 bytes from 10.0.30.1: icmp_seq=1 ttl=63 time=0.072 ms
64 bytes from 10.0.30.1: icmp_seq=2 ttl=63 time=0.044 ms
64 bytes from 10.0.30.1: icmp_seq=3 ttl=63 time=0.041 ms
64 bytes from 10.0.30.1: icmp_seq=4 ttl=63 time=0.027 ms
64 bytes from 10.0.30.1: icmp_seq=5 ttl=63 time=0.027 ms
64 bytes from 10.0.30.1: icmp_seq=6 ttl=63 time=0.028 ms
64 bytes from 10.0.30.1: icmp_seq=7 ttl=63 time=0.029 ms
64 bytes from 10.0.30.1: icmp_seq=8 ttl=63 time=0.028 ms
^C
--- 10.0.30.1 ping statistics ---
8 packets transmitted, 8 received, 0% packet loss, time 7154ms
rtt min/avg/max/mdev = 0.027/0.037/0.072/0.014 ms
```

```
root@bridge2:~# ping 10.0.10.1
PING 10.0.10.1 (10.0.10.1) 56(84) bytes of data.
64 bytes from 10.0.10.1: icmp_seq=1 ttl=63 time=0.043 ms
64 bytes from 10.0.10.1: icmp_seq=2 ttl=63 time=0.031 ms
64 bytes from 10.0.10.1: icmp_seq=3 ttl=63 time=0.042 ms
64 bytes from 10.0.10.1: icmp_seq=4 ttl=63 time=0.030 ms
64 bytes from 10.0.10.1: icmp_seq=5 ttl=63 time=0.042 ms
64 bytes from 10.0.10.1: icmp_seq=6 ttl=63 time=0.043 ms
64 bytes from 10.0.10.1: icmp_seq=7 ttl=63 time=0.028 ms
^C
--- 10.0.10.1 ping statistics ---
7 packets transmitted, 7 received, 0% packet loss, time 6131ms
rtt min/avg/max/mdev = 0.028/0.037/0.043/0.006 ms
```

## Q7. BONUS Explain why it is necessary to turn off RPF on H1 and H2

H1:

- 10.0.20.2 -> 10.0.30.1 would have been filtered out (return through 10.0.10.2)
  H2:
- 10.0.30.1 -> 10.0.20.2 (reply) would have been filtered out (return through 10.0.10.1)

## Q8. What is the maximum number of VLAN IDs active on a network segment? Be precise

- basic vlan: 2^12 - 2 (0, 4095 reserved): 4094
- with Service VLAN tag: 4094 x 4094
- with single PBN: 4094 x 4094 x 4094
- with multilayer PBN: as many as will fit in (potentially unlimited) MTU

- https://wiki.mef.net/display/CESG/Provider+Backbone+Bridged+Networks

## Q9. Draw a network diagram that depicts the configuration Make sure the VLANs are clearly marked

{{:2019-2020:students:sean_liao:inr:inr4.png}}

## Q10. Create the config file that starts the network depicted in Figure 1 using the bridge.cfg

- `sudo -i -E /home/arccy/pogo/pogo -bc /home/arccy/pogo/bridge.cfg`
- `sudo ./pogo -sc bridge.cfg`
- `sudo xen create /etc/xen/bridge1.cfg`
- `sudo xen create /etc/xen/bridge2.cfg`
- `sudo xen create /etc/xen/bridge3.cfg`

notes: my host doesn't like the mirrors, edit `STP_bridge.py` with new mirrors

<spoiler|brudge.cfg>

```
[global]
switches = 2

[host1]
role= host
home=/tmp
eth0 = 0,10.0.0.1/8,2001:0db8:0:f101::1/64

[host2]
role = host
home=/tmp
eth0 = 1,10.0.0.2/8,2001:0db8:0:f101::2/64

[bridge1]
role = stp_bridge
eth0 = 0,,
eth1 = 1,,

[bridge2]
role = stp_bridge
eth0 = 0,,
eth1 = 1,,

[bridge3]
role = stp_bridge
eth0 = 0,,
eth1 = 1,,
```

</spoiler>
<spoiler|bridge1 forward-blocking>

```
root@bridge1:~# bridge -d link
2: eth0 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge1 state forwarding priority 32 cost 100
    hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth0	1 PVID Egress Untagged

3: eth1 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge1 state blocking priority 32 cost 100
hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth1 1 PVID Egress Untagged

4: bridge1 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge1
bridge1 1 PVID Egress Untagged
```

</spoiler>
<spoiler| brudge2 forward-forward>

```
root@bridge2:~# bridge -d link
2: eth0 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge2 state forwarding priority 32 cost 100
hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth0 1 PVID Egress Untagged

3: eth1 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge2 state forwarding priority 32 cost 100
hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth1 1 PVID Egress Untagged

4: bridge2 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge2
bridge2 1 PVID Egress Untagged
```

</spoiler>
<spoiler|bridge3 forward-blocking>

```
root@bridge3:~# bridge -d link
2: eth0 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge3 state forwarding priority 32 cost 100
hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth0 1 PVID Egress Untagged

3: eth1 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge3 state blocking priority 32 cost 100
hairpin off guard off root_block off fastleave off learning on flood on mcast_flood on neigh_suppress off vlan_tunnel off
eth1 1 PVID Egress Untagged

4: bridge3 state UP : <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 master bridge3
bridge3 1 PVID Egress Untagged
```

</spoiler>

## Q11. Create a diagram of the networks showing the state of all the bridge ports and the root bridge

{{:2019-2020:students:sean_liao:inr:inr4-1.png}}

## Q12. Describe in detail (i.e. your own words, not verbatim dumps) what packets are sent, when and why. Explain how the root bridge was elected. Only mention the relevant packets. Upload the raw dump file (as generated by the sniffer) to your wiki, and provide a link in your log.

- 15:43:52.057222: (pogo1) bridge1 advertises itself
  - only bridge in existence
- 15:43:52.057243: (pogo0) bridge1 advertises itself
  - only bridge in existence
- 15:43:56.729398: (pogo0) bridge2 advertises itself
  - new bridge
- 15:43:56.729498: (pogo1) bridge2 advertises itself
  - new bridge
- 15:43:56.729626: (pogo0) bridge1 announces topology change
  - recognises bridge2 as root
- 15:43:57.985603: (pogo0) bridge2 topology change ack
  - response to previous message
- 15:44:04.001834: (pogo0) bridge3 advertises bridge2 as root
  - already received?
- 15:44:04.001879: (pogo0) bridge3 announces topology change
  - recognises bridge2 as root
- 15:44:04.993669: (pogo0) bridge2 topology change ack
  - response to previous message

```
eth0             UP             00:16:3e:e1:67:e0 <BROADCAST,MULTICAST,UP,LOWER_UP>
eth1             UP             00:16:3e:91:06:e1 <BROADCAST,MULTICAST,UP,LOWER_UP>
bridge1          UP             00:16:3e:c2:46:06 <BROADCAST,MULTICAST,UP,LOWER_UP>

eth0             UP             00:16:3e:95:2a:e0 <BROADCAST,MULTICAST,UP,LOWER_UP>
eth1             UP             00:16:3e:50:9d:e1 <BROADCAST,MULTICAST,UP,LOWER_UP>
bridge2          UP             00:16:3e:3e:1b:b2 <BROADCAST,MULTICAST,UP,LOWER_UP>

eth0             UP             00:16:3e:05:39:e0 <BROADCAST,MULTICAST,UP,LOWER_UP>
eth1             UP             00:16:3e:4c:0e:e1 <BROADCAST,MULTICAST,UP,LOWER_UP>
bridge3          UP             00:16:3e:44:73:94 <BROADCAST,MULTICAST,UP,LOWER_UP>
```

<spoiler|tcpdump -env -r start0.pcap stp>

```
15:43:52.057243 00:16:3e:e1:67:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:54.809352 00:16:3e:e1:67:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:56.729337 00:16:3e:e1:67:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:56.729398 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:43:56.729626 00:16:3e:e1:67:e0 > 01:80:c2:00:00:00, 802.3, length 7: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Topology Change
15:43:57.985603 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change, Topology change ACK], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:43:58.977566 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:00.001614 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:01.985667 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:04.001708 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:04.001834 00:16:3e:05:39:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:44:73:94.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 100
15:44:04.001879 00:16:3e:05:39:e0 > 01:80:c2:00:00:00, 802.3, length 7: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Topology Change
15:44:04.993669 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change, Topology change ACK], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:05.985715 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:08.001769 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:09.985786 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:12.001816 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:13.985847 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:16.001880 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:17.985934 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:20.001963 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:21.985982 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:24.002012 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:25.986093 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:28.002082 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:29.986118 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:32.002157 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:33.986189 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:36.002197 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:37.986254 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:40.002248 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:41.986308 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:44.002336 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:45.986380 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:48.002425 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:49.986436 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:52.002496 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:53.986504 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:56.002541 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:57.986549 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:00.002605 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:01.986650 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:04.002647 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:05.986680 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:08.002712 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:09.986735 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:12.002777 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:13.986802 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:16.002820 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:17.986863 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:20.002894 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:21.986949 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:24.002972 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:25.986999 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:28.003038 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:29.987061 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:32.003105 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:33.987136 00:16:3e:95:2a:e0 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8001, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0

```

</spoiler>
<spoiler|tcpdump -env -r start1.pcap stp>

```
15:43:52.057222 00:16:3e:91:06:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:54.809329 00:16:3e:91:06:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:56.729439 00:16:3e:91:06:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:c2:46:06.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:c2:46:06, root-pathcost 0
15:43:56.729498 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:43:58.017634 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:00.001595 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:01.985613 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:04.001684 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:05.985680 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:08.001746 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:09.985766 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:12.001796 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:13.985829 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:16.001861 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:17.985897 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:20.001928 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:21.985959 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:24.001995 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:25.986030 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:28.002061 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:29.986084 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:32.002105 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:33.986152 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:36.002178 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:37.986218 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [Topology change], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:40.002225 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:41.986274 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:44.002304 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:45.986343 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:48.002380 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:49.986404 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:52.002459 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:53.986471 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:56.002504 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:44:57.986530 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:00.002566 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:01.986604 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:04.002630 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:05.986623 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:08.002662 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:09.986699 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:12.002739 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:13.986763 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:16.002794 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:17.986828 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:20.002859 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:21.986895 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:24.002931 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:25.986959 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:28.002995 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:29.987040 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:32.003083 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:33.987107 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:36.003139 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
15:45:37.987168 00:16:3e:50:9d:e1 > 01:80:c2:00:00:00, 802.3, length 38: LLC, dsap STP (0x42) Individual, ssap STP (0x42) Command, ctrl 0x03: STP 802.1d, Config, Flags [none], bridge-id 8000.00:16:3e:3e:1b:b2.8002, length 35
	message-age 0.00s, max-age 20.00s, hello-time 2.00s, forwarding-delay 15.00s
	root-id 8000.00:16:3e:3e:1b:b2, root-pathcost 0
```

</spoiler>

- {{:2019-2020:students:sean_liao:inr:start0.pcap|pogo-bridge0.pcap}}
- {{:2019-2020:students:sean_liao:inr:start0.pcap|pogo-bridge1.pcap}}

## Q13.

### a. What parameters are used in the BPDU packets

- Max Age
- Hello Time
- Forward Delay

### b. What is the role of each parameter?

- Max Age: amount of time this data should be valid for
- Hello Time: time between root config messages
- Forward Delay: time to wait before state transitions

### c. What are they set to?

- Max Age: 20s
- Hello Time: 2s
- Forward Delay: 15s

## Q14. What happens if you shutdown the root bridge? Describe all the events that take place from the moment the bridge goes down until the network has converged again.

- `root@bridge2:~# ip link set bridge2 down`
  - bridge1: forwarding-blocking
  - bridge2: forwarding-blocking
- 20s later
  - bridge1: forwarding-listening
  - bridge3: forwarding-listening
- 15s later
  - bridge1: forwarding-blocking (transition in 2s)
  - bridge3: forwarding-learning
- 15s later
  - bridge1: forwarding-blocking
  - bridge3: forwarding-forwarding

## Q15. Next, on a bridge machine that has disabled ports, bring down the bridge, disable STP and then bring up the bridge again. Describe what happens mentioning the state of the ports on all bridges. After how much time do the other bridges notice your disruptive actions? What is the downtime experienced by the ping session? Based on the STP protocol variables explain this downtime

- `root@bridge3:~# ip link set bridge3 down`
  - no disruption in pings
  - no change in port states
- `root@bridge3:~# ip link set bridge3 type bridge stp 0`
- `root@bridge3:~# ip link set bridge3 up`
- 0s
  - new link is almost instantly recognized
  - bridge1: blocking-listening
    bridge2: forwarding-listening
- 0.xs later
  - bridge1: listening-blocking
    bridge2: forwarding-blocking
- 15s later
  - bridge1: learning-blocking
    bridge2: forwarding-blocking
- 15s later
  - bridge1: forwarding-blocking
    bridge2: forwarding-blocking
- 30.3 seconds (303 dropped with interval 0.1s)
  - 30s = 15s for listening and 15s for learning, set by `forwarding-delay 15.00s`

ping -i 0.1

```
64 bytes from 10.0.0.2: icmp_seq=134 ttl=64 time=0.356 ms
64 bytes from 10.0.0.2: icmp_seq=135 ttl=64 time=0.343 ms
64 bytes from 10.0.0.2: icmp_seq=438 ttl=64 time=0.771 ms
64 bytes from 10.0.0.2: icmp_seq=439 ttl=64 time=0.335 ms
```

ip -d link show bridge2

```
root@bridge2:~# ip -d link show bridge2
4: bridge2: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc noqueue state UP mode DEFAULT group default qlen 1000
    link/ether 00:16:3e:3e:1b:b2 brd ff:ff:ff:ff:ff:ff promiscuity 0
    bridge forward_delay 1500 hello_time 200 max_age 2000 ageing_time 30000 stp_state 1 priority 32768 vlan_filtering 0 vlan_protocol 802.1Q bridge_id 8000.0:16:3e:3e:1b:b2 designated_root 8000.0:16:3e:3e:1b:b2 root_port 0 root_path_cost 0 topology_change 0 topology_change_detected 0 hello_timer    1.75 tcn_timer    0.00 topology_change_timer    0.00 gc_timer  112.03 vlan_default_pvid 1 vlan_stats_enabled 0 group_fwd_mask 0 group_address 01:80:c2:00:00:00 mcast_snooping 1 mcast_router 1 mcast_query_use_ifaddr 0 mcast_querier 0 mcast_hash_elasticity 4 mcast_hash_max 512 mcast_last_member_count 2 mcast_startup_query_count 2 mcast_last_member_interval 100 mcast_membership_interval 26000 mcast_querier_interval 25500 mcast_query_interval 12500 mcast_query_response_interval 1000 mcast_startup_query_interval 3124 mcast_stats_enabled 0 mcast_igmp_version 2 mcast_mld_version 1 nf_call_iptables 0 nf_call_ip6tables 0 nf_call_arptables 0 addrgenmode eui64 numtxqueues 1 numrxqueues 1 gso_max_size 65536 gso_max_segs 65535
```

## Q16. Enable STP again on all bridges. Does the network come back to the initial state? Why?

```
root@bridge3:~# ip link set bridge3 down
root@bridge3:~# ip link set bridge3 type bridge stp 1
root@bridge3:~# ip link set bridge3 up
```

yes, after every network change, STP will reelect the master based on all available information. Here, using the lowest identifier, given the same set of nodes, they will always choose the same root bridge
