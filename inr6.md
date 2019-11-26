# OSPF

## Q1. Create the config file required to start the network depicted in Figure 1

```
[global]
session_path  = /tmp
tcpdump_path= /tmp
switches = 10

[router1]
role = router
eth1 = 0,4.4.1.1/24,2001:db8:700:501::1/64
eth14 = 6,4.4.9.1/24,2001:db8:700:509::1/64
eth123 = 8,4.4.41.1/24,2001:db8:700:529::1/64

[router2]
role = router
eth2 = 1,4.4.2.2/24,2001:db8:700:502::2/64
eth123 = 8,4.4.41.2/24,2001:db8:700:529::2/64

[router3]
role = router
eth3 = 2,4.4.3.3/24,2001:db8:700:503::3/64
eth35 = 7,4.4.18.3/24,2001:db8:700:512::3/64
eth123 = 8,4.4.41.3/24,2001:db8:700:529::3/64

[router4]
role = router
eth4 = 3,4.4.4.4/24,2001:db8:700:504::4/64
eth14 = 6,4.4.9.4/24,2001:db8:700:509::4/64
eth456 = 9,4.4.33.4/24,2001:db8:700:521::4/64

[router5]
role = router
eth5 = 4,4.4.5.5/24,2001:db8:700:505::5/64
eth35 = 7,4.4.18.5/24,2001:db8:700:512::5/64
eth456 = 9,4.4.33.5/24,2001:db8:700:521::5/64

[router6]
role = router
eth6 = 5,4.4.6.6/24,2001:db8:700:506::6/64
eth456 = 9,4.4.33.6/24,2001:db8:700:521::6/64
```

## Q2. Draw a diagram of the network that shows the IP addressing for each router interface

## Q3. Configure OSPF on each router in such a way that the network is stable and operational (any point is reachable from any point) All routers are in the same area

```
!
hostname router1
password 1
log stdout
!
debug ospf6 events
debug ospf6 packet
!
!
router ospf6
redistribute connected
area 0.0.0.0 range 2001:db8:700:500::/56
interface eth1 area 0.0.0.0
interface eth14 area 0.0.0.0
interface eth123 area 0.0.0.0
!
line vty
!
end
```

```
router1# show ipv6 route
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

O   2001:db8:700:501::/64 [110/10] is directly connected, eth1, 00:03:34
C>* 2001:db8:700:501::/64 is directly connected, eth1, 00:03:47
O>* 2001:db8:700:502::/64 [110/20] via fe80::216:3eff:fe16:15a0, eth123, 00:03:30
O>* 2001:db8:700:503::/64 [110/20] via fe80::216:3eff:fedb:3814, eth123, 00:03:31
O>* 2001:db8:700:504::/64 [110/20] via fe80::216:3eff:fec9:1f0c, eth14, 00:03:31
O>* 2001:db8:700:505::/64 [110/30] via fe80::216:3eff:fedb:3814, eth123, 00:03:26
  *                                via fe80::216:3eff:fec9:1f0c, eth14, 00:03:26
O>* 2001:db8:700:506::/64 [110/30] via fe80::216:3eff:fec9:1f0c, eth14, 00:03:25
O   2001:db8:700:509::/64 [110/10] is directly connected, eth14, 00:03:33
C>* 2001:db8:700:509::/64 is directly connected, eth14, 00:03:47
O>* 2001:db8:700:512::/64 [110/20] via fe80::216:3eff:fedb:3814, eth123, 00:03:31
O>* 2001:db8:700:521::/64 [110/20] via fe80::216:3eff:fec9:1f0c, eth14, 00:03:26
O   2001:db8:700:529::/64 [110/10] is directly connected, eth123, 00:03:34
C>* 2001:db8:700:529::/64 is directly connected, eth123, 00:03:47
C * fe80::/64 is directly connected, eth14, 00:03:47
C * fe80::/64 is directly connected, eth123, 00:03:47
C>* fe80::/64 is directly connected, eth1, 00:03:47
```

