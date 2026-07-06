package com.bnpaper.agento.agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Common shape returned by agento-agent generation endpoints. Additional
 * fields returned by the agent are ignored to keep the contract loose.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AgentGenerationResult(
        String output,
        Double qualityScore,
        Boolean isSafe,
        String provider
) {
}
