package com.bnpaper.agento.integration;

import com.bnpaper.agento.brand.BrandRepository;
import com.bnpaper.agento.brand.ProductSku;
import com.bnpaper.agento.brand.ProductSkuRepository;
import com.bnpaper.agento.content.ContentService;
import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.bnpaper.agento.content.dto.CreateContentBriefRequest;
import com.bnpaper.agento.content.dto.GeneratedContentResponse;
import com.bnpaper.agento.dashboard.DashboardService;
import com.bnpaper.agento.dashboard.dto.DashboardSummaryResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end test on a real PostgreSQL (Testcontainers) with real Flyway
 * migrations. The agento-agent dependency is faked with a MockWebServer, so no
 * live agent is required.
 */
@Testcontainers
@SpringBootTest
class AgentoApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    static MockWebServer agent;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) throws IOException {
        agent = new MockWebServer();
        agent.start();

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("agento.agent.base-url", () -> agent.url("/").toString());
    }

    @Autowired
    BrandRepository brandRepository;
    @Autowired
    ProductSkuRepository productSkuRepository;
    @Autowired
    ContentService contentService;
    @Autowired
    DashboardService dashboardService;

    @Test
    void flywaySeedsSoCleanBrandAndSku() {
        var brand = brandRepository.findByCode("SOCLEAN");
        assertThat(brand).isPresent();
        assertThat(brand.get().getName()).isEqualTo("SoClean");

        List<ProductSku> skus = productSkuRepository.findByBrandId(brand.get().getId());
        assertThat(skus).hasSize(1);
        ProductSku sku = skus.get(0);
        assertThat(sku.getSkuCode()).isEqualTo("SOCLEAN-FT-180");
        assertThat(sku.getSheetCount()).isEqualTo(180);
        assertThat(sku.getPly()).isEqualTo(2);
        assertThat(sku.getPacksPerCarton()).isEqualTo(10);
        assertThat(sku.getUnitsPerPack()).isEqualTo(5);
        assertThat(sku.getUnitsPerCarton()).isEqualTo(50);
    }

    @Test
    void contentBriefFlow_createGenerateAndSummarize() {
        var brand = brandRepository.findByCode("SOCLEAN").orElseThrow();

        ContentBriefResponse brief = contentService.createBrief(new CreateContentBriefRequest(
                brand.getId(), "facebook", "awareness", "homes", "facial tissue", 3));
        assertThat(brief.id()).isNotNull();

        agent.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"output":"clean idea","qualityScore":0.88,"isSafe":true,"provider":"claude"}
                        """));

        GeneratedContentResponse generated = contentService.generate(brief.id());
        assertThat(generated.output()).isEqualTo("clean idea");
        assertThat(generated.isSafe()).isTrue();

        DashboardSummaryResponse summary = dashboardService.summary();
        assertThat(summary.totalContentBriefs()).isGreaterThanOrEqualTo(1);
        assertThat(summary.totalGeneratedContents()).isGreaterThanOrEqualTo(1);
        assertThat(summary.averageQualityScore()).isNotNull();
    }
}
