# Network Tools

## Q1. Find out what network cards your server has To what type of computer expansion bus are they connected? What is the speed of this interconnecting bus in mebibytes per second? Hint lspci

- `lspci | grep Ethernet`: list PCI(e) devices, filter for ethernet
  - Broadcom NetXtreme® BCM5720 Dual-Port 1GBASE-T PCIe 2.1 Ethernet Controller
- PCIe: peripheral component interconnect express
- Rate: 1 (width) x 5.0 GT/s x 8/10 (encoding) = 500 MB/s = 477 MiB/s

<spoiler|lspci>

```
arccy@nevers» lspci | grep Ethernet
03:00.0 Ethernet controller: Broadcom Inc. and subsidiaries NetXtreme BCM5720 Gigabit Ethernet PCIe
03:00.1 Ethernet controller: Broadcom Inc. and subsidiaries NetXtreme BCM5720 Gigabit Ethernet PCIe
```

<spoiler|lspci -vv>

```
03:00.0 Ethernet controller: Broadcom Inc. and subsidiaries NetXtreme BCM5720 Gigabit Ethernet PCIe
	Subsystem: Dell NetXtreme BCM5720 Gigabit Ethernet PCIe
	Control: I/O- Mem+ BusMaster+ SpecCycle- MemWINV- VGASnoop- ParErr- Stepping- SERR- FastB2B- DisINTx+
	Status: Cap+ 66MHz- UDF- FastB2B- ParErr- DEVSEL=fast >TAbort- <TAbort- <MAbort- >SERR- <PERR- INTx-
	Latency: 0
	Interrupt: pin A routed to IRQ 16
	Region 0: Memory at 92a30000 (64-bit, prefetchable) [size=64K]
	Region 2: Memory at 92a40000 (64-bit, prefetchable) [size=64K]
	Region 4: Memory at 92a50000 (64-bit, prefetchable) [size=64K]
	Expansion ROM at 90100000 [disabled] [size=256K]
	Capabilities: [48] Power Management version 3
		Flags: PMEClk- DSI- D1- D2- AuxCurrent=0mA PME(D0+,D1-,D2-,D3hot+,D3cold+)
		Status: D0 NoSoftRst+ PME-Enable- DSel=8 DScale=1 PME-
	Capabilities: [50] Vital Product Data
		Product Name: Broadcom NetXtreme Gigabit Ethernet
		Read-only fields:
			[PN] Part number: BCM95720
			[MN] Manufacture ID: 31 30 32 38
			[V0] Vendor specific: FFV20.2.17
			[V1] Vendor specific: DSV1028VPDR.VER1.0
			[V2] Vendor specific: NPY2
			[V3] Vendor specific: PMT1
			[V4] Vendor specific: NMVBroadcom Corp
			[V5] Vendor specific: DTINIC
			[V6] Vendor specific: DCM1001008d452101008d45
			[RV] Reserved: checksum good, 233 byte(s) reserved
		End
	Capabilities: [58] MSI: Enable- Count=1/8 Maskable- 64bit+
		Address: 0000000000000000  Data: 0000
	Capabilities: [a0] MSI-X: Enable+ Count=17 Masked-
		Vector table: BAR=4 offset=00000000
		PBA: BAR=4 offset=00001000
	Capabilities: [ac] Express (v2) Endpoint, MSI 00
		DevCap:	MaxPayload 256 bytes, PhantFunc 0, Latency L0s <4us, L1 <64us
			ExtTag- AttnBtn- AttnInd- PwrInd- RBE+ FLReset+ SlotPowerLimit 10.000W
		DevCtl:	Report errors: Correctable- Non-Fatal+ Fatal+ Unsupported+
			RlxdOrd- ExtTag- PhantFunc- AuxPwr+ NoSnoop- FLReset-
			MaxPayload 256 bytes, MaxReadReq 512 bytes
		DevSta:	CorrErr- UncorrErr- FatalErr- UnsuppReq- AuxPwr+ TransPend-
		LnkCap:	Port #0, Speed 5GT/s, Width x2, ASPM L0s L1, Exit Latency L0s <1us, L1 <2us
			ClockPM+ Surprise- LLActRep- BwNot- ASPMOptComp-
		LnkCtl:	ASPM Disabled; RCB 64 bytes Disabled- CommClk+
			ExtSynch- ClockPM- AutWidDis- BWInt- AutBWInt-
		LnkSta:	Speed 5GT/s, Width x1, TrErr- Train- SlotClk+ DLActive- BWMgmt- ABWMgmt-
		DevCap2: Completion Timeout: Range ABCD, TimeoutDis+, LTR-, OBFF Not Supported
		DevCtl2: Completion Timeout: 65ms to 210ms, TimeoutDis-, LTR-, OBFF Disabled
		LnkCtl2: Target Link Speed: 2.5GT/s, EnterCompliance- SpeedDis-
			Transmit Margin: Normal Operating Range, EnterModifiedCompliance- ComplianceSOS-
			Compliance De-emphasis: -6dB
		LnkSta2: Current De-emphasis Level: -6dB, EqualizationComplete-, EqualizationPhase1-
			EqualizationPhase2-, EqualizationPhase3-, LinkEqualizationRequest-
	Capabilities: [100 v1] Advanced Error Reporting
		UESta:	DLP- SDES- TLP- FCP- CmpltTO- CmpltAbrt- UnxCmplt- RxOF- MalfTLP- ECRC- UnsupReq- ACSViol-
		UEMsk:	DLP- SDES- TLP- FCP- CmpltTO- CmpltAbrt+ UnxCmplt+ RxOF- MalfTLP- ECRC- UnsupReq- ACSViol-
		UESvrt:	DLP+ SDES+ TLP+ FCP+ CmpltTO+ CmpltAbrt- UnxCmplt- RxOF+ MalfTLP+ ECRC+ UnsupReq- ACSViol-
		CESta:	RxErr- BadTLP- BadDLLP- Rollover- Timeout- NonFatalErr+
		CEMsk:	RxErr- BadTLP+ BadDLLP+ Rollover+ Timeout+ NonFatalErr+
		AERCap:	First Error Pointer: 00, GenCap+ CGenEn+ ChkCap+ ChkEn+
	Capabilities: [13c v1] Device Serial Number 00-00-34-17-eb-f0-dc-e3
	Capabilities: [150 v1] Power Budgeting <?>
	Capabilities: [160 v1] Virtual Channel
		Caps:	LPEVC=0 RefClk=100ns PATEntryBits=1
		Arb:	Fixed- WRR32- WRR64- WRR128-
		Ctrl:	ArbSelect=Fixed
		Status:	InProgress-
		VC0:	Caps:	PATOffset=00 MaxTimeSlots=1 RejSnoopTrans-
			Arb:	Fixed- WRR32- WRR64- WRR128- TWRR128- WRR256-
			Ctrl:	Enable+ ID=0 ArbSelect=Fixed TC/VC=ff
			Status:	NegoPending- InProgress-
	Kernel driver in use: tg3
	Kernel modules: tg3
```

