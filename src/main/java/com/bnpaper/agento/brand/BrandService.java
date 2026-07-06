package com.bnpaper.agento.brand;

import com.bnpaper.agento.brand.dto.BrandResponse;
import com.bnpaper.agento.brand.dto.ProductSkuResponse;
import com.bnpaper.agento.common.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductSkuRepository productSkuRepository;
    private final BrandMapper mapper;

    public List<BrandResponse> listBrands() {
        return brandRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    public BrandResponse getBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Brand", id));
        return mapper.toResponse(brand);
    }

    /** Ensures a brand exists; used by other modules before delegating to the agent. */
    public Brand requireBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Brand", id));
    }

    public List<ProductSkuResponse> listProducts(Long brandId) {
        requireBrand(brandId);
        return productSkuRepository.findByBrandId(brandId).stream().map(mapper::toResponse).toList();
    }
}
