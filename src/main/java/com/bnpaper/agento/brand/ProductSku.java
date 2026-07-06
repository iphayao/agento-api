package com.bnpaper.agento.brand;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_skus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "sku_code", nullable = false, unique = true)
    private String skuCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "sheet_count")
    private Integer sheetCount;

    private Integer ply;

    @Column(name = "packs_per_carton")
    private Integer packsPerCarton;

    @Column(name = "units_per_pack")
    private Integer unitsPerPack;

    @Column(name = "units_per_carton")
    private Integer unitsPerCarton;

    @Column(nullable = false)
    private boolean active;
}
