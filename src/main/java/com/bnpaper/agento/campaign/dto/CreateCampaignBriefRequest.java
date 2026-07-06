package com.bnpaper.agento.campaign.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCampaignBriefRequest(
        @NotNull(message = "brandId is required")
        Long brandId,

        @NotBlank(message = "objective is required")
        String objective,

        String channel,

        String audience,

        String product,

        @NotNull(message = "durationWeeks is required")
        @Min(value = 1, message = "durationWeeks must be at least 1")
        @Max(value = 52, message = "durationWeeks must be at most 52")
        Integer durationWeeks
) {
}
