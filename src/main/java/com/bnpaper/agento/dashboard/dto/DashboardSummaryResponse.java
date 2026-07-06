package com.bnpaper.agento.dashboard.dto;

public record DashboardSummaryResponse(
        long totalContentBriefs,
        long totalGeneratedContents,
        Double averageQualityScore,
        long unsafeContentCount,
        long totalCampaignBriefs,
        long totalResellerLeads,
        long totalResellerMessages
) {
}
