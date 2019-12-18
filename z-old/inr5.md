# RIPng

## Q1. Take a look at the FRR documentation and explain what zebra is

Zebra is an IP routing manager responsible for lookups, updates, and redistribution of routes.

- http://docs.frrouting.org/en/latest/zebra.html

## Q2. In the configurations/ folder of pogo you will find a config file for the simple RIPng network of Figure 1. Edit the ripng cfg so the pass variables contain the absolute paths on the host of the \_i directories.

- `sudo ./pogo -bc configurations/ripng/ripng.cfg`
- `sudo ./pogo -sc configurations/ripng/ripng.cfg`

## Q3. Test the connectivity between R1 and R3 using the traceroute6 utility

```
root@router1:~# traceroute6 2001:db8:0:f102::2
traceroute to 2001:db8:0:f102::2 (2001:db8:0:f102::2) from 2001:db8:0:f101::2, 30 hops max, 24 byte packets
 1  2001:db8:0:f101::1 (2001:db8:0:f101::1)  0.175 ms  0.06 ms  0.05 ms
 2  2001:db8:0:f102::2 (2001:db8:0:f102::2)  0.081 ms  0.038 ms  0.031 ms
```

```
root@router3:~# traceroute6 2001:db8:0:f101::2
traceroute to 2001:db8:0:f101::2 (2001:db8:0:f101::2) from 2001:db8:0:f102::2, 30 hops max, 24 byte packets
 1  2001:db8:0:f102::1 (2001:db8:0:f102::1)  0.072 ms  0.068 ms  0.042 ms
 2  2001:db8:0:f101::2 (2001:db8:0:f101::2)  0.054 ms  0.053 ms  0.054 ms
```

## Q4. Examine the config files for both the network script and quagga and then on one of the routers connect to (via vtysh ) and explore the ripng and zebra daemons Very briefly explain what the following commands do show ? show run config terminal router ? network exit write memory Hint some commands only exist in certain contexts

- show ?: show information about something
- show run: show runtime configuration
- configure terminal: enter configuration mode
- (config terminal) router ?: manage a routing process
- (config terminal router rip) network: enable routing on specified network
- exit: exit a (sub) level of menus
- write memory: writes out config from memory

## Q5. Connect to the RIPng process on R3 and display the RIPng routes and protocol status

```
router3# show ipv6 route ripng
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

R>* 2001:db8:0:f101::/64 [120/2] via fe80::216:3eff:fe23:514f, eth23, 00:18:26
```

```
router3# show ipv6 ripng status
Routing Protocol is "RIPng"
  Sending updates every 30 seconds with +/-50%, next due in 16 seconds
  Timeout after 180 seconds, garbage collect after 120 seconds
  Outgoing update filter list for all interface is not set
  Incoming update filter list for all interface is not set
  Default redistribution metric is 1
  Redistributing:    connected
  Default version control: send version 1, receive version 1
    Interface        Send  Recv
    eth23            1     1
  Routing for Networks:
    ::/0
  Routing Information Sources:
    Gateway          BadPackets BadRoutes  Distance Last Update
    fe80::216:3eff:fe23:514f
                        0          0        120      00:00:32
```

## Q6. Start a ping6 from R1 to R3. Remove the global IPv6 address configured on R2 s eth1 What do you observe after max 30 seconds? Why is the ping still working?

- `root@router1:~# ping6 2001:db8:0:f102::2`
- `root@router2:~# ip a delete 2001:db8:0:f102::1/64 dev eth23`
- after 30s: everything still works
- eth23's address is not necessary for routing, the routes are configured using ipv6 link local addresses

### Correction

eth23's public address is not needed for communication between directly connected interfaces (they use link local addresses).

recovery time is affected by the update timer

## Q7. Create the config file required to start the network depicted in Figure 2. Show the config file on your log

- `sudo ./pogo -sc configurations/ripng/ripng.cfg`
- `sudo ./pogo -dc configurations/ripng/ripng.cfg`
- edit
- `sudo ./pogo -bc configurations/ripng/ripng.cfg`
- `sudo ./pogo -sc configurations/ripng/ripng.cfg`

<spoiler|rigng.cfg>

