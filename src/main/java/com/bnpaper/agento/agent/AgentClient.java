package com.bnpaper.agento.agent;

import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.agent.dto.AgentHealth;
import com.bnpaper.agento.agent.dto.GenerateCampaignRequest;
import com.bnpaper.agento.agent.dto.GenerateContentRequest;
import com.bnpaper.agento.agent.dto.GenerateResellerMessageRequest;
import com.bnpaper.agento.agent.dto.QualityCheckRequest;
import com.bnpaper.agento.agent.dto.QualityCheckResult;
import com.bnpaper.agento.agent.dto.ValidateKnowledgeRequest;
import com.bnpaper.agento.agent.dto.ValidateKnowledgeResult;
import com.bnpaper.agento.common.error.AgentGenerationFailedException;
import com.bnpaper.agento.common.error.AgentServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

/**
 * Thin, typed wrapper around the {@code agento-agent} HTTP API. All AI content
 * generation is delegated here; this repository never builds prompts or calls a
 * model directly.
 *
 * <p>Connectivity failures surface as {@link AgentServiceUnavailableException};
 * error responses from the agent surface as {@link AgentGenerationFailedException}.
 */
@Slf4j
@Component
public class AgentClient {

    private final WebClient webClient;
    private final Duration timeout;

    public AgentClient(WebClient agentWebClient, AgentProperties properties) {
        this.webClient = agentWebClient;
        this.timeout = Duration.ofMillis(properties.timeoutMs());
    }

    public AgentGenerationResult generateContent(GenerateContentRequest request) {
        return post("/api/v1/generate/content", request, AgentGenerationResult.class);
    }

    public AgentGenerationResult generateWeeklyCampaign(GenerateCampaignRequest request) {
        return post("/api/v1/generate/campaign", request, AgentGenerationResult.class);
    }

    public AgentGenerationResult generateResellerMessage(GenerateResellerMessageRequest request) {
        return post("/api/v1/generate/reseller-message", request, AgentGenerationResult.class);
    }

    public QualityCheckResult checkQuality(QualityCheckRequest request) {
        return post("/api/v1/quality/check", request, QualityCheckResult.class);
    }

    public ValidateKnowledgeResult validateKnowledge(ValidateKnowledgeRequest request) {
        return post("/api/v1/knowledge/validate", request, ValidateKnowledgeResult.class);
    }

    public AgentHealth health() {
        try {
            return webClient.get()
                    .uri("/health")
                    .retrieve()
                    .bodyToMono(AgentHealth.class)
                    .timeout(timeout)
                    .block();
        } catch (WebClientRequestException | IllegalStateException ex) {
            throw new AgentServiceUnavailableException("agento-agent is not reachable", ex);
        } catch (WebClientResponseException ex) {
            throw new AgentServiceUnavailableException(
                    "agento-agent health check returned " + ex.getStatusCode(), ex);
        }
    }

    private <T> T post(String path, Object body, Class<T> responseType) {
        try {
            T result = webClient.post()
                    .uri(path)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .timeout(timeout)
                    .block();
            if (result == null) {
                throw new AgentGenerationFailedException("agento-agent returned an empty response for " + path);
            }
            return result;
        } catch (WebClientRequestException ex) {
            throw new AgentServiceUnavailableException(
                    "Unable to reach agento-agent at " + path + ": " + ex.getMessage(), ex);
        } catch (WebClientResponseException ex) {
            if (ex.getStatusCode().is5xxServerError()) {
                throw new AgentServiceUnavailableException(
                        "agento-agent returned " + ex.getStatusCode() + " for " + path, ex);
            }
            throw new AgentGenerationFailedException(
                    "agento-agent rejected the request to " + path + " (" + ex.getStatusCode() + "): "
                            + ex.getResponseBodyAsString(), ex);
        } catch (IllegalStateException ex) {
            // block() timeout manifests as IllegalStateException / TimeoutException wrappers
            throw new AgentServiceUnavailableException("agento-agent timed out for " + path, ex);
        }
    }
}
