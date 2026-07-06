package com.bnpaper.agento.reseller.dto;

import java.time.Instant;

public record ResellerLeadResponse(
        Long id,
        Long brandId,
        String name,
        String contact,
        String resellerType,
        String location,
        String notes,
        Instant createdAt
) {
}
