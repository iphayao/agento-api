package com.bnpaper.agento.brand;

import com.bnpaper.agento.brand.dto.BrandResponse;
import com.bnpaper.agento.brand.dto.ProductSkuResponse;
import org.mapstruct.Mapper;

@Mapper
public interface BrandMapper {

    BrandResponse toResponse(Brand brand);

    ProductSkuResponse toResponse(ProductSku sku);
}
