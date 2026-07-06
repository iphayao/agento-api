# agento-api

Backend API for **Agento Marketing**, an AI Marketing Operations system for
BN Paper. `agento-api` stores marketing requests and results and delegates all
AI generation to the `agento-agent` HTTP service.

First use case: the **SoClean Growth Engine** — helping SoClean facial tissue
reach 4,000 cartons/month.

## Where it fits

| Repo               | Responsibility                                   |
|--------------------|--------------------------------------------------|
| `agento-knowledge` | brand knowledge, prompts, templates, playbooks   |
| `agento-agent`     | AI agent runtime with HTTP API (does generation) |
| **`agento-api`**   | **this repo** — requests, results, reporting     |
| `agento-web`       | future frontend                                  |

> `agento-api` never builds prompts or calls a model. It records the request (a
> brief) and the result returned by `agento-agent`.

## Tech stack

Java 21 · Spring Boot 3.3 (Web, Validation, Data JPA) · PostgreSQL · Flyway ·
Lombok · MapStruct · WebClient · JUnit 5 · Testcontainers · Maven.

## Modules (modular monolith, package-by-feature)

```
com.bnpaper.agento
├── common      error envelope, shared enums, exception handling
├── brand       Brand + ProductSku (seeded with SoClean)
├── content     ContentBrief + GeneratedContent
├── campaign    CampaignBrief + GeneratedCampaign
├── reseller    ResellerLead + ResellerMessageBrief + GeneratedResellerMessage
├── agent       WebClient client for agento-agent
└── dashboard   cross-module summary
```

## Local run

Prerequisites: Java 21, Maven, Docker.

```bash
# 1. Start PostgreSQL
docker compose up -d postgres

# 2. Run the API (Flyway applies the schema + SoClean seed on startup)
mvn spring-boot:run

# 3. Smoke test
curl http://localhost:8080/api/v1/brands
curl http://localhost:8080/api/v1/dashboard/summary
```

Or run the whole stack (Postgres + API) in Docker:

```bash
docker compose up --build
```

See [`docs/local-development.md`](docs/local-development.md) for full config.

## Database setup & Flyway

Schema is owned by Flyway; Hibernate runs in `validate` mode.

```
src/main/resources/db/migration/
├── V1__initial_schema.sql   all tables + indexes
└── V2__seed_soclean.sql     SoClean brand + facial tissue SKU
```

Migrations run automatically on application start. Details in
[`docs/database-schema.md`](docs/database-schema.md).

## Agent integration

Configured via `agento.agent.base-url` (default `http://localhost:8081`).
`AgentClient` wraps `WebClient` and exposes `generateContent`,
`generateWeeklyCampaign`, `generateResellerMessage`, `checkQuality`,
`validateKnowledge`, and `health`. If `agento-agent` is unreachable, the API
returns a clear `AGENT_SERVICE_UNAVAILABLE` error. Details in
[`docs/agent-integration.md`](docs/agent-integration.md).

## Key endpoints

Full list in [`docs/api-design.md`](docs/api-design.md). Highlights:

```
POST /api/v1/content/briefs
GET  /api/v1/content/briefs
GET  /api/v1/content/briefs/{id}
POST /api/v1/content/briefs/{id}/generate
GET  /api/v1/content/generated/{id}

POST /api/v1/campaigns/briefs
POST /api/v1/campaigns/briefs/{id}/generate

POST /api/v1/resellers/leads
POST /api/v1/resellers/message-briefs
POST /api/v1/resellers/message-briefs/{id}/generate

GET  /api/v1/dashboard/summary
```

## Sample curl commands

Create a content brief (brand id 1 is the seeded SoClean):

```bash
curl -X POST http://localhost:8080/api/v1/content/briefs \
  -H 'Content-Type: application/json' \
  -d '{
        "brandId": 1,
        "channel": "facebook",
        "contentGoal": "awareness",
        "audience": "homes and small offices",
        "product": "SoClean facial tissue 180 double sheets",
        "numberOfIdeas": 3
      }'
```

Generate content for that brief (requires `agento-agent` running):

```bash
curl -X POST http://localhost:8080/api/v1/content/briefs/1/generate
```

Dashboard summary:

```bash
curl http://localhost:8080/api/v1/dashboard/summary
```

## Error format

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request",
    "details": { "brandId": "brandId is required" }
  }
}
```

Codes: `VALIDATION_ERROR`, `RESOURCE_NOT_FOUND`, `AGENT_SERVICE_UNAVAILABLE`,
`AGENT_GENERATION_FAILED`, `INTERNAL_ERROR`.

## Tests

```bash
mvn test
```

Service (Mockito), controller (`@WebMvcTest`), agent client (`MockWebServer`),
dashboard, and a Testcontainers integration test that runs real Flyway
migrations against real PostgreSQL. No live `agento-agent` is required — it is
always mocked.

## Documentation

- [`docs/architecture.md`](docs/architecture.md)
- [`docs/api-design.md`](docs/api-design.md)
- [`docs/agent-integration.md`](docs/agent-integration.md)
- [`docs/database-schema.md`](docs/database-schema.md)
- [`docs/local-development.md`](docs/local-development.md)
