# BGP Template

## Q1. Connect your INR DomU to switch marked Mgmt / OS3 IX at the top of each rack

```
sudo ip link add xenbr1 type bridge
sudo ip link set xenbr1 up
sudo ip link set eno2 master xenbr1
```

## Q2. Create your AS with pogo

```
[global]
session_path = /tmp

switches = 10

[router1]
role= router
eth0 = 0,10.40.1.1/24
eth1 = 5,10.40.123.1/24
eth2 = 6,10.40.14.1/24

[router2]
role = router
eth0 = 9,172.16.0.40/24
eth1 = 5,10.40.123.2/24


[router3]
role = router
eth0 = 2,10.40.3.3/24
eth1 = 5,10.40.123.3/24
eth2 = 8,10.40.35.3/24

[router4]
role = router
eth0 = 3,10.40.4.4/24
eth1 = 7,10.40.45.4/24
eth2 = 6,10.40.14.4/24

[router5]
role = router
eth0 = 4,10.40.5.5/24
eth1 = 7,10.40.45.5/24
eth2 = 8,10.40.35.5/24
```

## Q3. On the DomU VM connect the new eth1 to the correct openvSwitch instance using ovs-vsctl add-port ogo-ovsX eth1

```
ip link set eth1 master pogo-bridge9
root@g1:~# ip link set lxdbr0 up
root@g1:~# ip link set lxcbr0 up
root@g1:~# ip link set eth1 up
```

## Q4. Once that is done you can log into R2. You should now have an eth0 interface that connects to the OS3 IX. Make sure that all your bridges and interfaces are up. Check if you can ping a machine in the OS3 IX LAN ping 172 16 0 42

```
root@router2:~# ping 172.16.0.42
PING 172.16.0.42 (172.16.0.42) 56(84) bytes of data.
64 bytes from 172.16.0.42: icmp_seq=1 ttl=64 time=0.297 ms
64 bytes from 172.16.0.42: icmp_seq=2 ttl=64 time=0.279 ms
64 bytes from 172.16.0.42: icmp_seq=3 ttl=64 time=0.268 ms
64 bytes from 172.16.0.42: icmp_seq=4 ttl=64 time=0.336 ms
64 bytes from 172.16.0.42: icmp_seq=5 ttl=64 time=0.276 ms
^C
--- 172.16.0.42 ping statistics ---
5 packets transmitted, 5 received, 0% packet loss, time 4087ms
rtt min/avg/max/mdev = 0.268/0.291/0.336/0.026 ms
```

## Q5. Set up BGP on 2 using your assigned AS number This will be your border router

- `chown -R 100000:100000 frr`

```
router bgp 64640
bgp router-id 172.16.0.40
!
address-family ipv4 unicast
network 10.40.0.0/16
exit-address-family
```

## Q6. Use your border router to peer with two of your colleagues. Test if the connectivity between the networks is as you expected and document your findings. What does the network in your group look like and what are the relations? Hint Use neighbor a b c d next-hop-self or BGP may take a short-cut over the shared LAN later

3 independent ASs (me, davide, rutgter) each connected through a single border gateway router

```
router bgp 64640
 bgp router-id 172.16.0.40
 neighbor 172.16.0.26 remote-as 64626
 neighbor 172.16.0.37 remote-as 64637
!
address-family ipv4 unicast
 network 10.40.0.0/16
 neighbor 172.16.0.26 next-hop-self
 neighbor 172.16.0.37 next-hop-self
exit-address-family
!
```

```
router2# ping 10.37.123.2
PING 10.37.123.2 (10.37.123.2) 56(84) bytes of data.
64 bytes from 10.37.123.2: icmp_seq=1 ttl=64 time=0.524 ms
64 bytes from 10.37.123.2: icmp_seq=2 ttl=64 time=0.591 ms
^C
--- 10.37.123.2 ping statistics ---
2 packets transmitted, 2 received, 0% packet loss, time 1023ms
rtt min/avg/max/mdev = 0.524/0.557/0.591/0.040 ms
```

## Q7. Configure your border router such that even though you have a direct peering relation with one of your colleagues all the outbound traffic goes via your other colleague. Do the same for inbound traffic. Work together with one person being the source one person the middleperson and one the destination. It is sufficient to do the exercise once from this perspective. Are you in full control of the routing? Explain