## Q4. For R5 display the following: the router ID, the routing table, the OSPF routing table, the OSPF interface table, the OSPF neighbors. Are there any differences between the OSPF table and the routing table? ( Hints telnet ip show ) Why?

kernel routing table is specified mainly in terms of interfaces,
ospf6 is specified in terms of next hop addresses

router id: 4.4.33.5

```
router5# show ipv6 ospf6
 OSPFv3 Routing Process (0) with Router-ID 4.4.33.5
 Running 00:04:47
 LSA minimum arrival 1000 msecs
 Initial SPF scheduling delay 0 millisec(s)
 Minimum hold time between consecutive SPFs 50 millsecond(s)
 Maximum hold time between consecutive SPFs 5000 millsecond(s)
 Hold time multiplier is currently 1
 SPF algorithm last executed 00:03:37 ago, reason N-
 Last SPF duration 0 sec 110 usec
 SPF timer is inactive
 Number of AS scoped LSAs is 16
 Number of areas in this router is 1

 Area 0.0.0.0
     Number of Area scoped LSAs is 22
     Interface attached to this area: eth5 eth35 eth456
SPF last executed 217.636660s ago
```

ospf routing table

```
router5# show ipv6 ospf6 route
*N IA 2001:db8:700:501::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:56
                                     fe80::216:3eff:fe7d:ebfd  eth456
 N E1 2001:db8:700:501::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
                                     fe80::216:3eff:fe7d:ebfd  eth456
*N IA 2001:db8:700:502::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
 N E1 2001:db8:700:502::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
*N IA 2001:db8:700:503::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
 N E1 2001:db8:700:503::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
*N IA 2001:db8:700:504::/64          fe80::216:3eff:fe7d:ebfd  eth456 00:05:56
 N E1 2001:db8:700:504::/64          fe80::216:3eff:fe7d:ebfd  eth456 00:05:56
*N IA 2001:db8:700:505::/64          ::                          eth5 00:06:01
*N IA 2001:db8:700:506::/64          fe80::216:3eff:fe02:b963  eth456 00:05:55
 N E1 2001:db8:700:506::/64          fe80::216:3eff:fe02:b963  eth456 00:05:55
*N IA 2001:db8:700:509::/64          fe80::216:3eff:fe7d:ebfd  eth456 00:05:56
 N E1 2001:db8:700:509::/64          fe80::216:3eff:fe7d:ebfd  eth456 00:05:56
 N E1 2001:db8:700:509::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
                                     fe80::216:3eff:fe7d:ebfd  eth456
*N IA 2001:db8:700:512::/64          ::                         eth35 00:06:01
 N E1 2001:db8:700:512::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
*N IA 2001:db8:700:521::/64          ::                        eth456 00:06:00
 N E1 2001:db8:700:521::/64          fe80::216:3eff:fe02:b963  eth456 00:05:56
                                     fe80::216:3eff:fe7d:ebfd  eth456
*N IA 2001:db8:700:529::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
 N E1 2001:db8:700:529::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
 N E1 2001:db8:700:529::/64          fe80::216:3eff:fe0a:8371   eth35 00:05:59
                                     fe80::216:3eff:fe7d:ebfd  eth456
```

ospf interface table

