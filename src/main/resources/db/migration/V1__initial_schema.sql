-- Agento API initial schema
-- Modular monolith: one schema, tables grouped by feature module.

-- === Brand module ===
CREATE TABLE brands (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(64)  NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    positioning TEXT,
    active      BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE product_skus (
    id               BIGSERIAL    PRIMARY KEY,
    brand_id         BIGINT       NOT NULL REFERENCES brands (id),
    sku_code         VARCHAR(64)  NOT NULL UNIQUE,
    name             VARCHAR(255) NOT NULL,
    product_type     VARCHAR(128),
    sheet_count      INTEGER,
    ply              INTEGER,
    packs_per_carton INTEGER,
    units_per_pack   INTEGER,
    units_per_carton INTEGER,
    active           BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_product_skus_brand_id ON product_skus (brand_id);

-- === Content module ===
CREATE TABLE content_briefs (
    id              BIGSERIAL    PRIMARY KEY,
    brand_id        BIGINT       NOT NULL REFERENCES brands (id),
    channel         VARCHAR(128) NOT NULL,
    content_goal    VARCHAR(255) NOT NULL,
    audience        VARCHAR(255),
    product         VARCHAR(255),
    number_of_ideas INTEGER      NOT NULL,
    status          VARCHAR(32)  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_content_briefs_brand_id ON content_briefs (brand_id);

CREATE TABLE generated_contents (
    id               BIGSERIAL   PRIMARY KEY,
    content_brief_id BIGINT      NOT NULL REFERENCES content_briefs (id),
    output           TEXT,
    quality_score    DOUBLE PRECISION,
    is_safe          BOOLEAN,
    provider         VARCHAR(128),
    created_at       TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_generated_contents_brief_id ON generated_contents (content_brief_id);

-- === Campaign module ===
CREATE TABLE campaign_briefs (
    id             BIGSERIAL    PRIMARY KEY,
    brand_id       BIGINT       NOT NULL REFERENCES brands (id),
    objective      VARCHAR(255) NOT NULL,
    channel        VARCHAR(128),
    audience       VARCHAR(255),
    product        VARCHAR(255),
    duration_weeks INTEGER      NOT NULL,
    status         VARCHAR(32)  NOT NULL,
    created_at     TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_campaign_briefs_brand_id ON campaign_briefs (brand_id);

CREATE TABLE generated_campaigns (
    id                BIGSERIAL   PRIMARY KEY,
    campaign_brief_id BIGINT      NOT NULL REFERENCES campaign_briefs (id),
    output            TEXT,
    quality_score     DOUBLE PRECISION,
    is_safe           BOOLEAN,
    provider          VARCHAR(128),
    created_at        TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_generated_campaigns_brief_id ON generated_campaigns (campaign_brief_id);

-- === Reseller module ===
CREATE TABLE reseller_leads (
    id            BIGSERIAL    PRIMARY KEY,
    brand_id      BIGINT       NOT NULL REFERENCES brands (id),
    name          VARCHAR(255) NOT NULL,
    contact       VARCHAR(255),
    reseller_type VARCHAR(128),
    location      VARCHAR(255),
    notes         TEXT,
    created_at    TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_reseller_leads_brand_id ON reseller_leads (brand_id);

CREATE TABLE reseller_message_briefs (
    id               BIGSERIAL    PRIMARY KEY,
    brand_id         BIGINT       NOT NULL REFERENCES brands (id),
    reseller_lead_id BIGINT       NOT NULL REFERENCES reseller_leads (id),
    message_goal     VARCHAR(255) NOT NULL,
    tone             VARCHAR(128),
    product          VARCHAR(255),
    status           VARCHAR(32)  NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_reseller_message_briefs_lead_id ON reseller_message_briefs (reseller_lead_id);

CREATE TABLE generated_reseller_messages (
    id                        BIGSERIAL   PRIMARY KEY,
    reseller_message_brief_id BIGINT      NOT NULL REFERENCES reseller_message_briefs (id),
    output                    TEXT,
    quality_score             DOUBLE PRECISION,
    is_safe                   BOOLEAN,
    provider                  VARCHAR(128),
    created_at                TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_generated_reseller_messages_brief_id
    ON generated_reseller_messages (reseller_message_brief_id);
