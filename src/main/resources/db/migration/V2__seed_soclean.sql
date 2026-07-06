-- Seed the default MVP brand (SoClean) and its flagship facial tissue SKU.

INSERT INTO brands (code, name, description, positioning, active)
VALUES (
    'SOCLEAN',
    'SoClean',
    'SoClean facial tissue by BN Paper. AI Marketing Operations first use case: the SoClean Growth Engine, targeting 4,000 cartons/month.',
    'clean; soft; dust-reduced; good value; suitable for homes, stores, offices, restaurants, clinics, small hotels, and resellers',
    TRUE
);

-- Product economics:
--   180 double (2-ply) sheets per tissue pack
--   1 pack        = 5 tissue packs   (units_per_pack)
--   1 carton      = 10 packs         (packs_per_carton)
--   1 carton      = 50 tissue packs  (units_per_carton)
INSERT INTO product_skus (
    brand_id, sku_code, name, product_type,
    sheet_count, ply, packs_per_carton, units_per_pack, units_per_carton, active
)
SELECT
    b.id, 'SOCLEAN-FT-180', 'SoClean Facial Tissue 180 Double Sheets', 'FACIAL_TISSUE',
    180, 2, 10, 5, 50, TRUE
FROM brands b
WHERE b.code = 'SOCLEAN';
