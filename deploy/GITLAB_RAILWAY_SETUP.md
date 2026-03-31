# GitLab CI/CD + Railway Deployment Setup

This guide explains how to configure GitLab CI/CD to automatically deploy your Terron API to Railway.

## Prerequisites

1. GitLab repository with your code
2. Railway account (https://railway.app)
3. Railway project created

## Step 1: Create Railway Project

### Option A: Using Railway CLI

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Create new project
railway init

# Add PostgreSQL database
railway add --database postgres

# Get project ID for GitLab
railway project
```

### Option B: Using Railway Dashboard

1. Go to https://railway.app/dashboard
2. Click "New Project"
3. Select "Provision PostgreSQL"
4. Note down the Project ID from project settings

## Step 2: Get Railway API Token

1. Go to https://railway.app/account/tokens
2. Click "Create Token"
3. Give it a name (e.g., "GitLab CI/CD")
4. Copy the token (you won't see it again!)

## Step 3: Configure GitLab CI/CD Variables

Go to your GitLab project:

**Settings → CI/CD → Variables → Add Variable**

Add these variables:

| Variable | Value | Protected | Masked |
|----------|-------|-----------|--------|
| `RAILWAY_TOKEN` | Your Railway API token | ✅ Yes | ✅ Yes |
| `RAILWAY_PROJECT_ID` | Your Railway project ID | ✅ Yes | ❌ No |
| `RAILWAY_SERVICE_URL` | Your Railway service URL (e.g., `https://your-app.up.railway.app`) | ✅ Yes | ❌ No |
| `RAILWAY_SERVICE_NAME` | (Optional) Service name if multiple services | ❌ No | ❌ No |
| `SPRING_PROFILES_ACTIVE` | `prod` | ❌ No | ❌ No |
| `JAVA_OPTS` | `-Xmx512m -Xms256m` | ❌ No | ❌ No |
| `CORS_ALLOWED_ORIGINS` | `*` or your frontend URL | ❌ No | ❌ No |

### How to Get Railway Project ID

```bash
railway link
# OR
railway project
```

Or from Railway Dashboard:
- Project Settings → General → Project ID

### How to Get Railway Service URL

After first deployment, Railway provides a URL like:
`https://terron-api-production.up.railway.app`

## Step 4: Pipeline Configuration

The `.gitlab-ci.yml` file includes these deployment jobs:

### 1. `railway_deploy` (Basic)

Standard deployment to Railway.

```yaml
railway_deploy:
  stage: deploy
  when: manual  # Requires clicking "Play" button
```

### 2. `railway_deploy_with_healthcheck` (Recommended)

Deploys and verifies the deployment with health checks.

```yaml
railway_deploy_with_healthcheck:
  stage: deploy
  when: manual
```

This job:
- Deploys to Railway
- Waits 45 seconds for startup
- Performs health check with 10 retries
- Fails if health check doesn't pass

### 3. `railway_deploy_docker`

Deploys using Docker image from GitLab Registry.

### 4. `railway_rollback`

Manual rollback job (shows recent deployments).

## Step 5: Deploy

### Automatic Deployment on Push

To make deployment automatic (not manual), change:

```yaml
railway_deploy:
  # ...
  when: manual  # Change to: on_success
```

### Manual Deployment (Default)

1. Push code to `main` or `master` branch:
   ```bash
   git push origin main
   ```

2. Go to GitLab → CI/CD → Pipelines

3. Wait for build stages to complete (build, test, package)

4. Find the `railway_deploy` job

5. Click the ▶️ "Play" button to trigger deployment

6. Monitor the deployment logs

## Step 6: Verify Deployment

After successful deployment:

```bash
# Check Railway logs
railway logs

# Check deployment status
railway status

# Open deployed app
railway open
```

Or visit your service URL directly:
`https://your-app.up.railway.app`

## Pipeline Stages Overview

```
build → test → package → deploy (manual)
```

| Stage | Job | Description |
|-------|-----|-------------|
| Build | `build` | Compiles Java code |
| Test | `test` | Runs unit tests |
| Package | `package` | Builds Docker image & pushes to GitLab Registry |
| Deploy | `railway_deploy` | Deploys to Railway |

## Troubleshooting

### "Railway CLI not found"

The job installs Railway CLI in `before_script`. Make sure you're using `node:18-alpine` image.

### "Unauthorized" or "Invalid token"

- Verify `RAILWAY_TOKEN` is set correctly
- Generate a new token if needed
- Ensure token is not expired

### "Project not found"

- Check `RAILWAY_PROJECT_ID` is correct
- Run `railway project` locally to get correct ID

### Deployment fails but Railway shows success

Use `railway_deploy_with_healthcheck` job instead - it verifies the deployment with health checks.

### Health check fails

- Check `RAILWAY_SERVICE_URL` is correct
- Verify `/actuator/health` endpoint is accessible
- Check Railway logs: `railway logs`
- Increase sleep time in health check if needed

### Database connection issues

Railway automatically sets database environment variables. Make sure your app uses:
- `${{Postgres.DATABASE_URL}}`
- `${{Postgres.USER}}`
- `${{Postgres.PASSWORD}}`

Or in Spring Boot `application-prod.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

## Environment-Specific Deployments

### Staging Environment

Add to `.gitlab-ci.yml`:

```yaml
railway_deploy_staging:
  stage: deploy
  image: node:18-alpine
  before_script:
    - npm install -g @railway/cli
  script:
    - railway login --token $RAILWAY_TOKEN
    - railway link --project $RAILWAY_STAGING_PROJECT_ID
    - railway up --detach
  environment:
    name: staging
    url: $RAILWAY_STAGING_URL
  only:
    - develop
  when: manual
```

Add GitLab variables:
- `RAILWAY_STAGING_PROJECT_ID`
- `RAILWAY_STAGING_URL`

## Advanced Configuration

### Deploy Only Specific Services

If your Railway project has multiple services:

```yaml
script:
  - railway link --project $RAILWAY_PROJECT_ID --service $RAILWAY_SERVICE_NAME
  - railway up --detach
```

### Use Specific Railway Environment

```yaml
script:
  - railway login --token $RAILWAY_TOKEN
  - railway environment production  # or 'staging'
  - railway up --detach
```

### Deploy with Variables from GitLab

```yaml
script:
  - railway login --token $RAILWAY_TOKEN
  - railway link --project $RAILWAY_PROJECT_ID
  # Set variables from GitLab
  - railway variables set SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE
  - railway variables set JAVA_OPTS=$JAVA_OPTS
  - railway up --detach
```

## Security Best Practices

1. **Protect Variables**: Mark sensitive variables as "Protected" and "Masked"
2. **Use Protected Branches**: Only deploy from protected branches (`main`, `master`)
3. **Manual Approval**: Keep `when: manual` for production deployments
4. **Token Rotation**: Regularly rotate Railway tokens
5. **Limit Token Scope**: Create tokens with minimal required permissions

## Additional Resources

- [Railway Documentation](https://docs.railway.app/)
- [Railway CLI Reference](https://docs.railway.app/reference/cli-api)
- [GitLab CI/CD Documentation](https://docs.gitlab.com/ee/ci/)
- [GitLab CI/CD Variables](https://docs.gitlab.com/ee/ci/variables/)
