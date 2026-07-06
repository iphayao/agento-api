package com.bnpaper.agento.agent.dto;

/**
 * Request contract for {@code POST /api/v1/generate/reseller-message} on agento-agent.
 */
public record GenerateResellerMessageRequest(
        String brandCode,
        String product,
        String resellerName,
        String resellerType,
        String messageGoal,
        String tone
) {
}
