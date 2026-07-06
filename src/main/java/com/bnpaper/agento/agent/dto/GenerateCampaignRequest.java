package com.bnpaper.agento.agent.dto;

/**
 * Request contract for {@code POST /api/v1/generate/campaign} on agento-agent.
 */
public record GenerateCampaignRequest(
        String brandCode,
        String product,
        String objective,
        String channel,
        String audience,
        Integer durationWeeks
) {
}
