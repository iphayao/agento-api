package com.bnpaper.agento.content.dto;

import com.bnpaper.agento.common.domain.BriefStatus;

import java.time.Instant;

public record ContentBriefResponse(
        Long id,
        Long brandId,
        String channel,
        String contentGoal,
        String audience,
        String product,
        Integer numberOfIdeas,
        BriefStatus status,
        Instant createdAt
) {
}
