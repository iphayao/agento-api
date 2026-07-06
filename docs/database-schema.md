# Database Schema

PostgreSQL, schema owned by **Flyway**. Hibernate runs in `validate` mode — it
never creates or alters tables in any real environment.

Migrations live in `src/main/resources/db/migration`:

- `V1__initial_schema.sql` — all tables + indexes
- `V2__seed_soclean.sql` — seed SoClean brand + facial tissue SKU

## Tables

### `brands`
| Column      | Type        | Notes            |
|-------------|-------------|------------------|
| id          | bigserial   | PK               |
| code        | varchar(64) | unique           |
| name        | varchar     |                  |
| description | text        |                  |
| positioning | text        |                  |
| active      | boolean     | default true     |

### `product_skus`
| Column           | Type      | Notes                  |
|------------------|-----------|------------------------|
| id               | bigserial | PK                     |
| brand_id         | bigint    | FK → brands            |
| sku_code         | varchar   | unique                 |
| name             | varchar   |                        |
| product_type     | varchar   |                        |
| sheet_count      | int       | e.g. 180               |
| ply              | int       | e.g. 2 (double sheets) |
| packs_per_carton | int       | e.g. 10                |
| units_per_pack   | int       | e.g. 5                 |
| units_per_carton | int       | e.g. 50                |
| active           | boolean   |                        |

### `content_briefs`
`id, brand_id (FK), channel, content_goal, audience, product, number_of_ideas,
status, created_at`

### `generated_contents`
`id, content_brief_id (FK), output, quality_score, is_safe, provider, created_at`

### `campaign_briefs`
`id, brand_id (FK), objective, channel, audience, product, duration_weeks,
status, created_at`

### `generated_campaigns`
`id, campaign_brief_id (FK), output, quality_score, is_safe, provider, created_at`

### `reseller_leads`
`id, brand_id (FK), name, contact, reseller_type, location, notes, created_at`

### `reseller_message_briefs`
`id, brand_id (FK), reseller_lead_id (FK), message_goal, tone, product, status,
created_at`

### `generated_reseller_messages`
`id, reseller_message_brief_id (FK), output, quality_score, is_safe, provider,
created_at`

## SoClean seed (V2)

| Field            | Value                                    |
|------------------|------------------------------------------|
| brand code       | `SOCLEAN`                                |
| sku_code         | `SOCLEAN-FT-180`                         |
| product_type     | `FACIAL_TISSUE`                          |
| sheet_count      | 180 (double / 2-ply sheets)              |
| packs_per_carton | 10                                       |
| units_per_pack   | 5                                        |
| units_per_carton | 50                                       |

Product math: 1 carton = 10 packs, 1 pack = 5 tissue packs, 1 carton = 50 tissue packs.
