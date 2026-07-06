package com.bnpaper.agento.content.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateContentBriefRequest(
        @NotNull(message = "brandId is required")
        Long brandId,

        @NotBlank(message = "channel is required")
        String channel,

        @NotBlank(message = "contentGoal is required")
        String contentGoal,

        String audience,

        String product,

        @NotNull(message = "numberOfIdeas is required")
        @Min(value = 1, message = "numberOfIdeas must be at least 1")
        @Max(value = 20, message = "numberOfIdeas must be at most 20")
        Integer numberOfIdeas
) {
}
