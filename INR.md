# Networks

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
- VLSM: variable length subnet mask
- CIDR: classless Inter Domain Routing

### Layer 2

- Link / LAN
  - Layer 1: physical layer, link segment, single collision domain
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
  - 802.1D
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
- frames:
  - 7preamble, 1sfd
  - DIX Ethernet II: 6dst, 6src, 2type, 46-1500pdu, 4fcs
  - 802.3 with 802.2: ...src, 2len, 1dsap, 1ssap, 1 ctrl, 43-1497pdu, 4fcs
  - 802.1Q-2011 VLAN: ...src, x8100, 2ctag (3flag,1cfi,12tag), 2len...
  - 802.1ad (QinQ): ..src, x88a8, 2stag, x8100, 2ctag, 2len...
  - 802.1ah PBB: 6pdst, 6psrc, x88a8, 2btag, x88e7, 2itag, ...802.1ad

### Layer 2 RSTP

- rapid spanning tree protocol
- bipartite graph
- graph to tree: add nodes without creating loops
- broadcast direct connected perceived root
  - id, cost, own id, own port
- chooses lowest root id
  - tiebreak: cost, trans bridge id, port
- designated bridge
  - designated port to LAN segment
  - root port points to root
- wait 2x forward delay to configure
  - listen / learn
- Topology Change Notification to bridge
  - Root set TC for forward delay + max age
  - use short cache: forward delay
- BDPU
  - DSAP = SSAP = 0x42
  - dest `01:80:c2:00:00:00` local broadcast
- RSTP
  - backwards compatible
  - extra flags for early forward
- STP on VLANs
- MSTP Multiple Spanning Tree Protocol
  - each VLAN/region = virtual bridge, Internal Spanning Tree
  - Common Spanning Tree between VLANs
- params:
  - message age: 1/256 sec, 0 on all except new connect
  - max age: discard (20s)
  - hello time: time between keepalives (2s)
  - forward delay: listen, learn time (15s)
- frames:
  - hello: 2proto, 1vers, 1type, 1flag (1tca, ..., 1tc), 8rootid, 4cost, 8bridgeid,
    - 2portid, 2message age, 2maxage, 2hellotime, 2forward delay
  - TC: 2proto, 1vers, 1type

### Layer 3 IPv4

- Classful IPv4: a.b.c.d
  - 1/2 `0-------`: 0..127 to class A/8
  - 1/4 `10------`: 128..191 to class B/16
  - 1/8 `110-----`: 192..223 to class C/24
  - 1/16 `1110----`: 224..239 to class D mulitcast
  - 1/16 `1111----`: 240..255 to class E reserved local broadcast
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
  - other:
    - 192.88.99.1: 6to4 anycast
    - 224.0.0.5: ALLSPFRouters multicast
    - 224.0.0.6: ALLDRouters multicast
    - 224.0.0.9: RIPv2 multicast
    - 224.0.0.10: EIGRP multicast
- subnets: area with same prefix
- link: ip ttl 1 reachable
- Ipv4 protocols: `6` tcp, `17` udp
- header:
  - 1(version, internet header (5-16)), 1tos, 2totallen, 2id, 2(3flags, 13fragoff)
  - 1ttl, 1proto, 2chksum, 4src, 4dst, 4options x N

### Layer 3 IPv6

- allocations
  - 0000 000- = ::/7 special purpose
    - ::/128 unspecified
    - ::1/128 loopback
    - ::a.b.c.d/128 IPv4 compatible (depreciated)
    - ::ffff:a.b.c.d/96 IPv4 mapped (RFC2765)
    - ::ffff:0:a.b.c.d/96 IPv4 translated (RFC2765)
    - 64:ff9b::/96 Well known prefix (IPv4 embedded) (RFC6052)
    - 64:ff9b:1::/48 local well known
    - 100::/8, 100::/64 discard only
  - 001- ---- = 2000::/3 global unicast
    - 2001::/16 1st RIR
    - 2002::/16 6to4
  - 1111 110- = fc00::/7 unique local unicast
  - 1111 1110 10-- = fe80::/10 link local unicast
  - 1111 1111 = ff00/8 multicast
    - 8x1 4flags (0RPT) 4scope 112multicast_id
    - scope: 1 interface, 2 link, e global
    - ff02::1 all nodes
    - ff02::2 all routers
    - ff02::5 OSPF all routers
    - ff02::5 OSPF all designated routers
- neighbour discovery
  - ICMPv6 133-137
  - SLAAC: generate link local - DAD - RA - generate - DAD
