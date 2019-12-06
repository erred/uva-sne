# Large Systems

## Recap

- distribution transparency
  - what, main problems
- time to send packet
- scaling techniques
- user ISA, system ISA
- IO vs CPU bound
- system vs process VM
- taxonomy
- VT-X
- RPC, deferred synchronous
- UNIX file api
- MOM
- distributed algos

## Virtualization

- storage: files, disk partitions, logical blocks, RAID, LVM
- network: VLAN, channel bonding, clusters, virtual NICs
- resource: mulitprogramming, virtual memory
- VMs: basis for cloud computing, resource utilization
- Hardware - ISA - Software
- Hardware - (System ISA - Operating System / User ISA) - Application programs
- ISA
  - Ring 0: Kernel mode, Ring 3: User mode
  - User: raw compute
  - System ISA:
    - system resources (memory, storage, io), allocation, auth, OS
    - process: timer, user mode
    - memory: page table, TLB, virtual memory
    - traps: syscall exception
    - syscall: app - kernel - os syscall handler - os - kernel instruction
    - OS: app - lib - syscall os - kernel instruction - interrupt/trap/fault - os - app
- VM
  - system VM: HW - Virtual Machine Monitor - Guest OS - app
  - process VM: WH - Host OS - Virtual Machine Monitor - app
  - ABI: java, wine, WSL
  - API: recompile
- VM tree
  - process
    - same ISA: multiprogrammed systems: UNIX
    - diff ISA: emulators, translators: Wine, WSL, high level language VM: Java, MS CLR
  - system
    - same ISA: classic system VM: VM370, hosted VMs: VMware, Xen, docker
    - diff ISA: whole system VMs: ARM VM runtime, codesigned VMs: AS/400
- emulation: translate ISA
- VM impl:
  - trap on user access system ISA
  - not all syscall activate kernel mode (x86)
  - patch
  - Ring -1: Intel VT-X: VMX Root vs VMX Non-root
  - VM driver in host (dom0)
- paravirtualization:
  - guest OS modified (drivers) (pv front)
  - mux / driver on host
- OS virtualization
  - namespaces, not system or process, ex containers
  - uts (hostname), mnt (mount/filesystem), pid (process), user (UID), ipc (IPC IDs), net (network)
  - uts: sysname, nodename, release, version, machine, domainname
    - gethostname: `system_utsname.nodename` -> `current->nsproxy->uts_ns->name->nodename`
  - user, pid: offset view
  - net: copy stack, devices/sockets belong in single namespace, initial namespace, mv/ad- cgroups: resource management of process groups
- Windows:
  - hyper V
  - containers / hyper V containers
  - Windows Server Containers: namespaced
  - Hyper V containers: additionally run in VM
- Unikernels
  - no context switch
  - app specific os optimizations

| impl            | mode | with          | io  | interrupt/timer | motherboard/boot | priveled / page tabel |
| --------------- | ---- | ------------- | --- | --------------- | ---------------- | --------------------- |
| HVM / full      | HVM  |               | VS  | VS              | VS               | VH                    |
| HVM + PV driver | HVM  | PV drivers    | P   | VS              | VS               | VH                    |
| KVM             | HVM  |               | P   | VS              | VS / P           | VH                    |
| PVHVM           | HVM  | PVHVM drivers | P   | P               | VS               | VH                    |
| PVH             | PV   | pvh=1         | P   | P               | P                | VH                    |
| PV              | PV   |               | P   | P               | P                | P                     |

## Design & Implementation

- large = distributed
- transparency:
  - distribution
    - full transparency too hard, hiding node/network failures impossible
    - slow indistinguishable from failed
    - performance cost, insync reqquires time
  - failure
  - replication
  - relocation
- cluster failures, year 1
  - 1 network rewire, 5% over 2 days
  - 20 rack(40-60) failure, 1-6 hours
  - 5 racks(40-60) wonky, 50% packetloss
  - 8 network maintenance, 30min conn issues
  - 12 router reloads, ~mins
  - 3 router failures, ~hour
  - x0s DNS, 30s
  - 1000, machine failures
  - x000s hard drive fail
  - slow, flaky, misconfigured machines
  - nwild dogs, sharks, dead horses, drunk hunters affect network
