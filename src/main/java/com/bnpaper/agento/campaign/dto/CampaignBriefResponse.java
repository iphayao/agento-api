package com.bnpaper.agento.campaign.dto;

import com.bnpaper.agento.common.domain.BriefStatus;

import java.time.Instant;

public record CampaignBriefResponse(
        Long id,
        Long brandId,
        String objective,
        String channel,
        String audience,
        String product,
        Integer durationWeeks,
        BriefStatus status,
        Instant createdAt
) {
}