</spoiler>

</spoiler>

- https://www.broadcom.com/products/ethernet-connectivity/network-ics/bcm5720-1gbase-t-ic
- https://unix.stackexchange.com/questions/393/how-to-check-how-many-lanes-are-used-by-the-pcie-card

## Q2. What is the current speed of the network interface? What offload features are enabled? Briefly explain the purpose of the tcp-segmentation-offload feature Hint ethtool

- speed: 1000Mb/s
- tcp-segmentation-offload, generic-segmentation-offload, generic-receive-offload
- tcp-segmentation-offload: offload the segmentation of data into TCP packets from the CPU to the NIC

<spoiler|ethtool>

```
arccy@nevers» sudo ethtool eno1
Settings for eno1:
	Supported ports: [ TP ]
	Supported link modes:   10baseT/Half 10baseT/Full
	                       100baseT/Half 100baseT/Full
	                       1000baseT/Half 1000baseT/Full
	Supported pause frame use: No
	Supports auto-negotiation: Yes
	Supported FEC modes: Not reported
	Advertised link modes:  10baseT/Half 10baseT/Full
	                       100baseT/Half 100baseT/Full
	                       1000baseT/Half 1000baseT/Full
	Advertised pause frame use: Symmetric
	Advertised auto-negotiation: Yes
	Advertised FEC modes: Not reported
	Link partner advertised link modes:  10baseT/Full
	                                    100baseT/Full
	                                    1000baseT/Full
	Link partner advertised pause frame use: Symmetric Receive-only
	Link partner advertised auto-negotiation: Yes
	Link partner advertised FEC modes: Not reported
	Speed: 1000Mb/s
	Duplex: Full
	Port: Twisted Pair
	PHYAD: 1
	Transceiver: internal
	Auto-negotiation: on
	MDI-X: on
	Supports Wake-on: g
	Wake-on: d
	Current message level: 0x000000ff (255)
			      drv probe link timer ifdown ifup rx_err tx_err
	Link detected: yes
```