```
router5# show ipv6 ospf6 interface
  <cr>
  IFNAME   Interface name(e.g. ep0)
     eth5 eth35 eth456 lo
  prefix   Display connected prefixes to advertise
  traffic  Protocol Packet counters
router5# show ipv6 ospf6 interface
eth5 is up, type BROADCAST
  Interface ID: 381
  Internet Address:
    inet : 4.4.5.5/24
    inet6: 2001:db8:700:505::5/64
    inet6: fe80::216:3eff:fe16:a5d9/64
  Instance ID 0, Interface MTU 1500 (autodetect: 1500)
  MTU mismatch detection: enabled
  Area ID 0.0.0.0, Cost 10
  State DR, Transmit Delay 1 sec, Priority 1
  Timer intervals configured:
   Hello 10, Dead 40, Retransmit 5
  DR: 4.4.33.5 BDR: 0.0.0.0
  Number of I/F scoped LSAs is 2
    0 Pending LSAs for LSUpdate in Time 00:00:00 [thread off]
    0 Pending LSAs for LSAck in Time 00:00:00 [thread off]
eth35 is up, type BROADCAST
  Interface ID: 377
  Internet Address:
    inet : 4.4.18.5/24
    inet6: 2001:db8:700:512::5/64
    inet6: fe80::216:3eff:fe17:952e/64
  Instance ID 0, Interface MTU 1500 (autodetect: 1500)
  MTU mismatch detection: enabled
  Area ID 0.0.0.0, Cost 10
  State BDR, Transmit Delay 1 sec, Priority 1
  Timer intervals configured:
   Hello 10, Dead 40, Retransmit 5
  DR: 4.4.41.3 BDR: 4.4.33.5
  Number of I/F scoped LSAs is 4
    0 Pending LSAs for LSUpdate in Time 00:00:00 [thread off]
    0 Pending LSAs for LSAck in Time 00:00:00 [thread off]
eth456 is up, type BROADCAST
  Interface ID: 379
  Internet Address:
    inet : 4.4.33.5/24
    inet6: 2001:db8:700:521::5/64
    inet6: fe80::216:3eff:fe3e:485a/64
  Instance ID 0, Interface MTU 1500 (autodetect: 1500)
  MTU mismatch detection: enabled
  Area ID 0.0.0.0, Cost 10
  State BDR, Transmit Delay 1 sec, Priority 1
  Timer intervals configured:
   Hello 10, Dead 40, Retransmit 5
  DR: 4.4.33.6 BDR: 4.4.33.5
  Number of I/F scoped LSAs is 6
    0 Pending LSAs for LSUpdate in Time 00:00:00 [thread off]
    0 Pending LSAs for LSAck in Time 00:00:00 [thread off]
lo is up, type LOOPBACK
  Interface ID: 1
   OSPF not enabled on this interface
```

ospf neighbors

```
router5# show ipv6 ospf6 neighbor
Neighbor ID     Pri    DeadTime    State/IfState         Duration I/F[State]
4.4.41.3          1    00:00:33     Full/DR              00:07:33 eth35[BDR]
4.4.33.4          1    00:00:34     Full/DROther         00:07:31 eth456[BDR]
4.4.33.6          1    00:00:37     Full/DR              00:07:32 eth456[BDR]
```

kernel routing table

```
root@router5:~# ip -6 r
2001:db8:700:501::/64 proto ospf metric 20
	nexthop via fe80::216:3eff:fe0a:8371 dev eth35 weight 1
	nexthop via fe80::216:3eff:fe7d:ebfd dev eth456 weight 1
2001:db8:700:502::/64 via fe80::216:3eff:fe0a:8371 dev eth35 proto ospf metric 20 pref medium
2001:db8:700:503::/64 via fe80::216:3eff:fe0a:8371 dev eth35 proto ospf metric 20 pref medium
2001:db8:700:504::/64 via fe80::216:3eff:fe7d:ebfd dev eth456 proto ospf metric 20 pref medium
2001:db8:700:505::/64 dev eth5 proto kernel metric 256 pref medium
2001:db8:700:506::/64 via fe80::216:3eff:fe02:b963 dev eth456 proto ospf metric 20 pref medium
2001:db8:700:509::/64 via fe80::216:3eff:fe7d:ebfd dev eth456 proto ospf metric 20 pref medium
2001:db8:700:512::/64 dev eth35 proto kernel metric 256 pref medium
2001:db8:700:521::/64 dev eth456 proto kernel metric 256 pref medium
2001:db8:700:529::/64 via fe80::216:3eff:fe0a:8371 dev eth35 proto ospf metric 20 pref medium
fe80::/64 dev eth35 proto kernel metric 256 pref medium
fe80::/64 dev eth456 proto kernel metric 256 pref medium
fe80::/64 dev eth5 proto kernel metric 256 pref medium
```

