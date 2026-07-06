package com.bnpaper.agento.agent.dto;

/**
 * Request contract for {@code POST /api/v1/knowledge/validate} on agento-agent.
 */
public record ValidateKnowledgeRequest(
        String brandCode
) {
}
