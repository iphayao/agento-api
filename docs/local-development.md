# Local Development

## Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- Docker (for PostgreSQL and for running Testcontainers tests)
- A running `agento-agent` on `http://localhost:8081` (optional — only needed
  to actually generate content; briefs and reads work without it)

## 1. Start PostgreSQL

```bash
docker compose up -d postgres
```

This starts PostgreSQL on `localhost:5432` with db/user/password `agento`.

## 2. Run the app

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/agento
export SPRING_DATASOURCE_USERNAME=agento
export SPRING_DATASOURCE_PASSWORD=agento
export AGENTO_AGENT_BASE_URL=http://localhost:8081

mvn spring-boot:run
```

Flyway applies `V1` + `V2` on startup. The API is at `http://localhost:8080`.

## 3. Verify

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/v1/brands
curl http://localhost:8080/api/v1/dashboard/summary
```

## Tests

```bash
mvn test
```

- Service and controller tests use H2 + Mockito, no Docker needed.
- The agent client is tested against OkHttp `MockWebServer`.
- `AgentoApiIntegrationTest` spins up **real** PostgreSQL via Testcontainers and
  runs the real Flyway migrations (requires Docker). No live `agento-agent` is
  ever needed — it is mocked.

## Configuration reference

| Env var                     | Default                              |
|-----------------------------|--------------------------------------|
| `SERVER_PORT`               | `8080`                               |
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/agento` |
| `SPRING_DATASOURCE_USERNAME`| `agento`                             |
| `SPRING_DATASOURCE_PASSWORD`| `agento`                             |
| `AGENTO_AGENT_BASE_URL`     | `http://localhost:8081`              |
| `AGENTO_AGENT_TIMEOUT_MS`   | `30000`                              |

## Run everything in Docker

```bash
docker compose up --build
```

Brings up PostgreSQL + `agento-api`. `agento-agent` is expected to run on the
host and is reached at `http://host.docker.internal:8081`.
