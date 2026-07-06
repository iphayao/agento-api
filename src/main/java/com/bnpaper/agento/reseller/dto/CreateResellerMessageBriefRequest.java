package com.bnpaper.agento.reseller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResellerMessageBriefRequest(
        @NotNull(message = "brandId is required")
        Long brandId,

        @NotNull(message = "resellerLeadId is required")
        Long resellerLeadId,

        @NotBlank(message = "messageGoal is required")
        String messageGoal,

        String tone,

        String product
) {
}
