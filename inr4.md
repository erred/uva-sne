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

## Q9. Draw a network diagram that depicts the configuration Make sure the VLANs are clearly marked

## Q10. Create a new VM running the Ubuntu 14 04 Trusty operating system Give it 1GB of RAM and a public IP Make sure you can log in using your ssh keys

## Q11. Install old git repository https //github com/TeamOS3/ogopogo git

## Q12. Set the values of A B X Y in scripts/rc local. In the scripts directory run the build sh. Copy this image to the /tmp directory

## Q13. Create the config file that starts the network depicted in Figure 1

## Q14. Create a diagram of the networks showing the state of all the bridge ports and the root bridge

## Q15. Describe in detail (i e your own words not verbatim dumps) what packets are sent when and why. Explain how the root bridge was elected

## Q16.

### a. What parameters are used in the BPDU packets?

### b. What is the role of each parameter?

### c. What are they set to? enumerate

## Q17. What happens if you shutdown the root bridge? Describe all the events that take place from the moment the bridge goes down until the network has converged again

## Q18. Restore the original situation. Disable STP on a bridge. Explain

## Q19. Enable STP again on all bridges. Does the network come back to the initial state? Why?

## Q20. Bonus Get STP to work with containers in the pogo toolset