no, we are in full control of outbound traffic: we can select the paths to take, but we can only influence the inbound traffic, based on prefix length and the path lengths between other ASes which we are not in control of

- local preference for outbound traffic:

```
router bgp 64640
 bgp router-id 172.16.0.40
 neighbor 172.16.0.26 remote-as 64626
 neighbor 172.16.0.37 remote-as 64637
 !
 address-family ipv4 unicast
  network 10.40.0.0/16
  neighbor 172.16.0.26 next-hop-self
  neighbor 172.16.0.26 route-map RMAPlow in
  neighbor 172.16.0.37 next-hop-self
  neighbor 172.16.0.37 route-map RMAPhigh in
 exit-address-family
!
route-map RMAPhigh permit 20
 set local-preference 20
!
route-map RMAPlow permit 10
 set local-preference 10
```

```
router2# show bgp detail
BGP table version is 3, local router ID is 172.16.0.40, vrf id 0
Default local pref 100, local AS 64640
Status codes:  s suppressed, d damped, h history, * valid, > best, = multipath,
               i internal, r RIB-failure, S Stale, R Removed
Nexthop codes: @NNN nexthop's vrf id, < announce-nh-self
Origin codes:  i - IGP, e - EGP, ? - incomplete

   Network          Next Hop            Metric LocPrf Weight Path
*> 10.26.0.0/16     172.16.0.37                    20      0 64637 64626 i
*                   172.16.0.26              0     10      0 64626 i
*> 10.37.0.0/16     172.16.0.37              0     20      0 64637 i
*> 10.40.0.0/16     0.0.0.0                  0         32768 i

Displayed  3 routes and 4 total paths
```

- inbound traffic

```
router bgp 64640
 bgp router-id 172.16.0.40
 neighbor 172.16.0.26 remote-as 64626
 neighbor 172.16.0.37 remote-as 64637
 !
 address-family ipv4 unicast
  network 10.40.0.0/16
  neighbor 172.16.0.26 next-hop-self
  neighbor 172.16.0.37 next-hop-self
  neighbor 172.16.0.37 route-map LONGAS out
 exit-address-family
!
router ospf
 log-adjacency-changes detail
 network 10.66.123.0/24 area 0.0.0.0
!
route-map LONGAS permit 10
 set as-path prepend 64640 64640 64640 64640
```

```
router2# show bgp detail
BGP table version is 5, local router ID is 172.16.0.40, vrf id 0
Default local pref 100, local AS 64640
Status codes:  s suppressed, d damped, h history, * valid, > best, = multipath,
               i internal, r RIB-failure, S Stale, R Removed
Nexthop codes: @NNN nexthop's vrf id, < announce-nh-self
Origin codes:  i - IGP, e - EGP, ? - incomplete

   Network          Next Hop            Metric LocPrf Weight Path
*> 10.26.0.0/16     172.16.0.37                            0 64637 64626 i
*                   172.16.0.26              0             0 64626 64626 64626 i
*> 10.37.0.0/16     172.16.0.37              0             0 64637 i
*> 10.40.0.0/16     0.0.0.0                  0         32768 i

Displayed  3 routes and 4 total paths
```

## Q8. Document the current BGP routing table. Now choose a colleague from another group and setup peering with him. Write the peering setup on the board so that the rest of the class knows who is peering with whom. Describe the peering relations after you've added the new peer. What new routes did you get? How did the AS Path lengths change?

peer with Tjeerd, got almost every other route in the class, some very long paths

{{:2019-2020:students:sean_liao:inr:inr7.jpg?direct&400}}

```
router2# show bgp detail
BGP table version is 5, local router ID is 172.16.0.40, vrf id 0
Default local pref 100, local AS 64640
Status codes:  s suppressed, d damped, h history, * valid, > best, = multipath,
               i internal, r RIB-failure, S Stale, R Removed
Nexthop codes: @NNN nexthop's vrf id, < announce-nh-self
Origin codes:  i - IGP, e - EGP, ? - incomplete

   Network          Next Hop            Metric LocPrf Weight Path
*> 10.26.0.0/16     172.16.0.37                            0 64637 64626 i
*> 10.37.0.0/16     172.16.0.37              0             0 64637 i
*> 10.40.0.0/16     0.0.0.0                  0         32768 i

Displayed  3 routes and 3 total paths
```

