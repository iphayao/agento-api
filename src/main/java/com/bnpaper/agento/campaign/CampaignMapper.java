package com.bnpaper.agento.campaign;

import com.bnpaper.agento.campaign.dto.CampaignBriefResponse;
import com.bnpaper.agento.campaign.dto.GeneratedCampaignResponse;
import org.mapstruct.Mapper;

@Mapper
public interface CampaignMapper {

    CampaignBriefResponse toResponse(CampaignBrief brief);

    GeneratedCampaignResponse toResponse(GeneratedCampaign campaign);
}
