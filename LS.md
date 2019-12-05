# Large Systems

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
- Desktops
  -