</spoiler>
<spoiler|ethtool features>
```
arccy@nevers» sudo ethtool -k eno1 | grep " on" 
rx-checksumming: on
tx-checksumming: on
	tx-checksum-ipv4: on
	tx-checksum-ipv6: on
scatter-gather: on
	tx-scatter-gather: on
tcp-segmentation-offload: on
	tx-tcp-segmentation: on
	tx-tcp-ecn-segmentation: on
	tx-tcp6-segmentation: on
generic-segmentation-offload: on
generic-receive-offload: on
rx-vlan-offload: on [fixed]
tx-vlan-offload: on [fixed]
highdma: on
```
</spoiler>

- https://en.wikipedia.org/wiki/Large_send_offload

## Q3. What is the MAC address of the OS3 router facing your server? Can you infer the manufacturer of the network card? What about the MAC address of eth0 / eno1 and its manufacturer? Hint arp

- router.os3.nl: `f8:b1:56:2f:b5:23`
- `fb:b1:56`: Dell
- eno1: `34:17:eb:f0:dc:e3`
- `34:17:eb`: Dell

<spoiler|arp>

```
arccy@nevers» arp -i eno1
Address                  HWtype  HWaddress           Flags Mask            Iface
nancy.studlab.os3.nl     ether   34:17:eb:f0:de:af   C                     eno1
amiens.studlab.os3.nl    ether   34:17:eb:f0:e2:57   C                     eno1
avignon.studlab.os3.nl   ether   34:17:eb:f0:db:27   C                     eno1
router.studlab.os3.nl    ether   f8:b1:56:2f:b5:23   C                     eno1
```

</spoiler>

- http://www.adminsub.net/mac-address-finder/f8b156
- https://www.adminsub.net/mac-address-finder/34:17:eb:f0:dc:e3

## Q4. Assuming that you have completed the previous lab what interfaces are part of the xenbr0 bridge? What MAC addresses has this bridge learned so far? Hint brctl

- eno2: connection to avignon
- vif1.0: guest-01
- vif58.0: guest-05

- 00:16:3e:1a:e6:7e: Avignon-live-mig (guest on Avignon)
- 00:16:3e:81:56:74: guest-05
- 00:16:3e:76:c6:56: guest-01
- 76:8e:e9:fa:ea:b2: xenbr0 on avignon

<spoiler|brctl>

```
arccy@nevers» brctl show
bridge name	bridge id		STP enabled	interfaces
xenbr0		8000.2e26ca689ed9	no		eno2
							vif1.0
							vif58.0
```

