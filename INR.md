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
- Ipv4 protocols
  - 6 TCP
  - 17 UDP

## packets

### Ipv4

```
|           |           |           |           |           |           |           |           |
| version   | header len| type of service       | total length                                  |
| identifier                                    | flags  | fragment offset                      |
| time to live          | protocol              | header checksum                               |
| source ip                                                                                     |
| dest ip                                                                                       |
| options (padded)                                                                              |
```