```
router2# show ip route
Codes: K - kernel route, C - connected, S - static, R - RIP,
       O - OSPF, I - IS-IS, B - BGP, E - EIGRP, N - NHRP,
       T - Table, v - VNC, V - VNC-Direct, A - Babel, D - SHARP,
       F - PBR, f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

B>* 10.26.0.0/16 [20/0] via 172.16.0.37, eth0, 00:05:38
B>* 10.37.0.0/16 [20/0] via 172.16.0.37, eth0, 00:03:25
C>* 10.40.123.0/24 is directly connected, eth1, 00:09:38
C>* 172.16.0.0/24 is directly connected, eth0, 00:09:38
```

### extra peer

```
router2# show bgp detail
BGP table version is 60, local router ID is 172.16.0.40, vrf id 0
Default local pref 100, local AS 64640
Status codes:  s suppressed, d damped, h history, * valid, > best, = multipath,
               i internal, r RIB-failure, S Stale, R Removed
Nexthop codes: @NNN nexthop's vrf id, < announce-nh-self
Origin codes:  i - IGP, e - EGP, ? - incomplete

   Network          Next Hop            Metric LocPrf Weight Path
*> 10.14.0.0/16     172.16.0.14              0             0 64614 i
*                   172.16.0.37                            0 64637 64633 64614 i
*> 10.16.0.0/16     172.16.0.14                            0 64614 64624 64616 i
*                   172.16.0.37                            0 64637 64633 64617 64616 i
*  10.17.0.0/16     172.16.0.14                            0 64614 64633 64617 i
*>                  172.16.0.37                            0 64637 64633 64617 i
*  10.18.0.0/16     172.16.0.37                            0 64637 64633 64614 64618 i
*>                  172.16.0.14                            0 64614 64618 i
*> 10.19.0.0/16     172.16.0.14                            0 64614 64619 i
*                   172.16.0.37                            0 64637 64633 64614 64619 i
*> 10.23.0.0/24     172.16.0.14                            0 64614 64624 64623 i
*                   172.16.0.37                            0 64637 64633 64614 64624 64623 i
*> 10.24.0.0/16     172.16.0.14                            0 64614 64624 i
*                   172.16.0.37                            0 64637 64633 64617 64616 64624 i
*  10.26.0.0/16     172.16.0.14                            0 64614 64633 64637 64626 i
*>                  172.16.0.37                            0 64637 64626 i
*  10.33.0.0/16     172.16.0.14                            0 64614 64633 i
*>                  172.16.0.37                            0 64637 64633 i
*  10.37.0.0/16     172.16.0.14                            0 64614 64633 64637 i
*>                  172.16.0.37              0             0 64637 i
*> 10.40.0.0/16     0.0.0.0                  0         32768 i
*> 10.41.0.0/16     172.16.0.14                            0 64614 64641 i
*                   172.16.0.37                            0 64637 64633 64614 64641 i
*> 10.42.0.0/16     172.16.0.14                            0 64614 64642 i
*                   172.16.0.37                            0 64637 64633 64642 i
*> 10.44.0.0/16     172.16.0.14                            0 64614 64646 64644 i
*                   172.16.0.37                            0 64637 64633 64614 64646 64644 i
*> 10.46.0.0/16     172.16.0.14                            0 64614 64646 i
*                   172.16.0.37                            0 64637 64633 64614 64646 i
*> 10.56.0.0/16     172.16.0.14                            0 64614 64646 64644 64656 i
*                   172.16.0.37                            0 64637 64633 64614 64646 64644 6465
6 i
*> 172.16.0.0/24    172.16.0.14                            0 64614 64646 i
*                   172.16.0.37                            0 64637 64633 64614 64646 i

Displayed  17 routes and 33 total paths
```

