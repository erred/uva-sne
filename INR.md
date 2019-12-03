# InterNetworking and Routing

- Layer 2 (Link) and 3 (Network)
- Interface (Vertical), Protocol (Horizontal)
  - Service Data Unit (SDU), Protocol Data Unit (PDU)
- Encapsulation of higher layers: add control headers
- routing (all layers) multiphop to address
- Address (location of x) v Endpoints (thing we want to talk to)
  - Identifier Locator Network Protocol ILNP:
    - DNS lookup identifier and locator
    - Reuse IP addr for loc (ip4: 32, ip6: 64)
    - 64bit identifier
  - Locator Identifier Separation Protocol LISP
    - Core Edge separation, overload ip addrs
    - Endpoint IDs and Routing LOCators
  - Host Identity Protocol v2 HIPv2
    - DNS lookup pubkey identifier
    - IPSec between pubkeys (host identifiers)
- Strong v Weak Endsystem
  - Strong: only accept match physical interface
  - Weak: accept and route any

### Layer 1

- physical layer
- link segment
- single collision domain

### Layer 2

- Link / LAN
- single broadcast domain
- Media Access Control MAC sublayer
  - Ethernet: 802.3
  - Carrier Sense Multiple Access Collision Detection CSMA/CD
  - 48bit addr MAC-48 -> EUI-48 -> EUI-64 (insert `ff:fe`, ipv6 also flip local/universal)
  - 3 bytes Organizationally Unique Identifier
  - of first byte, starting least significant bit
  - bit 1: unicast / multicast
  - bit 2: universal / local
  - Ethertypes: `0x0800` ipv4, `0x0806` arp, `0x86dd` ipv6
- Logical Link Control
  - useless 802.2
  - wasted bytes
  - Sub Network Access Protocol SNAP: `0xaa` DSAP, `0xaa` SSAP `0x03` control, `0x000000` org id, `2 byte ethertype` protocol
  - Bridges == Switches
    - filtering database: learning
  - VLAN: split physical LAN to logical LANs
    - 802.1Q-2011, includes everything below
      - insert 4 byte header after src addr
    - 802.1ad QinQ
      - additonal service provider vlan tag before customer vlan tag
    - 802.1ah Provider Backbone Bridges PBB
      - additional src, dst, ethertype, service id header
    - 802.1Qay PBB-Traffic Engineering
      - alt to MPLS-Transport Profile / Transport-MPLS
    - ... stop inclusion ...
    - TRansparent Interconnect of Lots of Links TRILL
      - Layer 2 routing bridges on LAN with L3 like headers / TTL
    - Shortest Path Bridging SPB
      - replace STP / RSTP / MSTP
      - QinQ, MinM

### Layer 2 RSTP

- rapid spanning tree protocol
- bipartite graph
- graph to tree
- add nodes without creating loops
- broadcast direct connected perceived root
  - id, cost, own id, own port
- chooses lowest root id
  - best path
  - best bridge
  - best port
- designated bridge
  - designated port to LAN segment
  - root port points to root
- wait 2x forward delay to configure
  - listen / learn
- Topology Change Notification to bridge
  - Root set TC for forward delay + max age
- BDPU
  - DSAP = SSAP = 0x42
  - dest `01:80:c2:00:00:00` local broadcast
- RSTP
  - backwards compatible
  - extra flags for early forward
  - forward early on some
- STP on VLANs
- disable host port participation
- MSTP Multiple Spanning Tree Protocol
  - each VLAN/region = virtual bridge, Internal Spanning Tree
  - Common Spanning Tree between VLANs

### Layer 3 IPv4

- Classful IPv4: a.b.c.d
  - 1/2 a: 0..127 to class A/8
  - 1/4 a: 128..191 to class B/16
  - 1/8 a: 192..223 to class C/24
  - 1/16 a: 224..239 to class D mulitcast
  - 1/15 a: 240..255 to class E reserved local broadcast
- IPv4 special
  - 0.0.0.0 unkown IP
  - 127.0.0.1 loopback
  - Network id: host all 0
  - Network broadcast: host all 1
  - 255.255.255.255 local broadcast
  - Private:
    - 10.0.0.0/8
    - 172.16.0.0/12
    - 192.168.0.0/16
    - 169.254.0.0/16 link local dynamic config
- subnets: area with same prefix
- link: ip ttl 1 reachable
- Ipv4 protocols: `6` tcp, `17` udp

### Layer 3 IPv6

- allocations
  - 0000 000- = ::/7 special purpose
  - 001- ---- = 2000::/3 global unicast
  - 1111 110- = fc00::/7 unique local unicast
  - 1111 1110 10-- = fe80::/10 link local unicast
  - 1111 1111 = ff00/8 multicast
