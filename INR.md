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
- Rapid Spanning Tree RSTP

### Layer 3 ipv4

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