```
router2# show ip route
Codes: K - kernel route, C - connected, S - static, R - RIP,
       O - OSPF, I - IS-IS, B - BGP, E - EIGRP, N - NHRP,
       T - Table, v - VNC, V - VNC-Direct, A - Babel, D - SHARP,
       F - PBR, f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

B>* 10.14.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.16.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.17.0.0/16 [20/0] via 172.16.0.37, eth0, 00:01:49
B>* 10.19.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.23.0.0/24 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.24.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.26.0.0/16 [20/0] via 172.16.0.37, eth0, 00:08:35
B>* 10.33.0.0/16 [20/0] via 172.16.0.37, eth0, 00:01:49
B>* 10.37.0.0/16 [20/0] via 172.16.0.37, eth0, 00:06:22
C>* 10.40.123.0/24 is directly connected, eth1, 00:12:35
B>* 10.41.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.42.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.44.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.46.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B>* 10.56.0.0/16 [20/0] via 172.16.0.14, eth0, 00:00:09
B   172.16.0.0/24 [20/0] via 172.16.0.14 inactive, 00:00:09
C>* 172.16.0.0/24 is directly connected, eth0, 00:12:35
```

## Q9. Setup peering with 172 16 0 42/AS64642 This BGP peer has been allocated the IP block 10 42 0 0/16 but it also advertises other ranges. Configure your BGP router such that it accepts only valid routes from this peer

```
router bgp 64640
 bgp router-id 172.16.0.40
 neighbor 172.16.0.14 remote-as 64614
 neighbor 172.16.0.26 remote-as 64626
 neighbor 172.16.0.37 remote-as 64637
 neighbor 172.16.0.42 remote-as 64642
 !
 address-family ipv4 unicast
  network 10.40.0.0/16
  neighbor 172.16.0.14 next-hop-self
  neighbor 172.16.0.26 next-hop-self
  neighbor 172.16.0.37 next-hop-self
  neighbor 172.16.0.42 next-hop-self
  neighbor 172.16.0.42 prefix-list ALLOW42 in
 exit-address-family
!
router ospf
 log-adjacency-changes detail
 network 10.66.123.0/24 area 0.0.0.0
!
ip prefix-list ALLOW42 seq 3 permit 10.42.0.0/16
ip prefix-list ALLOW42 seq 5 deny any
!
line vty
!
end
```

```
router2# show ip route
Codes: K - kernel route, C - connected, S - static, R - RIP,
       O - OSPF, I - IS-IS, B - BGP, E - EIGRP, N - NHRP,
       T - Table, v - VNC, V - VNC-Direct, A - Babel, D - SHARP,
       F - PBR, f - OpenFabric,
       > - selected route, * - FIB route, q - queued route, r - rejected route

B>* 10.14.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.16.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.17.0.0/16 [20/0] via 172.16.0.37, eth0, 00:01:05
B>* 10.18.0.0/16 [20/0] via 172.16.0.26, eth0, 00:01:05
B>* 10.19.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.23.0.0/24 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.24.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.25.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.26.0.0/16 [20/0] via 172.16.0.26, eth0, 00:01:05
B>* 10.33.0.0/16 [20/0] via 172.16.0.37, eth0, 00:01:05
B>* 10.37.0.0/16 [20/0] via 172.16.0.37, eth0, 00:01:05
C>* 10.40.123.0/24 is directly connected, eth1, 00:01:07
B>* 10.41.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.42.0.0/16 [20/0] via 172.16.0.42, eth0, 00:01:04
B>* 10.44.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.46.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B>* 10.56.0.0/16 [20/0] via 172.16.0.14, eth0, 00:01:02
B   172.16.0.0/24 [20/0] via 172.16.0.14 inactive, 00:01:02
C>* 172.16.0.0/24 is directly connected, eth0, 00:01:07
```