## Q5. Identify the DR and BDR roles in one network segment as follows. Pick a network segment with both a DR and a BDR and explain how this was established. Shutdown the DR's interface that connects to that segment. Explain what happens and what are the new roles

Segment 456: DR: 4.4.33.6, BDR: 4.4.33.5: pick the highest router-id

- shutdown interface
- BDR becomes DR
- DROther (the remaining router) becomes BDR

```
eth456 is up, type BROADCAST
  Interface ID: 375
  Internet Address:
    inet : 4.4.33.4/24
    inet6: 2001:db8:700:521::4/64
    inet6: fe80::216:3eff:fe7d:ebfd/64
  Instance ID 0, Interface MTU 1500 (autodetect: 1500)
  MTU mismatch detection: enabled
  Area ID 0.0.0.0, Cost 10
  State DROther, Transmit Delay 1 sec, Priority 1
  Timer intervals configured:
   Hello 10, Dead 40, Retransmit 5
  DR: 4.4.33.6 BDR: 4.4.33.5
```

- `ip link set eth456 down`

```
router4# show ipv6 ospf6 interface eth456
eth456 is up, type BROADCAST
  Interface ID: 375
  Internet Address:
    inet : 4.4.33.4/24
    inet6: 2001:db8:700:521::4/64
    inet6: fe80::216:3eff:fe7d:ebfd/64
  Instance ID 0, Interface MTU 1500 (autodetect: 1500)
  MTU mismatch detection: enabled
  Area ID 0.0.0.0, Cost 10
  State BDR, Transmit Delay 1 sec, Priority 1
  Timer intervals configured:
   Hello 10, Dead 40, Retransmit 5
  DR: 4.4.33.5 BDR: 4.4.33.4
  Number of I/F scoped LSAs is 6
    0 Pending LSAs for LSUpdate in Time 00:00:00 [thread off]
    0 Pending LSAs for LSAck in Time 00:00:00 [thread off]
```

## Q6. Perform and explain all configurations required such that all traffic from R1 to R6 goes always via R3. Do not shutdown OSPF processes or interfaces

increase the path cost for the 1->4 link

<spoiler|routing pre>

```
router1# show ipv6 ospf6 spf tree
+-4.4.41.1 [0]
   +-4.4.41.1 Net-ID: 0.0.2.162 [10]
   |  +-4.4.33.4 [10]
   |     +-4.4.33.6 Net-ID: 0.0.2.186 [20]
   |        +-4.4.33.6 [20]
   +-4.4.41.3 Net-ID: 0.0.2.168 [10]
      +-4.4.41.2 [10]
      +-4.4.41.3 [10]
         +-4.4.41.3 Net-ID: 0.0.2.172 [20]
            +-4.4.33.5 [20]

router1# show ipv6 route
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

O   2001:db8:700:501::/64 [110/10] is directly connected, eth1, 00:00:42
C>* 2001:db8:700:501::/64 is directly connected, eth1, 00:00:55
O>* 2001:db8:700:502::/64 [110/20] via fe80::216:3eff:feb7:5569, eth123, 00:00:38
O>* 2001:db8:700:503::/64 [110/20] via fe80::216:3eff:feec:26a2, eth123, 00:00:39
O>* 2001:db8:700:504::/64 [110/20] via fe80::216:3eff:fe46:a9b2, eth14, 00:00:39
O>* 2001:db8:700:505::/64 [110/30] via fe80::216:3eff:feec:26a2, eth123, 00:00:34
  *                                via fe80::216:3eff:fe46:a9b2, eth14, 00:00:34
O>* 2001:db8:700:506::/64 [110/30] via fe80::216:3eff:fe46:a9b2, eth14, 00:00:33
O   2001:db8:700:509::/64 [110/10] is directly connected, eth14, 00:00:40
C>* 2001:db8:700:509::/64 is directly connected, eth14, 00:00:55
O>* 2001:db8:700:512::/64 [110/20] via fe80::216:3eff:feec:26a2, eth123, 00:00:39
O>* 2001:db8:700:521::/64 [110/20] via fe80::216:3eff:fe46:a9b2, eth14, 00:00:34
O   2001:db8:700:529::/64 [110/10] is directly connected, eth123, 00:00:42
C>* 2001:db8:700:529::/64 is directly connected, eth123, 00:00:55
C * fe80::/64 is directly connected, eth14, 00:00:55
C * fe80::/64 is directly connected, eth123, 00:00:55
C>* fe80::/64 is directly connected, eth1, 00:00:55
```

