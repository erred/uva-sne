# Amazon Template

## Q1. Create a keypair and download the pem file to a directory

- https://eu-west-1.console.aws.amazon.com/ec2/home?region=eu-west-1#KeyPairs:sort=keyName
- `chmod 0400 Sean.pem`

- https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html

## Q2. What is the difference between EBS and instance-store for VMs?

- EBS: Elastic Block Store == persistent block storage devices that can be attached to VMs, lifecycle separate from VMs
- instance-store: temporary block storage device attached while VMs are running

- https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/InstanceStorage.html
- https://aws.amazon.com/ebs/

## Q3. Launch two (micro)instances of a free Ubuntu 18 04 distro For now use Security Group os3-allow-vpc

- https://eu-west-1.console.aws.amazon.com/ec2/home?region=eu-west-1#LaunchInstanceWizard:

## Q4. Use ssh to connect to both instances install a Web server and change the web page to display the following where is a unique single digit id number for each instance c This page is served by instance number

- `ssh -i aws-creds/Sean.pem ubuntu@ec2-18-203-110-103.eu-west-1.compute.amazonaws.com`
- `ssh -i aws-creds/Sean.pem ubuntu@ec2-18-203-101-39.eu-west-1.compute.amazonaws.com`
- `sudo apt update && sudo apt install nginx`
- edit `/var/www/html/index.nginx-debian.html`

```
» curl 18.203.101.39
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com

» curl 18.203.110.103
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
```

## Q5. Test each web server instance using the DNS reference provided by Amazon

```
» curl ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com

» curl ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
```

## Q6. Measure the HTTP response time for each instance Load Balancing Create a load balancer containing both LAMP servers

| instance | average response time (20 requests, seconds) |
| -------- | -------------------------------------------- |
| 39       | 0.066                                        |
| 103      | 0.065                                        |

```
» bash
[arccy@eevee ~]$  export TIMEFORMAT='%3R'

[arccy@eevee ~]$  time curl ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
0.055
[arccy@eevee ~]$  time curl ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
0.053

[arccy@eevee ~]$  time curl ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
0.056
[arccy@eevee ~]$  time curl ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
0.066
```

<spoiler|time curl>

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s ec2-18-203-101-39.eu-west-1.compute.amazonaws.com >/dev/null; done
0.078
0.069
0.064
0.073
0.085
0.073
0.068
0.061
0.059
0.063
0.066
0.061
0.058
0.059
0.066
0.064
0.066
0.066
0.063
0.064
```

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s ec2-18-203-110-103.eu-west-1.compute.amazonaws.com >/dev/null; done
0.053
0.064
0.056
0.067
0.102
0.066
0.065
0.071
0.065
0.066
0.065
0.060
0.062
0.063
0.064
0.065
0.065
0.063
0.056
0.071
```

</spoiler>
## Q7. Test the load balancing server using the DNS reference provided by Amazon

```
» curl sean-154159855.eu-west-1.elb.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com

14:34:40 ~ 0s
» curl sean-154159855.eu-west-1.elb.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
```

## Q8. Which server is responding? And when?

both, more or less randomly, probably a variant of round robin

## Q9. Measure the HTTP response time via the load balancer Now generate a continuous load (of work) on the most responsive of the instances

average over 20 request: 0.067

<spoiler|time curl>

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s sean-154159855.eu-west-1.elb.amazonaws.com >/dev/null; done
0.066
0.073
0.086
0.072
0.066
0.064
0.061
0.061
0.068
0.065
0.068
0.063
0.062
0.072
0.068
0.060
0.069
0.060
0.070
0.065
```

</spoiler>

## Q10. How does this influence the load balancer?

no effect, the load balancer doesn't know about the load on the server,
the load balancer only cares about the health check: response to `GET /`

<spoiler|cpu load>

```
fulload() { dd if=/dev/zero of=/dev/null | dd if=/dev/zero of=/dev/null | dd if=/dev/zero of=/dev/null | dd if=/dev/zero of=/dev/null & }; fulload; read; killall dd

top
top - 13:43:10 up  1:00,  2 users,  load average: 7.49, 3.50, 1.37
Tasks:  97 total,   9 running,  55 sleeping,   0 stopped,   0 zombie
```

</spoiler>

<spoiler|curl>

```
» for i in {1..20}; do curl sean-154159855.eu-west-1.elb.amazonaws.com; done
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
```

</spoiler>
- https://stackoverflow.com/questions/2925606/how-to-create-a-cpu-spike-with-a-bash-command

## Q11. Measure the HTTP response time both on the load balancer as well as on the separate web servers

| instance | 20 request average response time (s) |
| -------- | ------------------------------------ |
| ELB      | 0.066                                |
| 39       | 0.065                                |
| 103      | 0.067                                |

<spoiler|time curl>

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s sean-154159855.eu-west-1.elb.amazonaws.com >/dev/null; done
0.050
0.071
0.071
0.071
0.065
0.069
0.065
0.069
0.068
0.059
0.054
0.073
0.066
0.065
0.065
0.064
0.061
0.068
0.071
0.069
```

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s ec2-18-203-110-103.eu-west-1.compute.amazonaws.com >/dev/null; done
0.054
0.067
0.066
0.068
0.065
0.067
0.067
0.070
0.066
0.063
0.069
0.070
0.067
0.068
0.068
0.071
0.067
0.069
0.069
0.068
```

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s ec2-18-203-101-39.eu-west-1.compute.amazonaws.com >/dev/null; done
0.050
0.062
0.067
0.070
0.065
0.068
0.068
0.068
0.068
0.069
0.067
0.070
0.065
0.068
0.066
0.069
0.066
0.054
0.063
0.054
```