- transition
  - ipv4 XOR ipv6: use tunnels
  - Dual Stack
    - Stateless IP ICMP Translation SIIT
    - Bump in the Stack BIS
    - IPv6 Tunnel Broker
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
  - IPv4 embedded addresses (NAT64)
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
- header:
  - 4(4vers, 8class, 20flowlabel), 2payload len, 1next header (proto), 1hop limit, 16src, 16dst

### Layer 3 routing

- gateway = next hop
- longest prefix match selection
- Autonomous System: connected group with single clear routing policy
- Inter AS: BGP4
- Intra AS: RIP, OSPF, IS-IS, iBGP
- Static Routing: manual config
- Distance Vector: RIP, minimum spanning tree
- Path Vector: BGP, full path not just distance
- Link State: OSPF, know everything
- minimum spanning tree
  - least total cost
  - but not necessarily pairwise least cost
  - Prim
    - choose closest node,
    - single tree grows
  - Kruskal
    - choose globally least cost edge
    - merge forest -> tree

### Layer 3 Routing Information Protocol

- charles hedrik, 1988
- bellman-ford
  - limit
    - d0(s, t) = 0, s = t
    - d0(s, t) = inf, s != t
    - dn+1(s, t) = min i (dn(s, i), dit)
    - d(s, t) = lim n->inf dn(s, t)
    - d(s, t) = min i (d(s, i), dit)
  - shortest path from single source, handles negative weights
  - for all edges: check if reduces distance to node
- table of distance/metric, gateway/next hop
  - send out full table (no gateways), update own table
- small inf size, max 15 hops
- split horizon, poisoned reverse: no advertise back to network heard from
- Timers: Update 30s, Invalid (timeout) 180s, Flush 240s (60/120s after timeout), Hold down (no update on unreachable) 180s
- RIPv1: guess uniform subnet mask from self
- RIPv2
  - subnet mask, alt next hop, auth, multicast, route tags
  - mulitcast replace broadcast 224.0.0.9 not forwarded
  - next hop: speaking on behalf of router that doesn't speak RIPv2
- RIPng (next generation IPv6)
  - 521/udp
  - ipv6 prefix, route tags, next hop, multicast
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
- frames:
  - 520/udp, 512 bytes (8udp head, 4 head, 25x 20 route updates)
  - RIPv1: (1cmd, 1vers, 2reserved), (2afi, 2reserved, 4netaddr, 8reserved, 4metric) x max 20
    - cmd: 1req, 2res
  - RIPv2: (1cmd, 1vers, 2reserved), (2afi, 2route_tag, 4netaddr, 4subnet_mask, 4next_hot, 4metric) x max 20
    - first entry auth: afi=xffff, 2auth_type (2 plaintxt, 3 md5/hmac-sha), data (passwd or keyid, seq, len, offset of auth trailer)
    - route tag passed along
  - 521/udp, unfragmented
  - RIPng: (1cmd, 1vers, 2reserved), (16netaddr, 2route_tag, 1prefix_len, 1metric) x n unfragmented
    - next hop (next entry RTE, link local, use until reset): 16ipaddr, x0000, x00, xff

### Layer 3 OSPF

- link state
- dijkstra
  - shortest path tree: from single source
  - for all nodes: updated connected nodes, expand selected to closest connected
- features:
  - complex topologies, faster than distance vector (RIP)
  - subnets, metrics on cost per interface
  - load balancing, unnumbered interfaces, auth
  - proto 89, drop external routes on database overflow
- hello -> adjacent (neighbors = connected, adjacent = connected in protocol)
  - link state packets for point to point
  - virtual nodes / designated / backups for broadcast networks
  - change -> trigger update, default period LSRefreshTime: 30min
  - smart flood (no resend seen)
- out of order packets:
  - ttl always decrease when passing router, decrease in time
  - large seq space, lockup on max, wait for timeout, force timeout with maxage flood
- prototype 89
- Link State Advertisements (not packets)
- HelloInterval 10s, RouterDeadInterval 40s
- ACK LSA, timeout at same time with trigered redistribution using MaxAge
- sticky router election, AllDRouters 224.0.0.6, AllSPFRouters 224.0.0.5
- 2 tier, layer 0 backbone, extended with virtual links
- Inter Area Summary IAS (aggregation of area), forwarded directly to other areas
- Autonomous System Boundary Router, other protos, external routes
- Areas:
  - Stubby: no external routes, default route injected by Area Border Routers ABR
  - Totally Stubby: only default, no IAS
  - Not So Stubby: with certain external info
- OSPFv3
  - IPv6, no auth, addrs in LSA, works per link not subnet
  - explicit flooding scope (link, area, AS)
  - router/network LSA: topology connectivity only (no ips)
  - link LSA: router link local addr, ip6 prefix, options for network LSA
  - intra area prefix LSA: prefix info
  - inter area prefix LSA: type 3 summary
  - inter area router LSA: type 4 summary