</spoiler>

- `router1(config-if)# ipv6 ospf6 cost 65535`

<spoiler|routing post>

```
router1# show ipv6 ospf6 spf tree
+-4.4.41.1 [0]
   +-4.4.41.3 Net-ID: 0.0.2.168 [10]
      +-4.4.41.2 [10]
      +-4.4.41.3 [10]
         +-4.4.41.3 Net-ID: 0.0.2.172 [20]
            +-4.4.33.5 [20]
               +-4.4.33.6 Net-ID: 0.0.2.186 [30]
                  +-4.4.33.4 [30]
                  |  +-4.4.41.1 Net-ID: 0.0.2.162 [40]
                  +-4.4.33.6 [30]

router1# show ipv6 route
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

O   2001:db8:700:501::/64 [110/10] is directly connected, eth1, 00:02:16
C>* 2001:db8:700:501::/64 is directly connected, eth1, 00:02:29
O>* 2001:db8:700:502::/64 [110/20] via fe80::216:3eff:feb7:5569, eth123, 00:02:12
O>* 2001:db8:700:503::/64 [110/20] via fe80::216:3eff:feec:26a2, eth123, 00:02:13
O>* 2001:db8:700:504::/64 [110/40] via fe80::216:3eff:feec:26a2, eth123, 00:00:30
O>* 2001:db8:700:505::/64 [110/30] via fe80::216:3eff:feec:26a2, eth123, 00:00:30
O>* 2001:db8:700:506::/64 [110/40] via fe80::216:3eff:feec:26a2, eth123, 00:00:30
O   2001:db8:700:509::/64 [110/40] is directly connected, eth14, 00:00:30
C>* 2001:db8:700:509::/64 is directly connected, eth14, 00:02:29
O>* 2001:db8:700:512::/64 [110/20] via fe80::216:3eff:feec:26a2, eth123, 00:02:13
O>* 2001:db8:700:521::/64 [110/30] via fe80::216:3eff:feec:26a2, eth123, 00:00:30
O   2001:db8:700:529::/64 [110/10] is directly connected, eth123, 00:02:16
C>* 2001:db8:700:529::/64 is directly connected, eth123, 00:02:29
C * fe80::/64 is directly connected, eth14, 00:02:29
C * fe80::/64 is directly connected, eth123, 00:02:29
C>* fe80::/64 is directly connected, eth1, 00:02:29
```

</spoiler>

- http://docs.frrouting.org/en/latest/ospf6d.html#ospf6-area

## Q7. Configure R2 such that it behaves as a default gateway for all the other routers

```
router2(config)# ipv6 route ::/0 blackhole
router2(config-ospf6)# redistribute static
```

```
root@router6:~# ip -6 r
...
default proto ospf metric 20
	nexthop via fe80::216:3eff:fe3e:485a dev eth456 weight 1
	nexthop via fe80::216:3eff:fe7d:ebfd dev eth456 weight 1
```

## Q8. Configure OSPF on each router in such a way that the network is stable and operational (any point is reachable from any point)

edit ospf6.conf with new areas

