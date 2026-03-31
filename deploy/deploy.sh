#!/bin/bash

# Cloud Deployment Script for Terron API
# Usage: ./deploy.sh [local|prod|heroku|aws|digitalocean]

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="terron-api"
VERSION=$(date +%Y%m%d-%H%M%S)

echo_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

echo_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

echo_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Docker is installed
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        echo_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
    
    echo_info "Docker and Docker Compose are installed"
}

# Build Docker image
build_image() {
    echo_info "Building Docker image..."
    docker build -t ${APP_NAME}:${VERSION} -t ${APP_NAME}:latest .
    echo_info "Build complete: ${APP_NAME}:${VERSION}"
}

# Deploy locally with Docker Compose
deploy_local() {
    echo_info "Deploying locally with Docker Compose..."
    
    if [ ! -f ".env" ]; then
        echo_warn ".env file not found. Creating from .env.example..."
        cp .env.example .env
        echo_warn "Please edit .env file with your configuration before continuing."
        exit 1
    fi
    
    docker-compose down --remove-orphans 2>/dev/null || true
    docker-compose up -d --build
    
    echo_info "Deployment complete!"
    echo_info "API URL: http://localhost:8000"
    echo_info "Swagger UI: http://localhost:8000/swagger-ui/index.html#/"
    
    # Wait for health check
    sleep 10
    if curl -s http://localhost:8000/actuator/health | grep -q "UP"; then
        echo_info "Health check passed!"
    else
        echo_warn "Health check failed or not ready yet. Check logs with: docker-compose logs -f"
    fi
}

# Deploy to production server
deploy_prod() {
    echo_info "Deploying to production..."
    
    if [ ! -f ".env" ]; then
        echo_error ".env file not found. Please create it from .env.example"
        exit 1
    fi
    
    # Pull latest changes if in git repo
    if [ -d ".git" ]; then
        git pull origin main || echo_warn "Could not pull latest changes"
    fi
    
    # Build and deploy
    docker-compose -f docker-compose.yml down --remove-orphans 2>/dev/null || true
    docker-compose -f docker-compose.yml up -d --build
    
    # Cleanup old images
    docker image prune -f
    
    echo_info "Production deployment complete!"
}

# Deploy to Heroku
deploy_heroku() {
    echo_info "Deploying to Heroku..."
    
    if ! command -v heroku &> /dev/null; then
        echo_error "Heroku CLI is not installed."
        exit 1
    fi
    
    # Check if app is created
    if ! heroku apps:info &> /dev/null; then
        echo_warn "Heroku app not found. Please create it first:"
        echo "heroku create your-app-name"
        exit 1
    fi
    
    # Add PostgreSQL if not exists
    if ! heroku addons:info heroku-postgresql &> /dev/null 2>&1; then
        echo_info "Adding PostgreSQL addon..."
        heroku addons:create heroku-postgresql:mini
    fi
    
    # Deploy
    git push heroku main
    
    echo_info "Heroku deployment complete!"
    heroku open
}

# Deploy to AWS ECS
deploy_aws() {
    echo_info "Deploying to AWS ECS..."
    
    if ! command -v aws &> /dev/null; then
        echo_error "AWS CLI is not installed."
        exit 1
    fi
    
    # Get AWS account ID
    AWS_ACCOUNT=$(aws sts get-caller-identity --query Account --output text)
    AWS_REGION=$(aws configure get region)
    ECR_REPO="${AWS_ACCOUNT}.dkr.ecr.${AWS_REGION}.amazonaws.com/${APP_NAME}"
    
    echo_info "Logging into ECR..."
    aws ecr get-login-password --region ${AWS_REGION} | \
        docker login --username AWS --password-stdin ${ECR_REPO}
    
    echo_info "Building and pushing image..."
    docker build -t ${APP_NAME}:${VERSION} .
    docker tag ${APP_NAME}:${VERSION} ${ECR_REPO}:${VERSION}
    docker tag ${APP_NAME}:${VERSION} ${ECR_REPO}:latest
    docker push ${ECR_REPO}:${VERSION}
    docker push ${ECR_REPO}:latest
    
    echo_info "Image pushed to: ${ECR_REPO}:${VERSION}"
    echo_warn "Please update your ECS task definition manually or via AWS Console"
}

# Show usage
show_usage() {
    echo "Usage: $0 [local|prod|heroku|aws|build]"
    echo ""
    echo "Commands:"
    echo "  local    - Deploy locally with Docker Compose"
    echo "  prod     - Deploy to production server"
    echo "  heroku   - Deploy to Heroku"
    echo "  aws      - Deploy to AWS ECS"
    echo "  build    - Build Docker image only"
    echo ""
    echo "Examples:"
    echo "  $0 local     # Local development"
    echo "  $0 prod      # Production deployment"
    echo "  $0 build     # Build image only"
}

# Main script
main() {
    case "${1:-}" in
        local)
            check_docker
            build_image
            deploy_local
            ;;
        prod)
            check_docker
            build_image
            deploy_prod
            ;;
        heroku)
            deploy_heroku
            ;;
        aws)
            check_docker
            deploy_aws
            ;;
        build)
            check_docker
            build_image
            ;;
        *)
            show_usage
            exit 1
            ;;
    esac
}

main "$@"