- network performance
  - L1 cache 0.5ns
  - Branch miss 5ns
  - L2 cache 7ns
  - Mutex 25ns
  - Main memory 100ns
  - 1K cheap conpress 3000ns
  - 2K over 1Gbps 20000ns
  - 1MB from memory 250000ns
  - DC roundtrip 500000ns
  - Disk seek 10000000ns
  - 1MB from disk 20000000ns
  - CA->NL roundtrip 150000000ns
- application specific solutions
- scale:
  - numbers: users, machines, data
  - geography: lan / region / country / continent / global
  - admin: # owners
- scale techniques:
  - scale up: nonlinear, limited, few sers
  - scale out: replication, partition
  - async comms, virtualize
- google:
  - 1997: split to index and doc servers (sharded)
  - 1999: split to cache, replicated and sharded index and doc
    - cache miss on index update
    - adversial memory: bit flips in 1TB, mostly sorted
  - 2001: in memory index: single copy
    - load balanced shards, replicate important docs
    - +throughput +query latency -variance (xks machines), -availability fail
    - canary request, test not crash servers
  - 2004: tiered
    - cache at root
    - pyramid / tree of servers, leaf/repository servers over GFS
    - repository manager coordination, served from in memeory
  - batch process -> incremental
    - storage: Colossus / Bigtable, processng: Caffeine / Percolator
    - bigtable: distributed row-column-timestamp multidimensional eventual consistency
    - caffeine: events trigger on bigtable updates

### Communication Coordination Replication

- client-server: RPC: too hard to be transparent
  - ex: NFS: server stateful
- Message Oriented Middleware
  - abstract destinations, transparencies
  - decouple queue and location (IP address)
- Server-Server
  - L3 multicast 224.0.2.0+ not working globally, best effort udp
  - reach, all or none, dynamic groups, ordering: causal or total
- ordering
  - causal: m1 based on m2
  - total: global
  - time: atomic: direct / indirect gps
  - order events -> logical clock
  - always adjust clock to newest time
  - receipt = got by middleware != delivered = got by app
- mutex
  - central coord: request, ok, wait, release
    - google chubby
  - mesh
  - token ring

### scaling

- performance, redundancy - availability
- issues: consistency, network partitions
- CAP: Consistency, Availability, Partition Resistance
  - CA: MySQL, Spanner
  - CP: read only or dead when part: mongodb, hbase, redis, memcachedb
  - AP: always respond: couchdb, voldemort, cassandra
- consistency
  - linearizability: all at same time
  - sequential: same order, not at the same time
  - causal: related things in order
  - eventual: at some point
- replication
  - primary / backup: primary sequencer
  - active replication: need sequencer
  - quorom: NR + NW > N, NW > N/2
  - qs: granularity, replica locate/location (anycast, dns, locad balancer)
  - cache is king
- partition
  - heirarchical: admin
  - formulaic
- fault tolerance
  - safety
  - availability: uptime: 5nines: 5.26min/year, 6nines: 31.5s/year
  - reliability: mean time between failure
  - maintainability: mean time to repair
  - physical/information/time/software redundancy
- failures:
  - crash: k+1
  - byzantine (wrong ans): 2k+1 (majority)
  - RPC:
    - unknown point of failure
      - locate server, request lost, crash (server), reply lost, crash (client)
    - idempotent or atomic transactions
    - at least once
    - at most once
    - paxos consensus
    - partition = failure domain / swim lane
- architectures:
  - bittorrent
    - file pieces, info datastructure, infohash
    - torrent file: info ds, trackers, bootstrap DHT
    - choke/unchoke, tit-for-tat, rarest first
    - upload only to 4, optimistic random unchoke
    - ditributed hash table: no trackers

## Administration

### principles

- Service: business application
- devops: build and run it
- Full stack: strategy, design, transition, operation, continual improvement
  - strategy: business case, services to offer / in demand
  - design: requirements, SLA, supplier management
  - transition: build, testl, implement, prerelease, release management
  - operation: deliver, support, incidents, access
  - improvement: identify improvements
- todo, now
  - ticketing, triage (shield level 2 SA)
  - time saving policies: help procedure, responsibilities, emergency definition
  - automate: OS install, software release
- principles:
  - small batches, automate, deploy odten
    - features faster, smaller diff, MVP
  - automation
    - cattle not pets
  - self service, end to end integration, comms
