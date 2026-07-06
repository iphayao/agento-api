package com.bnpaper.agento.reseller;

import com.bnpaper.agento.reseller.dto.GeneratedResellerMessageResponse;
import com.bnpaper.agento.reseller.dto.ResellerLeadResponse;
import com.bnpaper.agento.reseller.dto.ResellerMessageBriefResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ResellerMapper {

    ResellerLeadResponse toResponse(ResellerLead lead);

    ResellerMessageBriefResponse toResponse(ResellerMessageBrief brief);

    GeneratedResellerMessageResponse toResponse(GeneratedResellerMessage message);
}
