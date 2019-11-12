# Migration

## Q1. Read about VM migration in Xen

### a. Describe the differences between cold (or off-line ) migration and live migration

- cold migration: execution is stopped, network connections are dropped, the checkpoint is copied over, execution is restarted
- live migration: state is continuously copied over as things change, until a cutoff point where control is handed over. Execution never stops, connections are not dropped.

### b. What mechanism makes live migration almost instantaneous under light load?

- incremental / continuous copy of state while execution continues on the source machine.
- https://www.suse.com/c/xen-virtual-machine-migration/

## Q2. What are the technical requirements to be able to coldly migrate VMs and why?

- a way to transfer the state / checkpoint from one host to another: some way to move data
- compatible hosts that can both execute the guest: necessary for guest execution

## Q3. What are the technical requirements to be able to live migrate VMs and why?

- same as Q2
- shared storage mounted on same path: storage must be transparent to guest
- good connection: continous copy of state
- enough resources (ram/cpu) on target to execute the guest
- no special devices (pcie) attached
- https://www.virtuatopia.com/index.php/Migrating_Xen_domainU_Guests_Between_Host_Systems

## Q4. Form a group of two and discuss how you are going to migrate VMs to each other's hypervisor Set up your systems so you can do both cold and live migrations Describe your setup in your logs Hint Do not use LVM Remember your eno2

{{:2019-2020:students:sean_liao:ls:ls-2.png?direct|}}

- https://wiki.linuxfoundation.org/networking/bridge
- https://wiki.archlinux.org/index.php/Network_bridge#Troubleshooting

### cold migration

#### nevers -> avignon

- `sudo -i xen-create-image --force --memory=1024mb --size=10gb --vcpus=1 --dist=bionic --bridge=xenbr0 --ip=145.100.111.3 --gateway=145.100.111.1 --hostname=guest-02 --dir=/xen`
- `sudo xl create /etc/xen/guest-02.cfg`
  - in the guest: `cat /etc/machine-id`: `5c3f6311b18044a5b3726cdf5d7f4200`
- `sudo xl save guest-02 nevers-g2-cp`
- `rsync nevers-g2-cp marbadias@avignon.studlab.os3.nl:~`
- `sudo rsync -rP /xen/domains/guest-02 marbadias@avignon.studlab.os3.nl:~`
- avignon: `sudo mkdir -p /xen/domains`
- avignon: `sudo mv guest-02 /xen/domains`
- avignon: `sudo xl restore nevers-g2-cp`
  - in the guest: `cat /etc/machine-id`: `5c3f6311b18044a5b3726cdf5d7f4200`

#### avignon -> nevers

- `sudo mkdir -p /home/marbadias/domains`
- `sudo mv Avignon-02 /home/marbadias/domains`
- `sudo xl restore avignon-vm`
- `sudo xl list`:
  ```
  Name                                        ID   Mem VCPUs	State	Time(s)
  Domain-0                                     0 13036     8     r-----     398.7
  guest-01                                     1  1024     2     -b----       6.0
  Avignon-02                                   5  1024     2     -b----       0.5
  ```

### live migration

edit `/etc/xen/xend-config.sxp`:

```
(xend-relocation-server yes)
(xend-relocation-hosts-allow '145.100.104.103')
```

#### nevers -> avignon

- `sudo apt install nfs-kernel-server nfs-common`
- `sudo mkdir -p /xen/nevers`
- `sudo chown nobody:nogroup /xen`
- `sudo chmod 777 /xen`
- edit `/etc/exports`: `/xen/nevers 145.100.104.103(rw,sync,no_subtree_check,no_root_squash)`
- `sudo exportfs -a`
- `sudo systemctl restart nfs-kernel-server`
- `sudo -i xen-create-image --force --memory=1024mb --size=1gb --vcpus=1 --dist=bionic --bridge=xenbr0 --ip=145.100.111.4 --gateway=145.100.111.1 --hostname=guest-04 --dir=/xen/nevers`
- `sudo xl start /etc/xen/guest-04.cfg`
- `sudo xl migrate 6 145.100.104.103 -l`

