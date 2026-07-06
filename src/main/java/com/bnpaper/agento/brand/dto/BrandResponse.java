package com.bnpaper.agento.brand.dto;

public record BrandResponse(
        Long id,
        String code,
        String name,
        String description,
        String positioning,
        boolean active
) {
}
