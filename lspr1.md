# K8s Load Balancing Performance Analysis

## Background

- load balancer: distribute load between instances
- service proxy: allow external requests to reach into cluster

## Metrics

- performance: requests per second
- latency: time for requests
- latency: background services churn

## Variables

- Networking layer

## Options

### LoadBalancer

- EKS Network Load Balancer
- EKS Classical Load Balancer
- GKE TCP/UDP Load Balancer
- GKE HTTP(S) Load Balancer
- AKS Basic Load Balancer
- AKS Standard Load Balancer
- DOKS Load Balancer

### Service Proxy

external load balancing

- Kubernetes Ingress (NGINX)
- Contour (Envoy)
- HAProxy
- Skipper
- Traefik

### API Gateway

- Ambassador
- Gloo
- Kong

### Service Mesh

hybrid load balancing

- Consul
- Istio
- LinkerD
- Maesh
- SuperGloo

### Networking

service layer load balancing

- Cilium
- Flannel
- Calico