</spoiler>

## Q12. Draw a conclusion from the results

Amazon's load balancer is (rightfully) unaware of CPU load on serving instances and relies on health checks to determin if an instance should continue to be used

## Q13. What mechanism does the load balancer use to decide whether a server is overloaded? Now add a Microsoft Windows 10 14 image ( ami-0cd8a780065ea078f ) and set up an IIS web server to serve the same page as the Ubuntu servers (you can change the for instance)

health checks: HTTP 200 response to `GET /`

- https://docs.aws.amazon.com/elasticloadbalancing/latest/application/target-group-health-checks.html

## Q14. What are the advantages/disadvantages of a mixed setup?

- advantages:
  - diverse set of backends means a single vulnerability is less likely to affect all instances
- disadvantages:

  - harder to manage

- `xfreerdp ec2-34-254-192-50.eu-west-1.compute.amazonaws.com.rdp`

## Q15. Re-test the load balancing server using the DNS reference provided by Amazon

see next question

## Q16. Which server is responding? And when?

all 3, more or less randomly, probably a variant of round robin

<spoiler|curl>

```
[arccy@eevee ~]$ for i in {1..20}; do curl -s sean-154159855.eu-west-1.elb.amazonaws.com; done
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
This page is served by instance number windows ec2-34-254-192-50.ed-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-110-103.eu-west-1.compute.amazonaws.com
This page is served by instance number ec2-18-203-101-39.eu-west-1.compute.amazonaws.com
```

</spoiler>
## Q17. Measure the HTTP response time via the load balancer Security

20 requests average: 0.074s

<spoiler|time curl>

```
[arccy@eevee ~]$ for i in {1..20}; do time curl -s sean-154159855.eu-west-1.elb.amazonaws.com >/dev/null; done
0.051
0.085
0.074
0.088
0.070
0.097
0.069
0.072
0.100
0.074
0.066
0.062
0.067
0.063
0.067
0.089
0.065
0.097
0.066
0.050
```

</spoiler>
## Q18. Create a Security Group for your web servers with reasonable inbound traffic rules For example you might want to limit certain traffic to sources from the OS3 network only Explain your reasons Termination Now terminate all your instances and especially your load balancer they are really expensive For the Amazon Cloud

- http/https traffic from os3: testing (ipv4 + ipv6: we have both)
- http/https traffic from 10.0.0.0/8: health checks (ipv4: don't think they use ipv6 for this?)
- ssh/rdp from os3: server management (ipv4 + ipv6: we have both)

## Q19. How much money did it cost? Make a detailed bill based on your estimated usage

- estimate 1 hour: \$0.059
  - 2x ec2 t2.micro on-demand linux: 0.025
  - 1x ec2 t2.micro on-demand windows: 0.016
  - 1x load balancer 1rps 10mb/h: 0.018
- from cost explorer (filter by tag): 0.0644374463

## Q20. Give an estimate of the costs if the services are used for a year Virtual Data Center A sysadmin argues that he can move ALL the storage network server (web application database) and security services of her company to the Amazon cloud

0.0644 x 24 x 365 = \$564.14

- https://calculator.aws/#/estimate

## Q21. Do you think this is feasible with the current AWS services?

- without knowledge of current workloads it is impossible to say if it is possible
  - ex some software need hardware key licenses that are impossible to do in the cloud
- without special requirements it is certainly possible
- whether it is cost effective / more efficient is another matter

## Q22. Briefly explain how you would use each AWS service to implement the sysadmin's plan

- easy way:
  - assuming currently using vmare for everything
  - **VMWare Cloud on AWS**: lift and shift everything to cloud
- less easy way:
  - storage:
    - **S3** for file storage
    - **AWS Backup** for backups
  - network:
    - **Route 52** dns
    - **Elastic Load Balancing** load balancing
    - **CloudFront** cdn
  - servers:
    - **EC2**, unless they are already designed to run in containers, then **Elastic Container Service**
    - **RDS** for whatever database they were using, **Aurora** if their workloads can use it
  - security:
    - **VPN** vpn
    - **Directory Service** active directory
    - **Identity and Access Management** iam
