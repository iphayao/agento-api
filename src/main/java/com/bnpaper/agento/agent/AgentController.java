package com.bnpaper.agento.agent;

import com.bnpaper.agento.agent.dto.AgentHealth;
import com.bnpaper.agento.agent.dto.QualityCheckRequest;
import com.bnpaper.agento.agent.dto.QualityCheckResult;
import com.bnpaper.agento.agent.dto.ValidateKnowledgeRequest;
import com.bnpaper.agento.agent.dto.ValidateKnowledgeResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Thin passthrough endpoints for agento-agent utility calls (health, quality
 * checks, knowledge validation). Generation flows live in their feature modules.
 */
@RestController
@RequestMapping("/api/v1/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentClient agentClient;

    @GetMapping("/health")
    public AgentHealth health() {
        return agentClient.health();
    }

    @PostMapping("/quality-check")
    public QualityCheckResult checkQuality(@Valid @RequestBody QualityCheckRequest request) {
        return agentClient.checkQuality(request);
    }

    @PostMapping("/validate-knowledge")
    public ValidateKnowledgeResult validateKnowledge(@Valid @RequestBody ValidateKnowledgeRequest request) {
        return agentClient.validateKnowledge(request);
    }
}
