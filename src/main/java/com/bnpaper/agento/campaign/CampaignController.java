package com.bnpaper.agento.campaign;

import com.bnpaper.agento.campaign.dto.CampaignBriefResponse;
import com.bnpaper.agento.campaign.dto.CreateCampaignBriefRequest;
import com.bnpaper.agento.campaign.dto.GeneratedCampaignResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping("/briefs")
    @ResponseStatus(HttpStatus.CREATED)
    public CampaignBriefResponse createBrief(@Valid @RequestBody CreateCampaignBriefRequest request) {
        return campaignService.createBrief(request);
    }

    @GetMapping("/briefs")
    public List<CampaignBriefResponse> listBriefs() {
        return campaignService.listBriefs();
    }

    @GetMapping("/briefs/{id}")
    public CampaignBriefResponse getBrief(@PathVariable Long id) {
        return campaignService.getBrief(id);
    }

    @PostMapping("/briefs/{id}/generate")
    public GeneratedCampaignResponse generate(@PathVariable Long id) {
        return campaignService.generate(id);
    }
}
