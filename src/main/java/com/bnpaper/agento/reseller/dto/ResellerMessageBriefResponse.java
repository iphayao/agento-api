package com.bnpaper.agento.reseller.dto;

import com.bnpaper.agento.common.domain.BriefStatus;

import java.time.Instant;

public record ResellerMessageBriefResponse(
        Long id,
        Long brandId,
        Long resellerLeadId,
        String messageGoal,
        String tone,
        String product,
        BriefStatus status,
        Instant createdAt
) {
}
