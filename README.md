# SpringPipelineDemo

A Spring Boot demo project showcasing CI/CD pipelines with GitHub Actions.

## Features

- ✅ Spring Boot 3.5.6 with Java 17
- ✅ Actuator health checks
- ✅ JaCoCo code coverage (minimum 50%)
- ✅ Unit tests and integration tests
- ✅ Complete CI/CD workflows

## Project Structure

```
SpringPipelineDemo/
├── src/
│   ├── main/
│   │   ├── java/com/pipeline/demo/
│   │   │   ├── SpringPipelineDemoApplication.java
│   │   │   ├── controller/
│   │   │   │   └── HelloWorldController.java
│   │   │   └── service/
│   │   │       └── GreetingService.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/pipeline/demo/
│           ├── controller/
│           ├── service/
│           └── integration/
└── .github/workflows/
    ├── ci.yml
    ├── deploy-sit.yml
    ├── deploy-uat.yml
    ├── deploy-prod.yml
    ├── release-tag.yml
    ├── hotfix-tag.yml
    ├── prod-pr-guard.yml
    └── pipeline.yml
```

## API Endpoints

- `GET /api/hello` - Returns a simple hello message
- `GET /api/hello/personalized?name={name}` - Returns a personalized greeting
- `GET /api/health` - Custom health endpoint
- `GET /actuator/health` - Spring Boot Actuator health check

## Running the Application

```bash
# Build the project
mvn clean package

# Run the application
java -jar target/SpringPipelineDemo-0.0.1-SNAPSHOT.jar

# Or use Maven
mvn spring-boot:run
```

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with coverage report
mvn clean verify
# Coverage report: target/site/jacoco/index.html
```

## CI/CD Workflows

### CI Pipeline (`ci.yml`)
- Runs on PRs to `main` and `hotfix` branches
- Builds, tests, and checks code coverage (minimum 50%)

### Deployment Workflows
- **SIT**: `deploy-sit.yml` - Deploys when `sit` branch is updated
- **UAT**: `deploy-uat.yml` - Deploys when `uat` branch is updated
- **Production**: `deploy-prod.yml` - Deploys when `prod` branch is updated (after PR merge)

### Release Workflows
- **Release Tag**: `release-tag.yml` - Creates semantic version tags on `main` branch push
- **Hotfix Tag**: `hotfix-tag.yml` - Creates hotfix tags on `hotfix` branch push

### Guard Workflows
- **Production PR Guard**: `prod-pr-guard.yml` - Validates PRs from `uat` to `prod`

## Deployment Model

This project uses a **tag-based deployment model**:

- **Tags** = Deployable versions (only source of truth)
- **Branches** = Deployment pointers (not code sources)
- **PRs** = Approval gates (not version selectors)

### Branch Roles

| Branch | Purpose | Protection |
|--------|---------|------------|
| `main` | Future development / normal releases | Standard protection |
| `sit` | SIT deployment pointer | No PR, allow force push |
| `uat` | UAT deployment pointer | No PR, allow force push |
| `prod` | Production approval branch | Require PR from `uat`, no force push |
| `hotfix` | Hotfix approval branch | Require PR, no force push |

### Deployment Process

1. **Create Release Tag**: Merge to `main` → CI creates tag (e.g., `v1.0.0`)
2. **Deploy to SIT**: `git checkout v1.0.0 && git checkout -B sit && git push origin sit --force`
3. **Deploy to UAT**: `git checkout v1.0.0 && git checkout -B uat && git push origin uat --force`
4. **Deploy to Production**: Create PR `uat → prod` → After approval, production deploys

## Code Coverage

- **Minimum Coverage**: 50% (line and branch)
- **Coverage Tool**: JaCoCo
- **Report Location**: `target/site/jacoco/index.html`

## Requirements

- Java 17
- Maven 3.6+
- GitHub Actions (for CI/CD)

## License

This is a demo project for educational purposes.
