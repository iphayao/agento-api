package com.bnpaper.agento.brand;

import com.bnpaper.agento.brand.dto.BrandResponse;
import com.bnpaper.agento.brand.dto.ProductSkuResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public List<BrandResponse> list() {
        return brandService.listBrands();
    }

    @GetMapping("/{id}")
    public BrandResponse get(@PathVariable Long id) {
        return brandService.getBrand(id);
    }

    @GetMapping("/{id}/products")
    public List<ProductSkuResponse> products(@PathVariable Long id) {
        return brandService.listProducts(id);
    }
}