```
router2# show bgp detail
BGP table version is 30, local router ID is 172.16.0.40, vrf id 0
Default local pref 100, local AS 64640
Status codes:  s suppressed, d damped, h history, * valid, > best, = multipath,
               i internal, r RIB-failure, S Stale, R Removed
Nexthop codes: @NNN nexthop's vrf id, < announce-nh-self
Origin codes:  i - IGP, e - EGP, ? - incomplete

   Network          Next Hop            Metric LocPrf Weight Path
*> 10.14.0.0/16     172.16.0.14              0             0 64614 i
*                   172.16.0.37                            0 64637 64633 64614 i
*                   172.16.0.26                            0 64626 64618 64614 i
*> 10.16.0.0/16     172.16.0.14                            0 64614 64624 64616 i
*                   172.16.0.26                            0 64626 64618 64614 64624 64616 i
*                   172.16.0.37                            0 64637 64633 64617 64616 i
*  10.17.0.0/16     172.16.0.14                            0 64614 64633 64617 i
*                   172.16.0.26                            0 64626 64637 64633 64617 i
*>                  172.16.0.37                            0 64637 64633 64617 i
*  10.18.0.0/16     172.16.0.14                            0 64614 64618 i
*>                  172.16.0.26                            0 64626 64618 i
*                   172.16.0.37                            0 64637 64626 64618 i
*> 10.19.0.0/16     172.16.0.14                            0 64614 64625 64619 i
*                   172.16.0.26                            0 64626 64618 64625 64619 i
*                   172.16.0.37                            0 64637 64633 64614 64625 64619 i
*> 10.23.0.0/24     172.16.0.14                            0 64614 64624 64623 i
*                   172.16.0.26                            0 64626 64618 64614 64624 64623 i
*                   172.16.0.37                            0 64637 64633 64614 64624 64623 i
*> 10.24.0.0/16     172.16.0.14                            0 64614 64624 i
*                   172.16.0.26                            0 64626 64618 64614 64624 i
*                   172.16.0.37                            0 64637 64626 64618 64614 64624 i
*> 10.25.0.0/16     172.16.0.14                            0 64614 64625 i
*                   172.16.0.26                            0 64626 64618 64625 i
*                   172.16.0.37                            0 64637 64626 64618 64625 i
*  10.26.0.0/16     172.16.0.14                            0 64614 64618 64626 i
*>                  172.16.0.26              0             0 64626 i
*                   172.16.0.37                            0 64637 64626 i
*  10.33.0.0/16     172.16.0.14                            0 64614 64633 i
*                   172.16.0.26                            0 64626 64637 64633 i
*>                  172.16.0.37                            0 64637 64633 i
*  10.37.0.0/16     172.16.0.14                            0 64614 64633 64637 i
*                   172.16.0.26                            0 64626 64637 i
*>                  172.16.0.37              0             0 64637 i
*> 10.40.0.0/16     0.0.0.0                  0         32768 i
*> 10.41.0.0/16     172.16.0.14                            0 64614 64641 i
*                   172.16.0.26                            0 64626 64618 64614 64641 i
*                   172.16.0.37                            0 64637 64633 64614 64641 i
*  10.42.0.0/16     172.16.0.14                            0 64614 64642 i
*>                  172.16.0.42              0             0 64642 i
*                   172.16.0.26                            0 64626 64642 i
*                   172.16.0.37                            0 64637 64626 64642 i
*> 10.44.0.0/16     172.16.0.14                            0 64614 64646 64644 i
*                   172.16.0.26                            0 64626 64618 64614 64646 64644 i
*                   172.16.0.37                            0 64637 64633 64614 64646 64644 i
*> 10.46.0.0/16     172.16.0.14                            0 64614 64646 i
*                   172.16.0.26                            0 64626 64618 64614 64646 i
*                   172.16.0.37                            0 64637 64633 64614 64646 i
*> 10.56.0.0/16     172.16.0.14                            0 64614 64646 64644 64656 i
*                   172.16.0.26                            0 64626 64618 64614 64646 64644 64656 i
*                   172.16.0.37                            0 64637 64633 64614 64646 64644 64656 i
*> 172.16.0.0/24    172.16.0.14                            0 64614 64646 i
*                   172.16.0.26                            0 64626 64618 64614 64646 i
*                   172.16.0.37                            0 64637 64633 64614 64646 i

Displayed  18 routes and 53 total paths
```

## Q10. Explain what the purpose of AS112 is

volunteer run DNS servers to blackhole queries for private address ranges

- https://www.as112.net/

## Q11. Perform a traceroute to 145 100 101 1 using a Web-based traceroute tool like https //www net princeton edu/traceroute html and note the ASs that are traversed. Now trace 145 100 104 1. Why does this trace take a different route although both addresses are part of OS3's 145 100 96 0/20 range?

It appears that the 145.100.101.1/24 subnet (OS3-AS) is directly connected to AMS-IX, while the rest of the OS3 network is connected through SURFnet, the more specific subnet is preferred over the larger one advertised by SURFnet

from Hurricane Electric Looking Glass:

```
BI	145.100.101.0/24	80.249.210.65	1355	100	0	1101, 1146	IGP	?
BI	145.100.0.0/15	195.66.225.122	1285	100	0	1103	IGP	?
```