```
[global]
session_path  = /tmp
tcpdump_path= /tmp
switches = 9
[router1]
role = router
eth1 = 0,4.4.1.1/24,2001:db8:700:501::1/64
eth123 = 8,4.4.41.1/24,2001:db8:700:529::1/64
eth14 = 5,4.4.7.1/24,2001:db8:700:507::1/64


[router2]
role = router
eth2 = 1,4.4.2.2/24,2001:db8:700:502::2/64
eth123 = 8,4.4.41.2/24,2001:db8:700:529::2/64

[router3]
role = router
eth3 = 2,4.4.3.3/24,2001:db8:700:503::3/64
eth35 = 6,4.4.15.3/24,2001:db8:700:50f::3/64
eth123 = 8,4.4.41.3/24,2001:db8:700:529::3/64

[router4]
role = router
eth4 = 3,4.4.4.4/24,2001:db8:700:504::4/64
eth14 = 5,4.4.7.4/24,2001:db8:700:507::4/64
eth45 = 7,4.4.31.4/24,2001:db8:700:51f::4/64

[router5]
role = router
eth5 = 4,4.4.5.5/24,2001:db8:700:505::5/64
eth35 = 6,4.4.15.5/24,2001:db8:700:50f::5/64
eth45 = 7,4.4.31.5/24,2001:db8:700:51f::5/64
```

</spoiler>

## Q8. Draw the diagram of the network including the IP address of each router interface ( Hint to make things easier try to establish addressing conventions )

{{:2019-2020:students:sean_liao:inr:inr5.png}}

## Q9. For R1 display the following the routing table, the RIPng table, the RIPng status Are there any differences between the RIPng table and the routing table? ( Hints ip 6 vtysh show )

the kernel routing table additionally shows the link local routes

<spoiler|routing table>

```
root@router1:~# ip -6 r
2001:db8:700:501::/64 dev eth1 proto kernel metric 256 pref medium
2001:db8:700:502::/64 via fe80::216:3eff:fe33:f7d6 dev eth123 proto ripng metric 20 pref medium
2001:db8:700:503::/64 via fe80::216:3eff:fe7f:be49 dev eth123 proto ripng metric 20 pref medium
2001:db8:700:504::/64 via fe80::216:3eff:fe85:1149 dev eth14 proto ripng metric 20 pref medium
2001:db8:700:505::/64 via fe80::216:3eff:fe85:1149 dev eth14 proto ripng metric 20 pref medium
2001:db8:700:507::/64 dev eth14 proto kernel metric 256 pref medium
2001:db8:700:50f::/64 via fe80::216:3eff:fe7f:be49 dev eth123 proto ripng metric 20 pref medium
2001:db8:700:51f::/64 via fe80::216:3eff:fe85:1149 dev eth14 proto ripng metric 20 pref medium
2001:db8:700:529::/64 dev eth123 proto kernel metric 256 pref medium
fe80::/64 dev eth123 proto kernel metric 256 pref medium
fe80::/64 dev eth1 proto kernel metric 256 pref medium
fe80::/64 dev eth14 proto kernel metric 256 pref medium
```

</spoiler>
<spoiler|RIPng table>

```
router1# show ipv6 route ripng
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

R>* 2001:db8:700:502::/64 [120/2] via fe80::216:3eff:fe33:f7d6, eth123, 00:02:23
R>* 2001:db8:700:503::/64 [120/2] via fe80::216:3eff:fe7f:be49, eth123, 00:02:22
R>* 2001:db8:700:504::/64 [120/2] via fe80::216:3eff:fe85:1149, eth14, 00:02:20
R>* 2001:db8:700:505::/64 [120/3] via fe80::216:3eff:fe85:1149, eth14, 00:02:19
R>* 2001:db8:700:50f::/64 [120/2] via fe80::216:3eff:fe7f:be49, eth123, 00:02:22
R>* 2001:db8:700:51f::/64 [120/2] via fe80::216:3eff:fe85:1149, eth14, 00:02:20
```

</spoiler>
<spoiler|RIPng status>

```
router1# show ipv6 ripng status
Routing Protocol is "RIPng"
  Sending updates every 30 seconds with +/-50%, next due in 6 seconds
  Timeout after 180 seconds, garbage collect after 120 seconds
  Outgoing update filter list for all interface is not set
  Incoming update filter list for all interface is not set
  Default redistribution metric is 1
  Redistributing:    connected
  Default version control: send version 1, receive version 1
    Interface        Send  Recv
    eth1             1     1
    eth14            1     1
    eth123           1     1
  Routing for Networks:
    ::/0
  Routing Information Sources:
    Gateway          BadPackets BadRoutes  Distance Last Update
    fe80::216:3eff:fe33:f7d6
                        0          0        120      00:00:24
    fe80::216:3eff:fe7f:be49
                        0          0        120      00:00:13
    fe80::216:3eff:fe85:1149
                        0          0        120      00:00:19
```