</spoiler>

<spoiler|arp>

```
arccy@nevers» arp -i xenbr0
Address                  HWtype  HWaddress           Flags Mask            Iface
145.100.110.36           ether   00:16:3e:1a:e6:7e   C                     xenbr0
guest-05                 ether   00:16:3e:81:56:74   C                     xenbr0
g1.nevers.prac.os3.nl    ether   00:16:3e:76:c6:56   C                     xenbr0
145.100.110.33           ether   76:8e:e9:fa:ea:b2   C                     xenbr0
```

</spoiler>

## Q5. How many bytes did your eth0 / eno1 interface receive since boot? The kernel uses an unsigned long long variable for the RX bytes counter How much traffic (in GiB) must the server receive for this value to overflow? Hint ifconfig

- received: 60781452482 bytes
- unsigned long long int: 64 bit: 18446744073709551616

<spoiler|ifconfig>

```
arccy@nevers» ifconfig eno1
eno1: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 145.100.104.117  netmask 255.255.255.224  broadcast 145.100.104.127
        inet6 fe80::3617:ebff:fef0:dce3  prefixlen 64  scopeid 0x20<link>
        ether 34:17:eb:f0:dc:e3  txqueuelen 1000  (Ethernet)
        RX packets 51641130  bytes 60781452482 (60.7 GB)
        RX errors 0  dropped 2  overruns 0  frame 0
        TX packets 58094232  bytes 68336404776 (68.3 GB)
        TX errors 0  dropped 0 overruns 0  carrier 0  collisions 0
        device interrupt 16
```

</spoiler>
## Q6. What is the MTU setting for eth0 / eno1 ? When do you think it should be increased? When do you think it should be decreased? Hint ip link ifconfig

- MTU: 1500
- increase: when both ends of the connection support it, MTU can be increases for efficiency
- decrease: when the link will pass through an encapsulating protocol, it should be decreased to fit unfragmented in the encapsulating packet

<spoiler|ip link>

```
arccy@nevers» ip link show eno1
2: eno1: <BROADCAST,MULTICAST,UP,LOWER_UP> mtu 1500 qdisc mq state UP mode DEFAULT group default qlen 1000
    link/ether 34:17:eb:f0:dc:e3 brd ff:ff:ff:ff:ff:ff
```

</spoiler>
## Q7. What is the default gateway on your server? Why is there an explicit route to the OS3 network? If you would delete this latter route will you be able to send traffic to your default gateway? Why?

- default: 140.100.104.97
- explicit route: netmask pushed by DHCP
- yes: there is another explicit route (/32) for the gateway itself

<spoiler|ip route>

```
arccy@nevers» ip route
default via 145.100.104.97 dev eno1 proto dhcp src 145.100.104.117 metric 100
145.100.104.96/27 dev eno1 proto kernel scope link src 145.100.104.117
145.100.104.97 dev eno1 proto dhcp scope link src 145.100.104.117 metric 100
145.100.110.32/28 dev xenbr0 proto kernel scope link src 145.100.110.40
145.100.111.0/28 dev xenbr0 proto kernel scope link src 145.100.111.1
145.100.111.12/30 dev wg0 proto kernel scope link src 145.100.111.12
```

</spoiler>
## Q8. Perform a traceroute to bad horse Why does it stop after 30 hops? How can you increase this number? Provide the full traceroute output Hint mtr traceroute

- traceroute has a default ttl of 30 (hops)
- can be increased by setting the max ttl: `-m some_large_value`

<spoiler|traceroute>

