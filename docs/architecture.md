# Architecture

`agento-api` is the backend API for **Agento Marketing**, an AI Marketing
Operations system for BN Paper. It stores marketing requests and results and
delegates all AI generation to the `agento-agent` service.

## Style: Modular Monolith, Package-by-Feature

A single deployable Spring Boot application, internally split into feature
modules. No microservices, no frontend, no AI model logic in this repo.

```
com.bnpaper.agento
├── common      # error envelope, shared enums, exception handling
├── brand       # Brand + ProductSku (seeded with SoClean)
├── content     # ContentBrief + GeneratedContent
├── campaign    # CampaignBrief + GeneratedCampaign
├── reseller    # ResellerLead + ResellerMessageBrief + GeneratedResellerMessage
├── agent       # WebClient integration with agento-agent
└── dashboard   # cross-module summary
```

Each feature module owns its entities, repositories, DTOs, MapStruct mapper,
service, and controller. Modules talk to each other through Spring beans
(e.g. every generate flow uses `BrandService.requireBrand`); the dashboard
module reads other modules' repositories for reporting.

## Layering

```
Controller  ── thin: validation + HTTP mapping only
    │
Service     ── business logic, transactions, orchestration of the agent
    │
Repository  ── Spring Data JPA
    │
PostgreSQL  ── schema owned by Flyway
```

- **Controllers** are thin. They validate input (`@Valid`) and map to/from DTOs.
- **Services** hold business logic and own transaction boundaries.
- **Mappers** (MapStruct) convert entities ↔ response DTOs.
- **Lombok** removes entity/boilerplate noise.

## Responsibility boundary

| Concern                        | Owner            |
|--------------------------------|------------------|
| Brand knowledge, prompts       | `agento-knowledge` |
| AI generation, model calls     | `agento-agent`   |
| Requests, results, reporting   | `agento-api` (this repo) |
| Frontend                       | `agento-web` (future) |

`agento-api` never builds prompts or calls a model directly. It records the
_request_ (a brief) and the _result_ returned by `agento-agent`.

## Generation flow

```
POST /briefs           → persist brief (status = NEW)
POST /briefs/{id}/generate
  → status = GENERATING
  → AgentClient → agento-agent HTTP API
  → persist GeneratedContent/Campaign/Message
  → status = GENERATED   (or FAILED on error)
```

The generate methods are intentionally **not** wrapped in a single transaction
so the terminal status write (`GENERATED`/`FAILED`) commits independently of a
possibly-failing agent call.