```
arccy@nevers» sudo xl migrate 6 145.100.104.103 -l
root@145.100.104.103's password:
migration target: Ready to receive domain.
Saving to migration stream new xl format (info 0x3/0x0/1444)
Loading new save file <incoming migration stream> (new xl fmt info 0x3/0x0/1444)
 Savefile contains xl domain config in JSON format
Parsing config from <saved>
xc: info: Saving domain 6, type x86 PV
xc: info: Found x86 PV domain from Xen 4.9
xc: info: Restoring domain
xc: info: Restore successful
xc: info: XenStore: mfn 0x3fb808, dom 0, evt 1
xc: info: Console: mfn 0x3fb8e7, dom 0, evt 2
migration target: Transfer complete, requesting permission to start domain.
migration sender: Target has acknowledged transfer.
migration sender: Giving target permission to start.
migration target: Got permission, starting domain.
migration target: Domain started successsfully.
migration sender: Target reports successful startup.
Migration successful.
```

#### avignon -> nevers

- `sudo mkdir /home/marbadias/domains/Avignon-live-mig`
- `sudo mount 145.100.104.103:/home/marbadias/domains/Avignon-live-mig /home/marbadias/domains/Avignon-live-mig`

```
Name                                        ID   Mem VCPUs	State	Time(s)
Domain-0                                     0 13036     8     r-----     998.1
guest-01                                     1  1024     2     -b----      26.8
Avignon-live-mig                            18  1024     2     -b----       0.4
```

- https://wiki.xenproject.org/wiki/Migration
- https://www.virtuatopia.com/index.php/Migrating_Xen_domainU_Guests_Between_Host_Systems
- https://vitux.com/install-nfs-server-and-client-on-ubuntu/

## Q5. Together think of a definition of the downtime of a VM and how to best measure that downtime Write down your definition and measurement method(s) for both

downtime: period of time the guest is unavailable to perform tasks assigned to it.

- ex compute: period of time the guest is not running (executing useful things)
- ex network: period of time the guest is unreachable from the network

measurement method:

- compute: script in the vm to continuously check previous / current time: recording (large) breaks in time
- network: script from outside to continuously check ping connectivity to the guest, recording large breaks in connectivity

compute script:

```
#!/usr/bin/env python3
import time

# print(time.clock_getres(time.CLOCK_REALTIME))

old = time.clock_gettime_ns(time.CLOCK_REALTIME)
cum, cnt = 0, 0

while True:
  cur = time.clock_gettime_ns(time.CLOCK_REALTIME)
  diff = cur - old
  if diff > 10000000:
    print('break,', diff, ",mean loop time,", cum / cnt)
  else:
    cum += diff
    cnt += 1
  old = cur
```

network script:

`ping -i 0.01 145.100.110.39 > aaa.out`

```
#!/usr/bin/env python3

with open('aaa.out') as fp:
  lines = fp.readlines()
lines.pop(0)
lines.pop(-1)
lines.pop(-1)
lines.pop(-1)
lines.pop(-1)

count = 1
first_time = 0
last_time = 0

for line in lines:
  if 'From' in line:
    continue
  ping = line.split()
  ping = int(ping[4].split('=')[1])
  if ping != count:
    first_time = count
    last_time = ping
    count = last_time+1
    sq_missing = last_time - first_time
    print('Time average:')
    print((sq_missing+1)\*0.01)
    print('---------------------')
  else:
  count+=1
```

### a. Cold migration

test script:

```
#!/bin/bash

for i in {1..10}; do
  xl save guest-05 /xen/nevers/guest-05.img && \
    ssh root@avignon.studlab.os3.nl xl restore /xen/nevers/guest-05.img
  echo "# "
  echo "# nevers -> avignon done \$i"
  echo "# "
  sleep 1

  ssh root@avignon.studlab.os3.nl xl save guest-05 /xen/nevers/guest-05.img && \
    xl restore /xen/nevers/guest-05.img
  echo "# "
  echo "# avignon -> nevers done $i"
  echo "# "
  sleep 1
done
```

### b. Live migration

test script:

```
#!/bin/bash

for i in {1..10}; do
  xl migrate guest-05 avignon.studlab.os3.nl
  echo "# "
  echo "# nevers -> avignon done \$i"
  echo "# "
  sleep 1

  ssh root@avignon.studlab.os3.nl xl migrate guest-05 nevers.studlab.os3.nl
  echo "# "
  echo "# avignon -> nevers done $i"
  echo "# "
  sleep 1
done
```

## Q6. Perform cold migrations with your partner and measure the downtime Do not take just a single measurement Compute mean and median of your chosen metric

- 10 cycles: 1 cycle = from `nevers` to `avignon` then back to `nevers`.
- Each migration was a single measurement: total 20 measurements

| time in ms | Compute Downtime | Network Downtime |
| ---------- | ---------------- | ---------------- |
| Mean       | 21228            | 5423             |
| Median     | 19529            | 5045             |
| stdev      | 4398             | 3686             |