```
arccy@nevers» traceroute signed.bad.horse
traceroute to signed.bad.horse (162.252.205.157), 30 hops max, 60 byte packets
 1  * * router.studlab.os3.nl (145.100.104.97)  0.567 ms
 2  ae3-1664.asd002a-jnx-01.surf.net (145.145.19.190)  1.632 ms  1.611 ms  1.655 ms
 3  asd-s8-rou-1041.NL.eurorings.net (134.222.155.84)  0.878 ms  0.759 ms  0.784 ms
 4  134.222.249.250 (134.222.249.250)  22.387 ms  22.366 ms  22.412 ms
 5  if-ae-2-2.tcore2.av2-amsterdam.as6453.net (195.219.194.6)  91.529 ms  91.587 ms  91.495 ms
 6  if-ae-14-2.tcore2.l78-london.as6453.net (80.231.131.160)  93.810 ms  93.498 ms  93.337 ms
 7  if-ae-15-2.tcore2.ldn-london.as6453.net (80.231.131.118)  91.738 ms  91.646 ms  91.896 ms
 8  if-ae-32-2.tcore2.nto-new-york.as6453.net (63.243.216.22)  91.684 ms  91.849 ms  99.566 ms
 9  if-ae-26-2.tcore1.ct8-chicago.as6453.net (216.6.81.29)  99.703 ms  91.771 ms  99.880 ms
10  if-ae-8-2.tcore2.tnk-toronto.as6453.net (66.110.48.1)  101.548 ms  100.147 ms  100.179 ms
11  if-ae-2-2.tcore1.tnk-toronto.as6453.net (64.86.33.89)  100.205 ms  99.484 ms  91.482 ms
12  64.86.33.58 (64.86.33.58)  99.372 ms  91.323 ms  91.344 ms
13  po-10.core02.tor1.prioritycolo.com (204.11.48.139)  91.590 ms  99.644 ms  91.599 ms
14  t00.toroc1.on.ca.sn11.net (162.252.204.2)  107.805 ms  134.881 ms  130.024 ms
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
<spoiler|man traceroute>
```
  -m max_ttl  --max-hops=max_ttl
                              Set the max number of hops (max TTL to be
                              reached). Default is 30
```
</spoiler>
## Q9. What are the three built-in chains in the netfilter filter table? Briefly explain what is the purpose of each chain

- INPUT: check incoming packets against this chain
- FORWARD: check packets not created by and not destined for current host against this chain
- OUTPUT: check outgoing packets created by this host against this chain

<spoiler|iptables>

```
arccy@nevers» sudo iptables -L
Chain INPUT (policy ACCEPT)
target     prot opt source               destination

Chain FORWARD (policy ACCEPT)
target     prot opt source               destination
ACCEPT     all  --  anywhere             anywhere             PHYSDEV match --physdev-out vif58.0 --physdev-is-bridged
ACCEPT     udp  --  anywhere             anywhere             PHYSDEV match --physdev-in vif58.0 --physdev-is-bridged udp spt:bootpc dpt:bootps
ACCEPT     all  --  anywhere             anywhere             PHYSDEV match --physdev-out vif58.0 --physdev-is-bridged
ACCEPT     all  --  guest-05             anywhere             PHYSDEV match --physdev-in vif58.0 --physdev-is-bridged
ACCEPT     all  --  anywhere             anywhere             PHYSDEV match --physdev-out vif1.0 --physdev-is-bridged
ACCEPT     udp  --  anywhere             anywhere             PHYSDEV match --physdev-in vif1.0 --physdev-is-bridged udp spt:bootpc dpt:bootps
ACCEPT     all  --  anywhere             anywhere             PHYSDEV match --physdev-out vif1.0 --physdev-is-bridged
ACCEPT     all  --  g1.nevers.prac.os3.nl  anywhere             PHYSDEV match --physdev-in vif1.0 --physdev-is-bridged