- special purpose
  - ::/128 unspecified
  - ::1/128 loopback
  - ::a.b.c.d/128 IPv4 compatible (depreciated)
  - ::ffff:0:0/96 IPv4 mapped
  - 100::/8, 100::/64 discard only
  - 64:ff9b::/96 Well known prefix (IPv4 algorithmic translation)
  - 64:ff9b:1::/48 local well known
- unicast
  - 2001::/16 1st RIR
  - 2002::/16 6to4
- local
  - fe80::/10 link local
  - fec0::/10 site local (depreciated)
  - fc00::/7 unique local
- multicast
  - ff00::/8
    - 8x1 4flags (0RPT) 4scope 112multicast_id
    - scope: 1 interface, 2 link, e global
    - ff02::1 all nodes
    - ff02::2 all routers
- neighbour discovery
  - ICMPv6 133-137
  - SLAAC
    - Link local - DAD
    - RA - generate - DAD
- transition
  - ipv4 XOR ipv6: use tunnels
  - Dual Stack
    - Stateless IP ICMP Translation SIIT
    - Bump in the Stack BIS
    - IPv6 Tunnel Brokeer
    - SOCKS IPv6/IPv4 Gateway
    - Transport Relay Translator TRT
    - Bump in the API BIA
    - **Bump in the Host BIS**
    - **IP/ICMP translation (SIIT derived)**
    - **6rd, IPv6 Rapid Deployment, 6to4 in ISP**
    - **DS-Lite Dual Stack Lite, ISP use tunnels and CGNAT for IPv4**
    - **NAT64** and **DNS64**
    - **6to4**
    - **ISATAP** and **Teredo**
  - IPv4 embedded addresses:
    - IPv4 converted: an ip4 address in a ip6 address (full map)
    - IPv4 translatable: an ip6 address in a ip4 address (partial map)
    - prefix - ipv4 addr - suffix
      - bits 64-71 all 0
      - suffix all 0
  - NAT64
    - header translation
    - 4 to 6 algorithmic, 6 to 4 stateful table
    - DNS64 address translation from v6 client to v4 server
  - ISATAP Intra Site Automatic Tunnel Addressing Protocol
    - 64_bit_prefix:0:5efe:a.b.c.d
    - 0:5efe constant
  - 6to4
    - connects IPv6 over IPv4
    - 2002:a.b.c.d::/48
    - well known IPv4 anycast 192.88.99.1
  - Teredo
    - 2001::/32 prefix
    - 2001:0000:ssss:ssss:ffff:pppp:cccc:cccc
      - s: server
      - f: flags
      - p: obfuscated NAT UDP port
      - c: obfuscated NAT client addr
  - DNS
    - AAAA
    - PTR ugly
    - .......ip6.arpa IN PTR ...
  - Other
    - inet_pton, inet_ntop
    - gethostbyname -> getaddrinfo
    - gethostbyaddr -> getnameinfo

### Algorithms

- Bellman-Ford
  - shortest path tree
    - from single source
    - handles negative weights
  - for all edges:
    - check if reduces distance to node
  - Split horizon / poisoned reverse
- Dijkstra
  - shortest path tree
    - from single source
  - for all nodes:
    - updated connected nodes
    - choose closest
- Prim
  - minimum spanning tree
    - least total cost
    - but not necessarily pairwise least cost
  - for all nodes
    - choose closest node
- Kruskal
  - minimum spanning tree
  - fpr all nodes
    - choose globally least cost edge without creating cycle

### Layer 3 routing

- gateway = next hop
- longest prefix match selection
- Autonomous System: connected group with single clear routing policy
- Inter AS
  - BGP4
- Intra AS
  - RIP, OSPF, IS-IS, iBGP
- Static Routing: manual config
- Distance Vector:
  - RIP
  - minimum spanning tree
- Path Vector:
  - BGP
  - full path not just distance
- Link State:
  - OSPF
  - know everything

### Layer 3 RIP

- charles hedrik, 1988
- bellman-ford
- small size, max 15 hops
- send out full table (no gateways), update own table
- split horizon, poisoned reverse: no advertise back to network heard from
- Timers: Update 30s, Invalid (timeout) 180s, Flush 240s (120s after timeout), Hold down (no update on unreachable) 180s
- 520/udp
- v1 supports fixed 1 level deep subnets
- Interior Gateway Routing Protocol IGRP:
  - Cisco
  - 4 parallel paths, more hops (100)
  - more route types: internal, system, exterior
  - more default routes
  - multi metric (hop count, delay, bandwidth, reliability, load, mtu)
