# End-to-End GitOps Pipeline: Spring Boot on AWS EKS with ArgoCD

This documentation provides a comprehensive, step-by-step guide to provisioning an AWS EKS cluster, installing and configuring ArgoCD, establishing prerequisites for your Spring Boot application, and setting up an automated CI/CD loop.

---

## 🏗️ 1. Provisioning the AWS EKS Cluster

The most reliable and standard way to spin up an EKS cluster from the command line is using `eksctl`.

### Prerequisites for your local terminal:
* **AWS CLI** configured with administrator permissions (`aws configure`).
* **kubectl** installed matching your target Kubernetes version.
* **eksctl** installed on your machine.

### Step-by-Step Cluster Creation:
Run the following command to provision a production-ready, multi-node cluster:

```bash
eksctl create cluster \
  --name eks-argocd-cluster \
  --region us-east-1 \
  --nodegroup-name standard-workers \
  --node-type t3.medium \
  --nodes 3 \
  --nodes-min 1 \
  --nodes-max 4 \
  --managed
```

### Verify Cluster Status:
```bash
kubectl get nodes
```

---

## 🐙 2. Installing and Configuring ArgoCD

### Step 1: Install ArgoCD Components

```bash
kubectl create namespace argocd
kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

### Step 2: Access the ArgoCD Web Dashboard

```bash
kubectl -n argocd port-forward svc/argocd-server 8080:443
```

Open your browser and go to `http://localhost:8080`.

### Step 3: Retrieve the Initial Admin Password

#### Linux / macOS / Git Bash:
```bash
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d; echo
```

#### Windows PowerShell:
```powershell
[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String((kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}")))
```

### Step 4: Connecting your App in the ArgoCD Dashboard

- Click **+ New App** in the top left corner.
- **General Information**:
  - Application name: e.g., `spring-boot-app`
  - Project: default
  - Sync Policy: **Automatic** (enable Prune Resources and Self Heal)
- **Source**:
  - Repository URL: your GitHub repository containing manifests
  - Revision: `main` (or your branch)
  - Path: folder containing Kubernetes manifests (e.g., `k8s`)
- **Destination**:
  - Cluster URL: `https://kubernetes.default.svc`
  - Namespace: `default` (or your target namespace)

Click **Create**.

---

## 3. Manifest Separation & Layout Consistency

Organize your repository as follows:

```
├── .github/workflows/pipeline.yaml  # Contains pipeline logic
├── k8s/                             # Manifest folder watched by ArgoCD
│   ├── deployment.yaml              # Contains your application deployment
│   └── service.yaml                 # Contains LoadBalancer declarations
├── Dockerfile                       # Multi-stage image builder file
└── ...                              # Main Spring Boot source files
```
