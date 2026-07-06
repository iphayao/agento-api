# API Design

Base path: `/api/v1`. All payloads are JSON. Requests and responses use
dedicated DTOs (never entities). Input is validated with Jakarta Validation.

## Endpoints

### Brand
| Method | Path                          | Description            |
|--------|-------------------------------|------------------------|
| GET    | `/brands`                     | List brands            |
| GET    | `/brands/{id}`                | Get one brand          |
| GET    | `/brands/{id}/products`       | List a brand's SKUs    |

### Content
| Method | Path                                   | Description               |
|--------|----------------------------------------|---------------------------|
| POST   | `/content/briefs`                      | Create content brief (201)|
| GET    | `/content/briefs`                      | List content briefs       |
| GET    | `/content/briefs/{id}`                 | Get content brief         |
| POST   | `/content/briefs/{id}/generate`        | Generate via agento-agent |
| GET    | `/content/generated/{id}`              | Get generated content     |

### Campaign
| Method | Path                                    | Description                |
|--------|-----------------------------------------|----------------------------|
| POST   | `/campaigns/briefs`                     | Create campaign brief (201)|
| GET    | `/campaigns/briefs`                     | List campaign briefs       |
| GET    | `/campaigns/briefs/{id}`                | Get campaign brief         |
| POST   | `/campaigns/briefs/{id}/generate`       | Generate weekly campaign   |

### Reseller
| Method | Path                                          | Description               |
|--------|-----------------------------------------------|---------------------------|
| POST   | `/resellers/leads`                            | Create reseller lead (201)|
| GET    | `/resellers/leads`                            | List reseller leads       |
| GET    | `/resellers/leads/{id}`                       | Get reseller lead         |
| POST   | `/resellers/message-briefs`                   | Create message brief (201)|
| POST   | `/resellers/message-briefs/{id}/generate`     | Generate reseller message |

### Agent (utility passthrough)
| Method | Path                             | Description                 |
|--------|----------------------------------|-----------------------------|
| GET    | `/agent/health`                  | agento-agent health         |
| POST   | `/agent/quality-check`           | Quality check content       |
| POST   | `/agent/validate-knowledge`      | Validate brand knowledge    |

### Dashboard
| Method | Path                    | Description        |
|--------|-------------------------|--------------------|
| GET    | `/dashboard/summary`    | Aggregate summary  |

## Error format

Every error returns a consistent envelope:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request",
    "details": { "brandId": "brandId is required" }
  }
}
```

| Code                        | HTTP | Meaning                                   |
|-----------------------------|------|-------------------------------------------|
| `VALIDATION_ERROR`          | 400  | Request failed bean validation            |
| `RESOURCE_NOT_FOUND`        | 404  | Entity does not exist                     |
| `AGENT_SERVICE_UNAVAILABLE` | 503  | agento-agent unreachable / timed out / 5xx|
| `AGENT_GENERATION_FAILED`   | 502  | agento-agent reached but rejected/failed  |
| `INTERNAL_ERROR`            | 500  | Unhandled server error                    |

## Brief lifecycle (`status`)

`NEW → GENERATING → GENERATED` on success, or `→ FAILED` if the agent call fails.