- Enhanced Interior Gateway Protocol EIGRP
  - Cisco
  - all paths
  - explicit subnet VLSM
  - Diffusing Update ALgorithm DUAL:
    - loop free faster convergence
    - similar to RSTP
    - switch to backup when next path (minus direct link) cost less than original full path
  - 224.0.0.10 multicast discovery
  - no full periodic updates, partial and incremental only
- RIPv2
  - subnet mask, alt next hop, auth, multicast, route tags
  - mulitcast replace broadcast 224.0.0.9 not forwarded
  - next hop: speaking on behalf of router that doesn't speak RIPv2
- RIPng (next generation IPv6)
  - 521/udp
  - ipv6 prefix, route tags, next hop, multicast

### Layer 3 OSPF

- link state
- dijkstra
- complex topologies, faster than distance vector (RIP)
- hello -> adjacent
  - link state packets for point to point
  - virtual nodes / designated / backups for broadcast networks
  - change -> trigger update, default period: 30min
  - smart flood (no resend seen)
- out of order:
  - ttl always decrease when passing router
  - large seq space, lockup on max, wait for timeout
- prototype 89
- Link State Advertisements (not packets)
- HelloInterval, RouterDeadInterval
- ACK LSA, timeout at same time with trigered redistribution using MaxAge
- sticky router election, AllDRouters 224.0.0.6, AllSPFRouters 224.0.0.5
- 2 tier, layer 0 backbone, extended with virtual links
- Inter Area Summary IAS (aggregation of area), forwarded directly to other areas
- Autonomous System Boundary Router, other protos, external routes
- Areas:
  - Stubby: no external routes, default route injected by Area Border Routers ABR
  - Totally Stubby: only default, no IAS
  - Not So Stubby: with certain external info
- IPv6
  - OSPFv3
  - works per link not subnet
  - link-LSA and explicit flooding scope (link, area, AS)
  - intra area prefix LSA: prefix info
  - inter area prefix LSA: type 3 summary
  - inter area router LSA: type 4 summary

### Layer 3 BGP

## packets

### Ethernet DIX

```
|           |           |           |           |           |           |           |           |
| dest addr                                                                                     |
|                                               | src addr                                      |
|                                                                                               |
| type                                          | ... padded data min 46 bytes max to 1500      |
| Frame check sequence / cyclic redundancy check                                                |
```

### Ethernet 802.3 with 802.3 LLC

```
|           |           |           |           |           |           |           |           |
| dest addr                                                                                     |
|                                               | src addr                                      |
|                                                                                               |
| length                                        | DSAP                   | SSAP                 |
| control / ethertype   | ...padded min 43...                                                   |
| FCS                                                                                           |
```

### Ethernet 802.1 VLAN

```
|           |           |           |           |           |           |           |           |
| dest addr                                                                                     |
|                                               | src addr                                      |
|                                                                                               |
| VLAN type 0x8100                              ||priority|CFI| VLAN ID                         |
| length                                        | DSAP                   | SSAP                 |
| control / ethertype   | ...padded...                                                          |
| FCS                                                                                           |
```

### Ethernet 802.1ah PBB + 802.1ad

```
|           |           |           |           |           |           |           |           |
| dest addr                                                                                     |
|                                               | src addr                                      |
|                                                                                               |
| B-Tag VLAN type 0x88a8                        ||priority|CFI| VLAN ID                         |
| I-Tag type 0x88e7                             | flags                 | service identifier    |
|                                               | dest addr                                     |
| src addr                                                                                      |
|                                               |...............skip............................|
| VLAN type 0x88a8                              ||priority|CFI| VLAN ID                         |
| VLAN type 0x8100                              ||priority|CFI| VLAN ID                         |
| length                                        | DSAP                   | SSAP                 |
| control / ethertype   | ...padded...                                                          |
| FCS                                                                                           |
```

### STP BDPU

todo

### Ipv4

```
|           |           |           |           |           |           |           |           |
| version   | header len| tos / DSCP      | ECN | total length                                  |
| identifier                                    | flags  | fragment offset                      |
| time to live          | protocol              | header checksum                               |
| source ip                                                                                     |
| dest ip                                                                                       |
| options (padded)                                                                              |
```

### IPv6

```
|           |           |           |           |           |           |           |           |
|version | traffic class            | flowlabel                                                 |
| payload length (inc ext)                      | next header type      | hop limit             |
| src                                                                                           |
|                                                                                               |
|                                                                                               |
|                                                                                               |
| dst                                                                                           |
|                                                                                               |
|                                                                                               |
|                                                                                               |
```
