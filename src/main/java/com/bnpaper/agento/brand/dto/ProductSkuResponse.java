package com.bnpaper.agento.brand.dto;

public record ProductSkuResponse(
        Long id,
        Long brandId,
        String skuCode,
        String name,
        String productType,
        Integer sheetCount,
        Integer ply,
        Integer packsPerCarton,
        Integer unitsPerPack,
        Integer unitsPerCarton,
        boolean active
) {
}
