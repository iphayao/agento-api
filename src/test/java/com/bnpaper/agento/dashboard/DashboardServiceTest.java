package com.bnpaper.agento.dashboard;

import com.bnpaper.agento.campaign.CampaignBriefRepository;
import com.bnpaper.agento.content.ContentBriefRepository;
import com.bnpaper.agento.content.GeneratedContentRepository;
import com.bnpaper.agento.dashboard.dto.DashboardSummaryResponse;
import com.bnpaper.agento.reseller.GeneratedResellerMessageRepository;
import com.bnpaper.agento.reseller.ResellerLeadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock ContentBriefRepository contentBriefRepository;
    @Mock GeneratedContentRepository generatedContentRepository;
    @Mock CampaignBriefRepository campaignBriefRepository;
    @Mock ResellerLeadRepository resellerLeadRepository;
    @Mock GeneratedResellerMessageRepository generatedResellerMessageRepository;

    @InjectMocks
    DashboardService service;

    @Test
    void summary_aggregatesAcrossModules() {
        when(contentBriefRepository.count()).thenReturn(5L);
        when(generatedContentRepository.count()).thenReturn(4L);
        when(generatedContentRepository.averageQualityScore()).thenReturn(0.85);
        when(generatedContentRepository.countByIsSafeFalse()).thenReturn(1L);
        when(campaignBriefRepository.count()).thenReturn(2L);
        when(resellerLeadRepository.count()).thenReturn(3L);
        when(generatedResellerMessageRepository.count()).thenReturn(6L);

        DashboardSummaryResponse summary = service.summary();

        assertThat(summary.totalContentBriefs()).isEqualTo(5L);
        assertThat(summary.totalGeneratedContents()).isEqualTo(4L);
        assertThat(summary.averageQualityScore()).isEqualTo(0.85);
        assertThat(summary.unsafeContentCount()).isEqualTo(1L);
        assertThat(summary.totalCampaignBriefs()).isEqualTo(2L);
        assertThat(summary.totalResellerLeads()).isEqualTo(3L);
        assertThat(summary.totalResellerMessages()).isEqualTo(6L);
    }
}
