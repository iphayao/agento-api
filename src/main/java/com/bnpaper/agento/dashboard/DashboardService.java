package com.bnpaper.agento.dashboard;

import com.bnpaper.agento.campaign.CampaignBriefRepository;
import com.bnpaper.agento.content.ContentBriefRepository;
import com.bnpaper.agento.content.GeneratedContentRepository;
import com.bnpaper.agento.dashboard.dto.DashboardSummaryResponse;
import com.bnpaper.agento.reseller.GeneratedResellerMessageRepository;
import com.bnpaper.agento.reseller.ResellerLeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Read-only aggregation over the feature modules. It reads repositories from
 * other modules directly; acceptable within a modular monolith for reporting.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ContentBriefRepository contentBriefRepository;
    private final GeneratedContentRepository generatedContentRepository;
    private final CampaignBriefRepository campaignBriefRepository;
    private final ResellerLeadRepository resellerLeadRepository;
    private final GeneratedResellerMessageRepository generatedResellerMessageRepository;

    public DashboardSummaryResponse summary() {
        return new DashboardSummaryResponse(
                contentBriefRepository.count(),
                generatedContentRepository.count(),
                generatedContentRepository.averageQualityScore(),
                generatedContentRepository.countByIsSafeFalse(),
                campaignBriefRepository.count(),
                resellerLeadRepository.count(),
                generatedResellerMessageRepository.count()
        );
    }
}