- frames:

  - header: 1vers, 1type, 2pktlen, 4routerid, 4areaid, 2chksum, 2authtype (null, simple, cryto), 8auth (ptr)
  - type 1 Hello: (4netmask, 2hello_int, 1opts, 1rtr_pri, 4rdead_int, 4DR ip, 4BDR ip), 4neighbor x N
  - type 2 Database Desc: (2mtu, 1opts, 00000_init_more_master/slave, 4seq), 20LSA_header x N
    - unique LSA instance
  - type 3 LS Request: (4LS_type, 4LS_id, 4adv_router) x N
    - unique LSA
  - type 4 LS Update: 4LSA_cnt, LSA x N
    - LSA header: 2LSA_age, 1opts, 1LS_type, 4LS_id, 4adv_router, 4LS_seq, 2chksum, 2len
    - type 1 Router LSA: (0_virtual_asbr_abr_0, 2link_cnt), (4link_id, 4link_data, 1type, 1tos_cnt, 2metric, (1tos, 1zero, 2tos_metric) x N us.0 ) x N
      - **by all routers**: describe self connected links, scope: area
      - LS_id = originating router id
      - type (link_id, link_data): 1ptp (neighbor id, orig ip), 2transit (DR ip, orig ip), 3stub (ip, mask), 4virtual (neighbor id, orig ip)
    - type 2 Network LSA: 4netmask, 4attached_router x N
      - **by DR**: describe routers in network, scope: area
      - LS_id = DR ip
    - type 3 Net Summary LSA: 4netmask, 1zero, 3metric, (1tos, 3tos_metric) x N
      - **by ABR**: area summary, scope: other areas
      - LS_id = net prefix dst
    - type 4 ASBR LSA: see type 3, netmask not relevant
      - **by ABR**: ASBR info, from type 1 with asbr bit set, scope: other areas
      - LS_id = ASBR router id
    - type 5 AS External LSA: 4netmask, 1e_zero, 3metric, 4fwd_addr, 4ext_route_tag, (1e_tos, 3tos_metric, 4fwd_addr, 4ext_route_tag) x N
      - **by ASBR**: external route summary, scope: all areas
      - LS_id = net prefix dst
      - e: external cost only
      - flood through AS
    - type 7 NotSoStubbyArea LSA: see type 5, flood through NSSA
      - **by ASBR**: external route summary, scope: NSSA, translated to type 5 by ABR
      - LS_id = net prefix dst
  - type 5 LS Ack: 20LSA_header x N

- http://www.ciscopress.com/articles/article.asp?p=2294214&seqNum=2

### Layer 3 BGP

- path vector, non coordinated
- speakers = routers
- earn money: customer > peer > provider
- AS types: stub (single provider), multi homed, transit, IXP
- transit relation export filtering: advertise: all to customers, customers to all
- eBGP between AS, iBGP internal sync routers, local routes, full mesh
- Adj-RIB-in : import policy : route selection : Loc-RIB : export policy : Adj-RIB-out
- TCP 179, TTL 1
- ORIGIN (unused), NEXT_HOP: advertise for someone else
- drop if own, weight, local_pref (high), as_path (short), origin (low), med (low), ebgp > ibgp
- tie break: IGP cost, oldest, router id, neighbor ip
- communities: optional transitive: preferred treatment, ex: NO_EXPORT, NO_ADVERTISE
- outbound routes/filters affect inbound traffic (peer policies)
  - Inbound Traffic Engineering: as path extension, med, outbound communities, spec routes
- inbound routes/filters affect outbound traffic (in control)
  - Outbound Traffic Engineering: local pref, as path extension, med inbound communities
- route reflector: smaller iBGP full mesh
- confederation: private AS, AS_PATH segment type
- Network Layer Reachability Information NLRI: prefix reachability, route select attrs
- frames:
  - 16bytes1 (old auth), 2len (19-4096), 1type, data
  - type 1: OPEN
    - 1vers, 2AS, 2holdtime, 4bgpid (sender ip), 1optparamlen, optparams
  - type 2: UPDATE
    - 2unfeasible_route_len, withdrawn_routes, 2total_path_attr_len, path_attr (tlv), NLRI
  - type 3: NOTIFICATION
    - 1errcode, 1errsubcode, data
  - type 4: KEEPALIVE
    - empty body
  - type 5: Route-REFRESH
    - 2afi (address family indicator, multiproto), 1reserved, 1safi (subsequent afi, uni/multicast)
