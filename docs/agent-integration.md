# Agent Integration

All AI generation is delegated to the `agento-agent` HTTP API. This repo holds
no prompts and calls no model directly.

## Configuration

```yaml
agento:
  agent:
    base-url: http://localhost:8081   # AGENTO_AGENT_BASE_URL
    timeout-ms: 30000                 # AGENTO_AGENT_TIMEOUT_MS
```

Bound by `AgentProperties`; the `WebClient` bean is built in `AgentClientConfig`.

## Client

`AgentClient` (a Spring `@Component` wrapping `WebClient`) exposes:

| Method                   | agento-agent endpoint (assumed contract)   |
|--------------------------|--------------------------------------------|
| `generateContent`        | `POST /api/v1/generate/content`            |
| `generateWeeklyCampaign` | `POST /api/v1/generate/campaign`           |
| `generateResellerMessage`| `POST /api/v1/generate/reseller-message`   |
| `checkQuality`           | `POST /api/v1/quality/check`               |
| `validateKnowledge`      | `POST /api/v1/knowledge/validate`          |
| `health`                 | `GET  /health`                             |

> These paths are the contract `agento-api` expects. If `agento-agent` differs,
> adjust the URIs in `AgentClient` and the DTOs in `agent/dto`.

## Request / response DTOs

Generation endpoints share a loose response shape (`AgentGenerationResult`):

```json
{ "output": "…", "qualityScore": 0.92, "isSafe": true, "provider": "claude" }
```

Unknown fields are ignored (`@JsonIgnoreProperties(ignoreUnknown = true)`), so
the agent can evolve its payload without breaking this client.

Example content generation request body:

```json
{
  "brandCode": "SOCLEAN",
  "product": "facial tissue",
  "channel": "facebook",
  "contentGoal": "awareness",
  "audience": "homes",
  "numberOfIdeas": 3
}
```

## Error handling

`AgentClient` translates transport/HTTP outcomes into domain exceptions handled
by `GlobalExceptionHandler`:

| Situation                              | Exception                          | HTTP |
|----------------------------------------|------------------------------------|------|
| Connection refused / timeout / 5xx     | `AgentServiceUnavailableException` | 503  |
| 4xx or empty body                      | `AgentGenerationFailedException`   | 502  |

So when `agento-agent` is unavailable, callers receive a clear
`AGENT_SERVICE_UNAVAILABLE` error envelope rather than an opaque 500. On a
failed generate, the corresponding brief is marked `FAILED`.

## Testing without a live agent

Tests never require a running `agento-agent`. `AgentClientTest` and the
integration test use OkHttp `MockWebServer` to stand in for the agent.
