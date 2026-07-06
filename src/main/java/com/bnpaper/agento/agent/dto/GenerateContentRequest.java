package com.bnpaper.agento.agent.dto;

/**
 * Request contract for {@code POST /api/v1/generate/content} on agento-agent.
 */
public record GenerateContentRequest(
        String brandCode,
        String product,
        String channel,
        String contentGoal,
        String audience,
        Integer numberOfIdeas
) {
}
