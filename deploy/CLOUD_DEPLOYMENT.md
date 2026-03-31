# Cloud Deployment Guide

This guide covers deploying the Terron API to various cloud platforms.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Railway Deployment](#railway-deployment) (Recommended)
3. [GitLab CI/CD](#gitlab-cicd)
4. [Docker Deployment](#docker-deployment)
5. [Docker Compose Deployment](#docker-compose-deployment)
6. [Heroku Deployment](#heroku-deployment)
7. [AWS Deployment](#aws-deployment)
8. [DigitalOcean Deployment](#digitalocean-deployment)
9. [Environment Variables](#environment-variables)

---

## Prerequisites

- Docker 20.10+ and Docker Compose 2.0+
- OR Java 11+ and Maven 3.8+ (for non-Docker deployment)
- PostgreSQL 13+ database

---

## Railway Deployment

[Railway](https://railway.app) is the recommended platform for easy deployment with automatic scaling and managed PostgreSQL.

### Option 1: Deploy via Railway CLI (Recommended)

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Create new project
railway init

# Add PostgreSQL database
railway add --database postgres

# Deploy
railway up

# Open deployed app
railway open
```

### Option 2: Deploy via GitHub Integration

1. Push your code to GitHub
2. Go to [Railway Dashboard](https://railway.app/dashboard)
3. Click "New Project"
4. Select "Deploy from GitHub repo"
5. Choose your repository
6. Railway will auto-detect the `Dockerfile` and deploy

### Option 3: Deploy via GitLab CI/CD

1. Set these GitLab CI/CD Variables (Settings → CI/CD → Variables):
   - `RAILWAY_TOKEN` - Your Railway API token
   - `RAILWAY_PROJECT_ID` - Your Railway project ID

2. Push to `main` branch to trigger deployment

```bash
git push origin main
```

3. Go to GitLab → CI/CD → Pipelines to monitor deployment
4. Deploy job is manual - click "Play" button to deploy

### Railway Environment Variables

Set these in Railway Dashboard (Variables tab):

| Variable | Description | Example |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL | `${{Postgres.DATABASE_URL}}` |
| `SPRING_DATASOURCE_USERNAME` | DB username | `${{Postgres.USER}}` |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `${{Postgres.PASSWORD}}` |
| `JAVA_OPTS` | JVM options | `-Xmx512m` |
| `CORS_ALLOWED_ORIGINS` | CORS origins | `https://yourfrontend.com` |

**Note:** Railway automatically provides `${{Postgres.*}}` variables when you add a PostgreSQL database.

### Railway Configuration Files

- `railway.json` - Railway deployment configuration
- `.gitlab-ci.yml` - GitLab CI/CD pipeline for Railway deployment

---

## GitLab CI/CD

### Setup

1. Go to GitLab Project → Settings → CI/CD → Variables
2. Add these variables:

**For GitLab Container Registry:**
- `CI_REGISTRY_USER` - GitLab username
- `CI_REGISTRY_PASSWORD` - GitLab personal access token

**For Railway Deployment:**
- `RAILWAY_TOKEN` - Get from [Railway Dashboard](https://railway.app/account/tokens)
- `RAILWAY_PROJECT_ID` - Get from Railway project settings
- `RAILWAY_SERVICE_URL` - Your Railway service URL (for environment link)

### Pipeline Stages

The `.gitlab-ci.yml` includes:

1. **Build** - Compiles the application
2. **Test** - Runs unit tests
3. **Package** - Builds and pushes Docker image to GitLab Registry
4. **Deploy** - Deploys to Railway (manual trigger)

### Pipeline Triggers

- **Push to main/master** - Runs build, test, package
- **Merge Request** - Runs build and test
- **Manual Deploy** - Deploys to Railway (requires clicking play button)

---

## Docker Deployment

### Quick Start

```bash
# Clone and build
git clone <repository-url>
cd terron

# Build Docker image
docker build -t terron-api:latest .

# Run with environment variables
docker run -d \
  -p 8000:8000 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/terrondb \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  -e SPRING_PROFILES_ACTIVE=prod \
  --name terron-api \
  terron-api:latest
```

### Multi-Platform Build

```bash
# Create buildx builder
docker buildx create --use

# Build for multiple platforms
docker buildx build --platform linux/amd64,linux/arm64 \
  -t your-registry/terron-api:latest \
  --push .
```

---

## Docker Compose Deployment

### Local/Development

```bash
# Copy environment file
cp .env.example .env

# Edit .env with your configuration
nano .env

# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Full reset (removes volumes)
docker-compose down -v
```

### Production Server

```bash
# On your production server
git clone <repository-url>
cd terron

# Create production environment file
cp .env.example .env
# Edit .env with production values

# Start with production profile
docker-compose -f docker-compose.yml up -d

# Check status
docker-compose ps
docker-compose logs -f
```

---

## Heroku Deployment

### Using Heroku CLI

```bash
# Login to Heroku
heroku login

# Create app
heroku create your-terron-api

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:mini

# Set environment variables
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set JAVA_OPTS="-Xmx512m -Xms256m"

# Deploy
git push heroku main

# View logs
heroku logs --tail
```

### Using Container Registry

```bash
# Login to Heroku Container Registry
heroku container:login

# Build and push
docker build -t registry.heroku.com/your-terron-api/web .
docker push registry.heroku.com/your-terron-api/web

# Release
heroku container:release web -a your-terron-api
```

---

## AWS Deployment

### Option 1: AWS ECS (Elastic Container Service)

```bash
# Prerequisites: AWS CLI configured, ECS cluster created

# Push to ECR
aws ecr get-login-password --region us-east-1 | \
  docker login --username AWS --password-stdin <account>.dkr.ecr.us-east-1.amazonaws.com

docker tag terron-api:latest <account>.dkr.ecr.us-east-1.amazonaws.com/terron-api:latest
docker push <account>.dkr.ecr.us-east-1.amazonaws.com/terron-api:latest

# Create ECS task definition and service via AWS Console or CloudFormation
```

### Option 2: AWS Elastic Beanstalk

```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p docker terron-api

# Create environment
eb create terron-api-prod

# Deploy
eb deploy

# Open app
eb open
```

### Option 3: EC2 with Docker

```bash
# On EC2 instance
sudo yum update -y
sudo yum install docker -y
sudo service docker start
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Clone and run
git clone <repository-url>
cd terron
cp .env.example .env
# Edit .env
docker-compose up -d
```

---

## DigitalOcean Deployment

### Using App Platform

1. Push code to GitHub/GitLab
2. Connect repository in DigitalOcean App Platform
3. Select "Autodeploy" option
4. Add environment variables in the UI
5. Deploy

### Using Droplet

```bash
# On DigitalOcean Droplet (Ubuntu)
apt update && apt upgrade -y
apt install docker.io docker-compose -y
systemctl enable docker
systemctl start docker

# Clone and deploy
git clone <repository-url>
cd terron
docker-compose up -d
```

---

## Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/terrondb` | Yes |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - | Yes |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` | No |
| `PORT` | Server port | `8000` | No |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` | No |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `*` | No |
| `POSTGRES_DB` | PostgreSQL database name | `terrondb` | For Docker Compose |
| `POSTGRES_USER` | PostgreSQL username | `postgres` | For Docker Compose |
| `POSTGRES_PASSWORD` | PostgreSQL password | - | For Docker Compose |

---

## Health Checks

The application exposes health endpoints:

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

---

## Troubleshooting

### Railway Deployment Issues

```bash
# Check Railway logs
railway logs

# Check deployment status
railway status

# Redeploy
railway up
```

### Database Connection Issues

```bash
# Check if database is accessible
docker-compose exec app wget -qO- postgres:5432 || echo "Database not reachable"

# Check logs
docker-compose logs postgres
docker-compose logs app
```

### Out of Memory

Increase JVM heap size in `JAVA_OPTS`:
```bash
JAVA_OPTS=-Xmx2g -Xms1g
```

### Port Already in Use

Change the port mapping in `docker-compose.yml`:
```yaml
ports:
  - "8080:8000"  # Host:Container
```

---

## Security Best Practices

1. **Never commit `.env` file** - It's in `.gitignore`
2. **Use strong database passwords** - At least 16 characters
3. **Restrict CORS origins** - Don't use `*` in production
4. **Use HTTPS** - Configure SSL/TLS in production
5. **Regular updates** - Keep base images and dependencies updated
6. **Non-root user** - Container runs as `terron` user, not root
7. **Secure Railway token** - Store as GitLab CI/CD variable, never in code