```
router2# show ipv6 route
Codes: K - kernel route, C - connected, S - static, R - RIPng,
       O - OSPFv3, I - IS-IS, B - BGP, N - NHRP, T - Table,
       v - VNC, V - VNC-Direct, A - Babel, D - SHARP, F - PBR,
       f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

O>* 2001:db8:700:501::/64 [110/20] via fe80::216:3eff:fed9:13c3, eth123, 00:02:24
O   2001:db8:700:502::/64 [110/10] is directly connected, eth2, 00:02:28
C>* 2001:db8:700:502::/64 is directly connected, eth2, 00:02:39
O>* 2001:db8:700:503::/64 [110/20] via fe80::216:3eff:fef7:e930, eth123, 00:02:23
O>* 2001:db8:700:504::/64 [110/20] via fe80::216:3eff:fed9:13c3, eth123, 00:01:29
O>* 2001:db8:700:505::/64 [110/30] via fe80::216:3eff:fed9:13c3, eth123, 00:01:29
O>* 2001:db8:700:506::/64 [110/30] via fe80::216:3eff:fed9:13c3, eth123, 00:01:29
O>* 2001:db8:700:509::/64 [110/10] via fe80::216:3eff:fed9:13c3, eth123, 00:02:24
O>* 2001:db8:700:512::/64 [110/20] via fe80::216:3eff:fef7:e930, eth123, 00:02:23
O>* 2001:db8:700:521::/64 [110/20] via fe80::216:3eff:fed9:13c3, eth123, 00:01:29
O   2001:db8:700:529::/64 [110/10] is directly connected, eth123, 00:02:24
C>* 2001:db8:700:529::/64 is directly connected, eth123, 00:02:39
C * fe80::/64 is directly connected, eth2, 00:02:39
C>* fe80::/64 is directly connected, eth123, 00:02:39
```

## Q9. From R3 perform a traceroute to L5. Explain the output

- the trace travels through area 1 to router 1, through area 0 to router 4, through area 2 to router 5
- although router 3 and router 5 are directly connected, they are in different areas in OSPF everything will route through the backbone (area 0)

```
root@router3:~# traceroute6 2001:db8:700:505::5
traceroute to 2001:db8:700:505::5 (2001:db8:700:505::5) from 2001:db8:700:529::3, 30 hops max, 24 byte packets
 1  2001:db8:700:529::1 (2001:db8:700:529::1)  0.157 ms  0.059 ms  0.042 ms
 2  2001:db8:700:509::4 (2001:db8:700:509::4)  0.066 ms  0.058 ms  0.048 ms
 3  2001:db8:700:505::5 (2001:db8:700:505::5)  0.069 ms  0.064 ms  0.054 ms
```

```
router1# show ipv6 ospf6 border-routers
Router-ID       Rtr-Bits Options        Path-Type  Area
4.4.33.4        ------EB --|R|-|--|E|V6 Intra-Area 0.0.0.0
4.4.33.5        ------E- --|R|-|--|E|V6 Inter-Area 0.0.0.0
4.4.33.6        ------E- --|R|-|--|E|V6 Inter-Area 0.0.0.0
4.4.41.1        ------EB --|R|-|--|E|V6 Intra-Area 0.0.0.0
4.4.41.1        ------EB --|R|-|--|E|V6 Intra-Area 0.0.0.1
4.4.41.2        ------E- --|R|-|--|E|V6 Intra-Area 0.0.0.1
4.4.41.3        ------E- --|R|-|--|E|V6 Intra-Area 0.0.0.1
```

## Q10. BONUS: Pick any network segment that has 3 routers. Identify all the types of network packets that are sent from network startup to convergence. Identify all the OSPF router roles. Upload the raw dump file (as generated by the sniffer) to your wiki and provide a link in your log. Only mention the relevant packets

## Q11. BONUS: Configure OSPFv2 and assign IPv4 address to the interfaces on each router in such a way that the network is stable and operational (any point is reachable from any point) . All routers are in the same area. Explain the configuration in your log
