package com.bnpaper.agento.content.dto;

import java.time.Instant;

public record GeneratedContentResponse(
        Long id,
        Long contentBriefId,
        String output,
        Double qualityScore,
        Boolean isSafe,
        String provider,
        Instant createdAt
) {
}
