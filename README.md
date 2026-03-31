# Terron

Terron is a Hotel Reservation Management System built using Java 11, Spring Boot, and PostgreSQL database.

## Prerequisites

In order to run this application you need to install these tools:
- Java 11 JDK
- Maven 3.x
- Docker (optional, for containerized deployment)
- PostgreSQL 12+ (optional, if not using Docker)

## Quick Start

### Option 1: Railway (Recommended for Production)

One-click deployment to Railway with managed PostgreSQL:

[![Deploy on Railway](https://railway.app/button.svg)](https://railway.app/template)

Or using CLI:
```bash
# Install Railway CLI
npm install -g @railway/cli

# Login and deploy
railway login
railway init
railway add --database postgres
railway up
```

### Option 2: Docker Compose (Local Development)

```bash
# Clone the repository
git clone <repository-url>
cd terron

# Setup environment
cp .env.example .env
# Edit .env with your database credentials

# Start with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f app
```

### Option 3: Local Development

```bash
# Build all modules
./mvnw clean install -DskipTests

# Run the application
cd web && ../mvnw spring-boot:run
```

## API Access

After running the app:
- **API Base URL:** http://localhost:8000
- **Swagger UI:** http://localhost:8000/swagger-ui/index.html#/
- **Health Check:** http://localhost:8000/actuator/health

## Database Seeding

The application automatically seeds default data on startup:

### Seeded Companies

**Insurance Companies:**
| Company Name | Email | Type |
|--------------|-------|------|
| AXA Mansard Insurance | info@axamansard.com | Insurance |
| Leadway Assurance | info@leadway.com | Insurance |

**Hotels:**
| Hotel Name | Email | Insurance Company |
|------------|-------|-------------------|
| Hilton Abuja | hilton.abuja@hilton.com | AXA Mansard |
| Eko Hotel & Suites | reservations@ekohotels.com | AXA Mansard |
| Transcorp Hilton Abuja | info@transcorphilton.com | Leadway |

### Default Users

| Username | Password | Role | Company |
|----------|----------|------|---------|
| admin@terror.insure | demo.admin | ADMIN | - |
| hilton.abuja@hilton.com | demo.password | COMPANY_OWNER | Hilton Abuja |
| maryam.a@axamansard.com | demo.password | INSURANCE_USER | AXA Mansard |
| bello.y@ntdc.gov.ng | demo.password | NTDA | - |
| dss.admin@dss.gov.ng | demo.password | DSS | - |

See [README.md](README.md) for complete user list.

## Cloud Deployment

### ☁️ Railway (Recommended)

Easiest deployment with automatic scaling and managed PostgreSQL.

**Via GitLab CI/CD:**
1. Set GitLab Variables: `RAILWAY_TOKEN`, `RAILWAY_PROJECT_ID`
2. Push to `main` branch
3. Click "Play" on deploy job in GitLab CI/CD

**Via Railway CLI:**
```bash
railway login
railway link
railway up
```

### Supported Platforms

- **Railway** ☁️ - Managed PostgreSQL + auto-scaling (Recommended)
- **Docker/Docker Compose** - Any cloud provider
- **Heroku** - Platform as a Service
- **AWS** - ECS, Elastic Beanstalk, EC2
- **DigitalOcean** - App Platform or Droplets
- **Google Cloud** - Cloud Run or GKE
- **Azure** - Container Instances or AKS

See detailed deployment guide: [deploy/CLOUD_DEPLOYMENT.md](deploy/CLOUD_DEPLOYMENT.md)

## GitLab CI/CD

This project includes `.gitlab-ci.yml` for automated CI/CD:

**Pipeline Stages:**
1. **Build** - Compiles the application
2. **Test** - Runs unit tests
3. **Package** - Builds Docker image
4. **Deploy** - Deploys to Railway (manual trigger)

**Required GitLab CI/CD Variables:**
- `RAILWAY_TOKEN` - Railway API token
- `RAILWAY_PROJECT_ID` - Railway project ID

## Project Structure

```
terron/
├── data/              # Data layer (Entities, Repositories, DTOs)
├── security/          # Security layer (JWT, Authentication)
├── service/           # Business logic layer (Services)
├── web/               # Web layer (Controllers, Seeders)
├── deploy/            # Deployment scripts and documentation
├── .gitlab-ci.yml     # GitLab CI/CD pipeline
├── railway.json       # Railway configuration
├── docker-compose.yml # Docker Compose configuration
├── Dockerfile         # Docker image build
└── .env.example       # Environment variables template
```

Module dependencies: `web → service → security → data`

## Configuration

### Environment Variables

Create a `.env` file from `.env.example`:

```bash
cp .env.example .env
```

Key variables:

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | JDBC URL for PostgreSQL | `jdbc:postgresql://localhost:5432/terrondb` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `prod` |
| `JAVA_OPTS` | JVM options | `-Xmx512m -Xms256m` |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `*` |

See `.env.example` for complete list.

### Railway Variables

When using Railway, these are automatically set:
- `${{Postgres.DATABASE_URL}}` - Full JDBC URL
- `${{Postgres.USER}}` - Database username
- `${{Postgres.PASSWORD}}` - Database password

### Profiles

- **dev** - Local development with localhost PostgreSQL
- **prod** - Production with environment-based configuration

## Build Commands

```bash
# Build all modules
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Run tests
./mvnw test

# Package for deployment
./mvnw clean package -DskipTests
```

## Docker Commands

```bash
# Build image
docker build -t terron-api:latest .

# Run container
docker run -d -p 8000:8000 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/terrondb \
  -e SPRING_DATASOURCE_PASSWORD=yourpassword \
  terron-api:latest

# View logs
docker logs -f <container-id>
```

## API Documentation

- **Swagger UI:** http://localhost:8000/swagger-ui/index.html#/
- **API Docs (JSON):** http://localhost:8000/v2/api-docs
- **Health:** http://localhost:8000/actuator/health

## Troubleshooting

### Railway Deployment

```bash
# Check Railway logs
railway logs

# Redeploy
railway up
```

### Database Connection Issues

```bash
# Check if database is running
docker-compose ps

# View database logs
docker-compose logs postgres
```

### Port Already in Use

Change the port in `docker-compose.yml`:
```yaml
ports:
  - "8080:8000"
```

### Reset Database

```bash
# Stop and remove volumes
docker-compose down -v

# Restart
docker-compose up -d
```

## Security Notes

- Container runs as non-root user (`terron`)
- CORS origins should be restricted in production
- Database passwords should be strong (16+ characters)
- Use HTTPS in production
- Never commit `.env` file to version control
- Store Railway token as GitLab CI/CD variable

## License

[Add license information here]

## Support

For deployment issues, see [deploy/CLOUD_DEPLOYMENT.md](deploy/CLOUD_DEPLOYMENT.md)