</spoiler>

## Q10. Configure R2 as a default gateway for all the other routers. Just adding static routes on each router is not allowed. Describe how you did this

```
router2(config)# ipv6 route ::/0 blackhole
router2(config-router)# route ::/0
```

### Correction

default information also works, i didnt put this because peter said don't use this

```
router2(config-router)# default-information originate
```

```
root@router5:~# ip -6 r
2001:db8:700:501::/64 via fe80::216:3eff:fec9:d597 dev eth35 proto ripng metric 20 pref medium
2001:db8:700:502::/64 via fe80::216:3eff:fec9:d597 dev eth35 proto ripng metric 20 pref medium
2001:db8:700:503::/64 via fe80::216:3eff:fec9:d597 dev eth35 proto ripng metric 20 pref medium
2001:db8:700:504::/64 via fe80::216:3eff:fe58:68db dev eth45 proto ripng metric 20 pref medium
2001:db8:700:505::/64 dev eth5 proto kernel metric 256 pref medium
2001:db8:700:507::/64 via fe80::216:3eff:fe58:68db dev eth45 proto ripng metric 20 pref medium
2001:db8:700:50f::/64 dev eth35 proto kernel metric 256 pref medium
2001:db8:700:51f::/64 dev eth45 proto kernel metric 256 pref medium
2001:db8:700:529::/64 via fe80::216:3eff:fec9:d597 dev eth35 proto ripng metric 20 pref medium
fe80::/64 dev eth35 proto kernel metric 256 pref medium
fe80::/64 dev eth45 proto kernel metric 256 pref medium
fe80::/64 dev eth5 proto kernel metric 256 pref medium
default via fe80::216:3eff:fec9:d597 dev eth35 proto ripng metric 20 pref medium
```

## Q11. Start a ping from R3 to R1 s address on the L1 segment. Turn off the interface that connects R1 to L123. How much time does it take for 3 to reach 1 again? Why? What can you say about the efficiency of RIPng in a network with unstable network paths?

- `ping6 -i 0.1 2001:db8:700:501::1`
- `root@router1:~# ip link set eth123 down`
- 91.4s: not very efficient at rerouting through unstable networks (with current configs)

```
64 bytes from 2001:db8:700:501::1: icmp_seq=240 ttl=64 time=0.021 ms
64 bytes from 2001:db8:700:501::1: icmp_seq=241 ttl=64 time=0.021 ms
From 2001:db8:700:529::3 icmp_seq=653 Destination unreachable: Address unreachable
...
From 2001:db8:700:529::2 icmp_seq=1145 Destination unreachable: No route
...
64 bytes from 2001:db8:700:501::1: icmp_seq=1155 ttl=62 time=0.092 ms
64 bytes from 2001:db8:700:501::1: icmp_seq=1156 ttl=62 time=0.035 ms
```

### Correction:

- ~47 seconds for ripng to realise the address is unreachable (update 30s +/-50%)
- ~201 seconds later the timeout is finally reached and the new route is used
- not really efficient for unstable networks
- don't know why previously only took 90s

```
root@router3:~# ping 2001:db8:700:501::1
PING 2001:db8:700:501::1(2001:db8:700:501::1) 56 data bytes
64 bytes from 2001:db8:700:501::1: icmp_seq=1 ttl=64 time=0.098 ms
64 bytes from 2001:db8:700:501::1: icmp_seq=2 ttl=64 time=0.042 ms
From 2001:db8:700:529::3 icmp_seq=49 Destination unreachable: Address unreachable
From 2001:db8:700:529::2 icmp_seq=170 Destination unreachable: No route
64 bytes from 2001:db8:700:501::1: icmp_seq=203 ttl=62 time=0.092 ms
```

## Q12. Using the packet dump for the L123 segment explain very briefly the startup and convergence of the RIPng protocol and the payload of the packets (look for interesting packets when are they sent and why) Only mention the relevant packets. Upload the raw dump file (as generated by the sniffer) to your wiki and provide a link on your log

```
00:16:3e:7f:85:e3 : router 1
00:16:3e:20:c4:e3 : router 2
00:16:3e:13:aa:e3 : router 3
```

- router 1 broadcasts a request:
  - `15:45:26.055822 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf1fe -> 0x08be!] ripng-req dump`
- router 1 broadcasts an advert
  - `15:45:27.050160 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf226 -> 0x9a1d!] ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)`
