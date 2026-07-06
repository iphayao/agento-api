package com.bnpaper.agento.reseller.dto;

import java.time.Instant;

public record GeneratedResellerMessageResponse(
        Long id,
        Long resellerMessageBriefId,
        String output,
        Double qualityScore,
        Boolean isSafe,
        String provider,
        Instant createdAt
) {
}