Chain OUTPUT (policy ACCEPT)
target     prot opt source               destination
```

</spoiler>

- https://unix.stackexchange.com/questions/96548/what-is-the-difference-between-output-and-forward-chains-in-iptables

## Q10. What ports are currently open on your machine? What services do they belong to? Hint netstat

- 22: ssh
- 53: coredns
- 111: rpcbind

<spoiler|netstat>

```
arccy@nevers» sudo netstat -plnt
Active Internet connections (only servers)
Proto Recv-Q Send-Q Local Address           Foreign Address         State       PID/Program name
tcp        0      0 0.0.0.0:111             0.0.0.0:*               LISTEN      22172/rpcbind
tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN      8678/sshd
tcp6       0      0 :::111                  :::*                    LISTEN      22172/rpcbind
tcp6       0      0 :::53                   :::*                    LISTEN      5745/coredns
tcp6       0      0 :::22                   :::*                    LISTEN      8678/sshd
```

</spoiler>

## Q11. How many unix sockets are currently created on your server? What are unix sockets used for? Hint lsof

- 12 unix domain sockets open
- unix sockets allow for communication between processes with a similar interface to network sockets but it all happens in kernel

<spoiler|lsof>

```
arccy@nevers» lsof -U
COMMAND  PID  USER   FD   TYPE             DEVICE SIZE/OFF   NODE NAME
systemd 8870 arccy    1u  unix 0x0000000000000000      0t0 155961 type=STREAM
systemd 8870 arccy    2u  unix 0x0000000000000000      0t0 155961 type=STREAM
systemd 8870 arccy    3u  unix 0x0000000000000000      0t0 161978 type=DGRAM
systemd 8870 arccy   14u  unix 0x0000000000000000      0t0 161986 /run/user/1000/systemd/notify type=DGRAM
systemd 8870 arccy   15u  unix 0x0000000000000000      0t0 161987 type=DGRAM
systemd 8870 arccy   16u  unix 0x0000000000000000      0t0 161988 type=DGRAM
systemd 8870 arccy   17u  unix 0x0000000000000000      0t0 161989 /run/user/1000/systemd/private type=STREAM
systemd 8870 arccy   23u  unix 0x0000000000000000      0t0 161995 /run/user/1000/gnupg/S.gpg-agent.extra type=STREAM
systemd 8870 arccy   24u  unix 0x0000000000000000      0t0 161996 /run/user/1000/gnupg/S.dirmngr type=STREAM
systemd 8870 arccy   25u  unix 0x0000000000000000      0t0 161997 /run/user/1000/gnupg/S.gpg-agent.browser type=STREAM
systemd 8870 arccy   26u  unix 0x0000000000000000      0t0 161998 /run/user/1000/gnupg/S.gpg-agent.ssh type=STREAM
systemd 8870 arccy   27u  unix 0x0000000000000000      0t0 161999 /run/user/1000/gnupg/S.gpg-agent type=STREAM
```

</spoiler>
## Q12. How can you test that a machine is listening on a specific TCP port? Can you do the same for UDP? Why? Hint nc telnet

- use netcat to open a connection to the host:port
- udp: not connection oriented, you can only send data, not open a connection

```
nc host:port
```

## Q13. What is the type and version of the webserver that serves www os3 nl ? Hint curl wget

- it claims to be Apache 2.4.10 on Debian

<spoiler|curl>

```
arccy@nevers» curl -v -X HEAD https://www.os3.nl
Warning: Setting custom HTTP method to HEAD with -X/--request may not work the
Warning: way you want. Consider using -I/--head instead.
* Rebuilt URL to: https://www.os3.nl/
*   Trying 145.100.96.70...
* TCP_NODELAY set
* Connected to www.os3.nl (145.100.96.70) port 443 (#0)
* ALPN, offering h2
* ALPN, offering http/1.1
* successfully set certificate verify locations:
*   CAfile: /etc/ssl/certs/ca-certificates.crt
  CApath: /etc/ssl/certs
* TLSv1.3 (OUT), TLS handshake, Client hello (1):
* TLSv1.3 (IN), TLS handshake, Server hello (2):
* TLSv1.2 (IN), TLS handshake, Certificate (11):
* TLSv1.2 (IN), TLS handshake, Server key exchange (12):
* TLSv1.2 (IN), TLS handshake, Server finished (14):
* TLSv1.2 (OUT), TLS handshake, Client key exchange (16):
* TLSv1.2 (OUT), TLS change cipher, Client hello (1):
* TLSv1.2 (OUT), TLS handshake, Finished (20):
* TLSv1.2 (IN), TLS handshake, Finished (20):
* SSL connection using TLSv1.2 / ECDHE-RSA-AES128-GCM-SHA256
* ALPN, server did not agree to a protocol
* Server certificate:
*  subject: CN=www.os3.nl
*  start date: Oct  4 21:23:53 2019 GMT
*  expire date: Jan  2 21:23:53 2020 GMT
*  subjectAltName: host "www.os3.nl" matched cert's "www.os3.nl"
*  issuer: C=US; O=Let's Encrypt; CN=Let's Encrypt Authority X3
*  SSL certificate verify ok.
> HEAD / HTTP/1.1
> Host: www.os3.nl
> User-Agent: curl/7.58.0
> Accept: */*
>
< HTTP/1.1 200 OK
< Date: Mon, 04 Nov 2019 07:43:07 GMT
< Server: Apache/2.4.10 (Debian)
< Strict-Transport-Security: max-age=31536000
< Set-Cookie: DokuWiki=2u1h95t34k3j19nat8uou97s66; path=/; secure; HttpOnly
< Expires: Thu, 19 Nov 1981 08:52:00 GMT
< Cache-Control: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
< Pragma: no-cache
< Set-Cookie: DW7fa065a06cb74b536c124cfbe56ac6d3=deleted; expires=Thu, 01-Jan-1970 00:00:01 GMT; Max-Age=0; path=/; secure; httponly
< Vary: Accept-Encoding
< Content-Type: text/html; charset=utf-8
* no chunk, no close, no size. Assume close to signal end
<
* Closing connection 0
* TLSv1.2 (OUT), TLS alert, Client hello (1):
```

</spoiler>
## Q14. What is the size of each ICMP packet? Why is it not 1024?

- 1032 bytes
- ICMP headers are 8 bytes

```
arccy@guest-01 ~ % ping -c 10 www.os3.nl -s 1024
arccy@nevers» sudo tcpdump -i xenbr0 icmp -w capture.pcap
```

<spoiler|tcpdump>

```
arccy@nevers» tcpdump -r capture.pcap
reading from file capture.pcap, link-type EN10MB (Ethernet)
09:02:42.470946 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 1, length 1032
09:02:42.471360 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 1, length 1032
09:02:43.472758 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 2, length 1032
09:02:43.473210 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 2, length 1032
09:02:44.489264 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 3, length 1032
09:02:44.489727 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 3, length 1032
09:02:45.513263 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 4, length 1032
09:02:45.513638 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 4, length 1032
09:02:46.537263 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 5, length 1032
09:02:46.537644 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 5, length 1032
09:02:47.561257 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 6, length 1032
09:02:47.561653 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 6, length 1032
09:02:48.585306 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 7, length 1032
09:02:48.585708 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 7, length 1032
09:02:49.609267 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 8, length 1032
09:02:49.609674 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 8, length 1032
09:02:50.633264 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 9, length 1032
09:02:50.633663 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 9, length 1032
09:02:51.657290 IP g1.nevers.prac.os3.nl > www.os3.nl: ICMP echo request, id 14557, seq 10, length 1032
09:02:51.657724 IP www.os3.nl > g1.nevers.prac.os3.nl: ICMP echo reply, id 14557, seq 10, length 1032
```

</spoiler>

<spoiler|man ping>

```
       -s packetsize
              Specifies the number of data bytes to be  sent.   The  default  is  56,  which
              translates  into  64  ICMP  data  bytes when combined with the 8 bytes of ICMP
              header data.
```

</spoiler>