- router 2 broadcasts a request
  - `15:45:27.762611 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34de -> 0xc5de!] ripng-req dump`
- router 1 responds to router2's request
  - `15:45:27.762763 00:16:3e:7f:85:e3 > 00:16:3e:20:c4:e3, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x34141, hlim 64, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > fe80::216:3eff:fe33:f7d6.521: [bad udp cksum 0x28bc -> 0x6388!] ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)`
- router 3 broadcasts a request
  - `15:45:28.452157 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfb9c -> 0xff1f!] ripng-req dump`
- router 1 responds to router 3's request
  - `15:45:28.452330 00:16:3e:7f:85:e3 > 00:16:3e:13:aa:e3, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x62700, hlim 64, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > fe80::216:3eff:fe7f:be49.521: [bad udp cksum 0xef7a -> 0x9cc9!] ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)`
- router 2 responds to router 3's request
  - `15:45:28.452343 00:16:3e:20:c4:e3 > 00:16:3e:13:aa:e3, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0x9b56e, hlim 64, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > fe80::216:3eff:fe7f:be49.521: [bad udp cksum 0x3246 -> 0xd3d2!] ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)`
- router 3 broadcasts an advert
  - `15:45:29.451020 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfbc4 -> 0x9075!] ripng-resp 3: 2001:db8:700:503::/64 (1) 2001:db8:700:50f::/64 (1) 2001:db8:700:529::/64 (1)`
- router 3 broadcasts an advert with segment 04, 45, 05
  - `15:45:32.633916 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfbc4 -> 0x9084!] ripng-resp 3: 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:51f::/64 (2)`
- router 1 broadcasts an advert with segment 04, 45, 05
  - `15:45:33.762491 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf226 -> 0x9a22!] ripng-resp 3: 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:51f::/64 (2)`

<spoiler|tcpdump -env ripng-converge-dump.pcap udp and port 521>

```
15:45:26.055822 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf1fe -> 0x08be!]  ripng-req dump
15:45:27.050160 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf226 -> 0x9a1d!]  ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)
15:45:27.762611 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34de -> 0xc5de!]  ripng-req dump
15:45:27.762763 00:16:3e:7f:85:e3 > 00:16:3e:20:c4:e3, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x34141, hlim 64, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > fe80::216:3eff:fe33:f7d6.521: [bad udp cksum 0x28bc -> 0x6388!]  ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)
15:45:28.452157 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfb9c -> 0xff1f!]  ripng-req dump
15:45:28.452330 00:16:3e:7f:85:e3 > 00:16:3e:13:aa:e3, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x62700, hlim 64, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > fe80::216:3eff:fe7f:be49.521: [bad udp cksum 0xef7a -> 0x9cc9!]  ripng-resp 3: 2001:db8:700:501::/64 (1) 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)
15:45:28.452343 00:16:3e:20:c4:e3 > 00:16:3e:13:aa:e3, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0x9b56e, hlim 64, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > fe80::216:3eff:fe7f:be49.521: [bad udp cksum 0x3246 -> 0xd3d2!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:45:28.761443 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:45:28.761853 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf212 -> 0x1401!]  ripng-resp 2: 2001:db8:700:507::/64 (1) 2001:db8:700:529::/64 (1)
15:45:29.451020 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfbc4 -> 0x9075!]  ripng-resp 3: 2001:db8:700:503::/64 (1) 2001:db8:700:50f::/64 (1) 2001:db8:700:529::/64 (1)
15:45:29.451410 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34de -> 0x4b0b!]  ripng-resp 1: 2001:db8:700:529::/64 (1)
15:45:32.633916 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfbc4 -> 0x9084!]  ripng-resp 3: 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:51f::/64 (2)
15:45:33.762491 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf226 -> 0x9a22!]  ripng-resp 3: 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:51f::/64 (2)
15:45:51.761669 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:45:53.051431 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:46:05.453874 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:46:08.053920 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:46:17.761939 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:46:29.456253 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:46:39.058239 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:46:40.763315 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:46:51.460801 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:47:10.058968 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:47:18.764466 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:47:26.060442 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:47:32.461310 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:47:46.060837 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:47:51.764673 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:48:17.461593 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:48:18.765236 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:48:30.061073 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:48:35.465065 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:48:45.765443 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 106: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 52) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34f2 -> 0xd126!]  ripng-resp 2: 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:49:10.574852 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x34de -> 0xc4ed!]  ripng-resp 1: ::/0 (1)
15:49:14.467612 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:49:15.061477 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:49:25.769689 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:49:37.467861 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:49:38.061929 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:49:56.769947 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:50:09.470260 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:50:18.064957 00:16:3e:7f:85:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x13865, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fec3:b467.521 > ff02::9.521: [bad udp cksum 0xf262 -> 0x2c4a!]  ripng-resp 6: 2001:db8:700:501::/64 (1) 2001:db8:700:504::/64 (2) 2001:db8:700:505::/64 (3) 2001:db8:700:507::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:50:19.771089 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:50:30.473907 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:50:52.772197 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:51:11.478390 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:51:33.774564 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:51:50.481314 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:52:11.779628 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:52:16.484863 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:52:30.780101 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:52:49.782385 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:52:59.486441 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 186: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 132) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc00 -> 0x22a2!]  ripng-resp 6: 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:53:14.784311 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:53:20.067958 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfb9c -> 0x8471!]  ripng-resp 1: 2001:db8:700:501::/64 (4)
15:53:32.666805 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 86: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 32) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfb9c -> 0x846c!]  ripng-resp 1: 2001:db8:700:507::/64 (3)
15:53:33.486907 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:53:57.784583 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:54:17.492141 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:54:31.789609 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:54:57.789771 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:54:59.493822 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:55:19.494745 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:55:34.792262 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:55:57.499226 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:56:04.795389 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
15:56:14.500272 00:16:3e:13:aa:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 226: (class 0xc0, flowlabel 0x6cfa9, hlim 255, next-header UDP (17) payload length: 172) fe80::216:3eff:fe7f:be49.521 > ff02::9.521: [bad udp cksum 0xfc28 -> 0x2ed0!]  ripng-resp 8: 2001:db8:700:501::/64 (4) 2001:db8:700:503::/64 (1) 2001:db8:700:504::/64 (3) 2001:db8:700:505::/64 (2) 2001:db8:700:507::/64 (3) 2001:db8:700:50f::/64 (1) 2001:db8:700:51f::/64 (2) 2001:db8:700:529::/64 (1)
15:56:22.795992 00:16:3e:20:c4:e3 > 33:33:00:00:00:09, ethertype IPv6 (0x86dd), length 126: (class 0xc0, flowlabel 0xd590c, hlim 255, next-header UDP (17) payload length: 72) fe80::216:3eff:fe33:f7d6.521 > ff02::9.521: [bad udp cksum 0x3506 -> 0xd0fd!]  ripng-resp 3: ::/0 (1) 2001:db8:700:502::/64 (1) 2001:db8:700:529::/64 (1)
```