trace: 104.100.101.1

```
  1    <1 ms   <1 ms   <1 ms 72.52.92.58      AS6939 HURRICANE - Hurricane Electric LLC, US
  2    83 ms   71 ms  153 ms 184.105.81.217
  3   244 ms  140 ms  158 ms 72.52.92.165
  4   140 ms  160 ms  164 ms 72.52.92.214
  5   174 ms  159 ms  141 ms 80.249.210.65    AS1200 AMS-IX1, NL
  6  1010 ms  178 ms  145 ms 145.100.101.1    AS1146 OS3-AS OS3, NL
```

trace: 104.100.104.1

```
  1   107 ms   18 ms    1 ms 72.52.92.58      AS6939 HURRICANE - Hurricane Electric LLC, US
  2    95 ms   65 ms   98 ms 184.105.81.217
  3   139 ms  159 ms  140 ms 72.52.92.165
  4   214 ms  186 ms  146 ms 195.66.225.122   AS5459 London interconnection point
  5   151 ms  252 ms  192 ms 145.100.104.1    AS1103 SURFNET-NL SURFnet, The Netherlands, NL
```

<spoiler|trace from machine in Google cloud>

from a VM in Google cloud:

- 145.100.101.1 (styx.os3.nl)

  - AS15169: GOOGLE
  - timeout

- 145.100.104.1 (router.studexp.nl)

  - AS15169: GOOGLE
  - timeout
  - AS1103: SURFnet
  - timeout

```
traceroute to 145.100.101.1 (145.100.101.1), 30 hops max, 60 byte packets
 1  216.239.58.255 (216.239.58.255)  27.137 ms  27.205 ms 216.239.57.137 (216.239.57.137)  27.081 ms
 2  172.253.65.175 (172.253.65.175)  95.029 ms  95.068 ms 216.239.41.48 (216.239.41.48)  100.670 ms
 3  108.170.237.242 (108.170.237.242)  100.030 ms 172.253.65.167 (172.253.65.167)  95.112 ms 209.85.142.166 (209.85.142.166)  100.694 ms
 4  209.85.255.231 (209.85.255.231)  100.687 ms  100.101 ms 209.85.245.230 (209.85.245.230)  99.822 ms
 5  108.170.241.166 (108.170.241.166)  100.603 ms  100.122 ms 108.170.241.198 (108.170.241.198)  100.474 ms
 6  * * 108.170.241.134 (108.170.241.134)  98.927 ms
 7  * * *
 8  * * *
 9  * * *
10  * * *
11  * * *
12  * * *
13  * * *
14  * * *
15  * * *
16  * * *
17  * * *
18  * * *
19  * * *
20  * * *
21  * * *
22  * * *
23  * * *
24  * * *
25  * * *
26  * * *
27  * * *
28  * * *
29  * * *
30  * * *
```

```
arccy@instance-1:~$ traceroute 145.100.104.1
traceroute to 145.100.104.1 (145.100.104.1), 30 hops max, 60 byte packets
 1  * 216.239.58.255 (216.239.58.255)  27.722 ms *
 2  172.253.65.175 (172.253.65.175)  96.084 ms 172.253.65.177 (172.253.65.177)  96.078 ms 172.253.65.175 (172.253.65.175)  95.458 ms
 3  108.170.237.242 (108.170.237.242)  100.031 ms 209.85.142.166 (209.85.142.166)  100.909 ms 108.170.234.118 (108.170.234.118)  101.085 ms
 4  216.239.42.210 (216.239.42.210)  100.306 ms * *
 5  108.170.241.139 (108.170.241.139)  100.160 ms 108.170.241.203 (108.170.241.203)  99.740 ms  99.825 ms
 6  * * *
 7  lo0-2.asd002a-jnx-01-sn7-internet.surf.net (145.145.128.2)  101.156 ms  101.626 ms  101.635 ms
 8  * * *
 9  * * *
10  * * *
11  * * *
12  * * *
13  * * *
14  * * *
15  * * *
16  * * *
17  * * *
18  * * *
19  * * *
20  * * *
21  * * *
22  * * *
23  * * *
24  * * *
25  * * *
26  * * *
27  * * *
28  * * *
29  * * *
30  * * *
```

</spoiler>
