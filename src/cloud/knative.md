# Knative

A Kubernetes-based platform for deploying and managing serverless workloads.

Key Features:
- Automatic scaling (including scale to zero)
- Request-driven compute
- Built on Kubernetes (uses CRDs, controllers, operators)
- Abstracts away infrastructure complexity

# Components

## Serving

Deploys and runs serverless containers.

Core Resources:
- **Service**: Top-level abstraction managing the entire lifecycle
- **Configuration**: Desired state of deployment (container image, env vars, limits)
- **Revision**: Immutable snapshot of Configuration (each change creates new revision)
- **Route**: Maps network endpoints to Revisions (traffic splitting)

Traffic Flow:
```
Request → Activator → Queue Proxy → Container
```

- **Activator**: Buffers requests when pods are scaled to zero, triggers scale-up
- **Queue Proxy**: Sidecar that handles request queuing and concurrency limits

Scale to Zero:
1. No requests for configurable period (default 30s)
2. Knative scales pods to zero
3. New request hits Activator
4. Activator holds request, signals Autoscaler
5. Pods created, request forwarded

## Eventing

Provides event-driven architecture.

Core Resources:
- **Broker**: Event mesh that receives and delivers events
- **Trigger**: Filters events from Broker and routes to subscribers
- **Source**: Produces events from external systems
- **Sink**: Destination for events (any addressable Kubernetes resource)

Event Format: CloudEvents specification (standardized event envelope)

```yaml
specversion: "1.0"
type: "dev.knative.samples.hello"
source: "/mycontext"
id: "A234-1234-1234"
data: { "message": "Hello World" }
```

Common Sources:
- PingSource (cron-based events)
- KafkaSource
- GitHubSource
- ContainerSource (custom event producers)

Channel Types:
- InMemoryChannel (development only, not persistent)
- KafkaChannel (durable, production-ready)
- NATSChannel

# Service Definition

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: hello
spec:
  template:
    spec:
      containers:
        - image: gcr.io/knative-samples/helloworld-go
          env:
            - name: TARGET
              value: "World"
```

# Autoscaling

Knative Autoscaler uses KPA (Knative Pod Autoscaler):

Metrics:
- **Concurrency**: Requests being processed simultaneously per pod
- **RPS**: Requests per second

Configuration:
```yaml
spec:
  template:
    metadata:
      annotations:
        autoscaling.knative.dev/target: "100"
        autoscaling.knative.dev/metric: "concurrency"
        autoscaling.knative.dev/minScale: "0"
        autoscaling.knative.dev/maxScale: "10"
```

# Traffic Splitting

Route percentage of traffic between revisions:

```yaml
apiVersion: serving.knative.dev/v1
kind: Service
metadata:
  name: hello
spec:
  traffic:
    - revisionName: hello-v1
      percent: 80
    - revisionName: hello-v2
      percent: 20
```

Use Cases:
- Canary deployments
- Blue/green deployments
- A/B testing

# Comparison

| Feature | Knative | AWS Lambda | Cloud Run |
|---------|---------|------------|-----------|
| Platform | Kubernetes | AWS | GCP |
| Vendor Lock-in | No | Yes | Partial |
| Scale to Zero | Yes | Yes | Yes |
| Container Support | Yes | Container images | Yes |
| Max Execution | Unlimited | 15 min | 60 min |
| Event System | Eventing | EventBridge | Pub/Sub |

# Installation

```bash
kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.12.0/serving-crds.yaml
kubectl apply -f https://github.com/knative/serving/releases/download/knative-v1.12.0/serving-core.yaml
kubectl apply -f https://github.com/knative/eventing/releases/download/knative-v1.12.0/eventing-crds.yaml
kubectl apply -f https://github.com/knative/eventing/releases/download/knative-v1.12.0/eventing-core.yaml
```

Requires a networking layer (choose one):
- Kourier (lightweight, recommended for getting started)
- Istio (full service mesh)
- Contour

# Architecture

```
┌──────────────────────────────────────────────┐
│                   Ingress                    │
│              (Kourier/Istio)                 │
└─────────────────────┬────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│                 Activator                     │
│         (handles scale-to-zero)              │
└─────────────────────┬────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│              Queue Proxy                      │
│    (sidecar in each pod, manages queue)      │
└─────────────────────┬────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│            User Container                     │
│         (your application)                   │
└──────────────────────────────────────────────┘
```

# When to Use

Good Fit:
- Event-driven microservices
- APIs with variable traffic patterns
- Batch processing triggered by events
- Multi-cloud/hybrid deployments
- Avoiding vendor lock-in while using serverless

Not Ideal:
- Long-running processes (consider regular Deployments)
- Applications that cannot handle cold starts
- Simple use cases where managed serverless (Lambda, Cloud Run) is sufficient