</spoiler>

{{:2019-2020:students:sean_liao:inr:ripng-converge-dump.pcap}}

## Q13. Configure R1 such that it will accept updates only from R2 and R3. Show the configuration changes. Warning some solutions only work via ripngd s telnet interface not vtysh

```
!
router ripng
network ::/0
ipv6 distribute-list q13 in eth14
redistribute connected
!
ipv6 access-list q13 deny any
!
```

```
router1# show ipv6 ripng
  <cr>
  status  IPv6 routing protocol process parameters and statistics
  vrf     Specify the VRF
router1# show ipv6 ripng
Codes: R - RIPng, C - connected, S - Static, O - OSPF, B - BGP
Sub-codes:
      (n) - normal, (s) - static, (d) - default, (r) - redistribute,
      (i) - interface, (a/S) - aggregated/Suppressed

   Network      Next Hop                      Via     Metric Tag Time
C(i) 2001:db8:700:501::/64
                  ::                          self       1    0
R(n) 2001:db8:700:502::/64
                  fe80::216:3eff:fec0:e22a    eth123     2    0  02:32
R(n) 2001:db8:700:503::/64
                  fe80::216:3eff:fe00:cef5    eth123     2    0  02:52
R(n) 2001:db8:700:504::/64
                  fe80::216:3eff:fe00:cef5    eth123     4    0  02:52
R(n) 2001:db8:700:505::/64
                  fe80::216:3eff:fe00:cef5    eth123     3    0  02:52
C(i) 2001:db8:700:507::/64
                  ::                          self       1    0
R(n) 2001:db8:700:50f::/64
                  fe80::216:3eff:fe00:cef5    eth123     2    0  02:52
R(n) 2001:db8:700:51f::/64
                  fe80::216:3eff:fe00:cef5    eth123     3    0  02:52
C(i) 2001:db8:700:529::/64
                  ::                          self       1    0
```

## Q14. OPTIONAL: Stop the quagga daemon on all the routers and configure bird to achieve similar results
