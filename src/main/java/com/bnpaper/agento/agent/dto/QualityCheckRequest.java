package com.bnpaper.agento.agent.dto;

/**
 * Request contract for {@code POST /api/v1/quality/check} on agento-agent.
 */
public record QualityCheckRequest(
        String brandCode,
        String channel,
        String content
) {
}