We think the lower values for network downtime are a result of us using ping
which tests if the interface is responsive and not if the host itself is responsive

## Q7. Perform live migrations with your partner and measure the downtime Compute mean and median of your chosen metric Performance There are various ways to set up shared storage for VM migration for instance using NFS and SMB two protocols that allow you to mount remote filesystems

- 10 cycles: 1 cycle = from `nevers` to `avignon` then back to `nevers`.
- Each migration was a single measurement: total 20 measurements

| time in ms | Compute Downtime | Network Downtime |
| ---------- | ---------------- | ---------------- |
| Mean       | 752              | 492              |
| Median     | 746              | 490              |
| stdev      | 30               | 12               |

## Q8. What are the most important differences between NFS and SMB? Explain in approximately 200 words

NFS (Network File System) is a protocol that only covers exposing a filesystem over a network.
Authentication is done per host or per request.
NFS has better performance than SMB, especially for small files.

SMB (Server Message Block) is a protocol for filesystems, printers, and serial ports over a network.
Authentication is done per user/connection or per request.
NFS is supports many more features,
but as a result is much more complex to configure,
introduces more bugs,
and is often the source of security issues.
SMB has also been extened by Mirosoft,
and is available natively on Windows.

- https://en.wikipedia.org/wiki/Network_File_System
- https://en.wikipedia.org/wiki/Server_Message_Block
- https://ferhatakgun.com/network-share-performance-differences-between-nfs-smb/

## Q9. Together with your partner design an experiment to compare the performance of NFS and SMB as VM shared storage Distinguish between raw I/O performance and the performance under a realistic workload E g what if the VM was running an Apache Web server? Discuss the design with a lab teacher

- `sudo apt install samba smbclient`
- edit `/etc/samba/smb.conf`
- `sudo mkdir /xen/samba`
- `sudo chown nobody:nogroup /xen/samba`
- `sudo systemctl restart smbd.service nmbd.service`
- `sudo useradd -r sambauser`
- `sudo smbpasswd -a sambauser`

- avignon: `sudo mount -t cifs -u sambauser %%//%%nevers.studlab.os3.nl/share mnt`

smb.conf:

```
[share]
comment = LS-2 test
path = /xen/samba
guest ok = yes
read only = no
```

- https://help.ubuntu.com/lts/serverguide/samba-fileserver.html

## Q10. Configure both NFS and SMB on your systems Perform the experiment and show the results in your log Try to explain any remarkable differences Hint root is a nobody when it comes to NFS

### Raw IO

copy 1GiB random (`/dev/urandom`) file, 10 times

|                        | Copy time (s) |
| ---------------------- | ------------- |
| Native local to local  | 19.75         |
| NFS write to remote    | 20.66         |
| NFS read from remote   | 20.15         |
| Samba write to remote  | 23.09         |
| Samba read from remote | 21.61         |

- to avoid caching effects we discarded the first _N_ results that were still increasing in transfer time.
- this synthetic test uses (relatively) large files and so is only useful for measuring raw throughput.
- the effects of latency should not be very visible in this test
- both setups were with default options, no attempts at performance tuning was made
- as expected samba was slightly slower

### Realistic Performance

Whether the test is **"realistic"** depends on the workload. Ex:

- A media transcoding server works similarly to our test and will see minimal impact
- A web server serving highly-cached / generated files will see minimal impact beyond first load.
- A database server may be more sensitive to latency and may see a higher impact

## BONUS

## Q11. Find out how the importing feature of Amazon works In which case would it be easier to migrate a VM to Amazon instead of just creating a new one?

## Q12. Create a new PV machine with 6500 MB of disk space Make sure Apache is installed and is serving a web page Convert this PV machine to a HVM machine Document the steps you take There are many ways to do the conversion we provide a cheat sheet below if you don't want to get lost

## Q13. Now migrate the disk to the Amazon cloud and create an instance with your volume Document which steps you took to get it to work Please make sure you use the t2 micro instance name all volumes and snapshots you create (unnamed ones are subject to deletion) delete all volumes and snapshots as soon as you are done This sounds draconic but S3 Storage is extremely expensive Last year we spent as much money on storage as on the whole of the Amazon lab Hint EC2 CLI EC2 volume upload (note ec2-instance-import is not working for us)

## Q14. Show the Amazon cloud serving the web page on your server What are the differences between the imported VM against the VM running on your system?
