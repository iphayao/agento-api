package com.bnpaper.agento.campaign.dto;

import java.time.Instant;

public record GeneratedCampaignResponse(
        Long id,
        Long campaignBriefId,
        String output,
        Double qualityScore,
        Boolean isSafe,
        String provider,
        Instant createdAt
) {
}