- infrastructure as code
  - autoconfiguration from definition files
  - source / version controlled
  - complete system testing

### services

- service profolio, reviewed regularly
  - business case
  - us vs outsource
  - cost insights, pricing, charge customers
- centralization: less duplications, colume pricing, specialization
- decentralization: low quality central, needs not met (generic, uninnovative), costs
- service valuable: utility, warranty (availability, capacity, continuity, security)
- service design: meeting IRL, written reqs
  - issues: language, right questions, difficult requests
- Service Level Requirements
  - feasible? costs, functional and technical design
- Service Level Agreement
  - Service Description
  - Service Level Targets
    - service hours, availability, reliability
    - incident response, capacity, performance, continuity
  - Support
  - Reporting and Review
- Operation Level Agreements, Underpinning Contracts
- Deployment
  - change review board: stakeholders, aspects of change, risk
  - change process, what changes, affected, why, risk, betas, success criteria
  - backout plan, duration (change, backout), decision point
  - launch readiness criteria: monitoring, backups, access control, SLA, docs, load, scale

### Desktops

- fungibility: can be substituted: automation, limit variation
- DHCP for control, IEEE 802.1x network access control
- Network directories: MS active directory, Apple Open Directory, LDAP
- storage:
  - local: fast, bad for fungibility
  - remote: NFS, SMB, CIFS
  - local sybced: Dropbox, iCloud, ownCloud
- Hardware:
  - Physical: laptop/desktop, vendors, product line: initial/total cost, performance
  - Virtual Desktop Infra VDI: thin client, needs bandwidth
  - BYOD
- Evard Life cycle of machine
  - new-build-clean-initialize-configured-(update)-entropy-unknown-debug-configured
  - configured/unknown-rebuild-clean
  - clean/unknown/configured-retire-off
- transitions need process, aim for configured state, automate and policies
- automate Iac: Config mgmt (ansible, puppet), MS GPO, DHCP
  - central control, testing, small batched, user delay 1wk, track
- disposal: remove from inventory/contracts, transfer data, reset, remove from track
- Intel Active Management Technology AMT: hardware/firmware out of band management
  - vPRO/DRAC for desktops
  - power, boot, io over LAN, BIOS, control network traffic, OOB comm, inventory (soft, hard), keyboard, video, mouse, one touch setup, MINIX TLS/TCP/IP stack, direct NIC

### Servers

- single: dependency hell
- beautiful snowflakes: machine per service
- VM on large servers, exploit stranded capacity, VM packing
- vs Grid Computing, Cloud Computing, SaaS, Server applicanes
- racked, console/kvm/terminal access, OOB mgmt, separate admin network
- controlled env (datacenter)
- redundancy: mirrored disks, RAID, power supply, n+1/n+2
- hot plug/hot swap/hot spare, failure domains
- inverted bell curve of observed failure rate
- RAID
  - JBOD: concat
  - RAID0: stripe
  - RAID1: mirror
  - RAID5: parity
  - RAID6: double parity
  - left lower level
  - 0+1: mirror raid0 arrays
  - 10: stripe accross raid1 arrays
  - 10, 50(5+0), 100(10+0), proprietary

### Datacenter

- strategy: build your own, rent, outsource, no datacenter, hybrid
- requirements:
  - availability, continuity, budget, change control, regulatory, privacy, flexibility
  - local v remote, up to date, security, remote/customer access, responsive, capacity
- elements: racks, power, cooling, wiring, fire suppress, location, access
- 1U racks: 19in wide, hole spacing
  - not standard: height, depth, post width
- common: 45U: 42U content, switch top, UPS bottom
- power ditribution units: power strips with overload protection, ip accessible
- UPS per rack: 10min full load, 2 year battery, power conditioning
- generator: fueled, 24h at 125% load
- BTU: 1055.06 Joules, 1W = 3.41214BTU/h
- Ton of Cooling: 12000BTU/h = 3.52kW = refrigeration ton RT
- power usage effectiveness: total/it_equipment
- underfloor cold air: cold/warm aisle
- cold aisle containment / hot aisle containment
- top of rack wiring
- fire:
  - halon: not used, inergen: 52% N2, 40% Ar, 8% CO2, argonite: 50% Ar, 50% N2
