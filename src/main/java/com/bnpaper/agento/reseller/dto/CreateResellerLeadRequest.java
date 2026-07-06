package com.bnpaper.agento.reseller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResellerLeadRequest(
        @NotNull(message = "brandId is required")
        Long brandId,

        @NotBlank(message = "name is required")
        String name,

        String contact,

        String resellerType,

        String location,

        String notes
) {
}
