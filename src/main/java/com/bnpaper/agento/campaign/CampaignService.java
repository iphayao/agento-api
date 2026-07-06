package com.bnpaper.agento.campaign;

import com.bnpaper.agento.agent.AgentClient;
import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.agent.dto.GenerateCampaignRequest;
import com.bnpaper.agento.brand.Brand;
import com.bnpaper.agento.brand.BrandService;
import com.bnpaper.agento.campaign.dto.CampaignBriefResponse;
import com.bnpaper.agento.campaign.dto.CreateCampaignBriefRequest;
import com.bnpaper.agento.campaign.dto.GeneratedCampaignResponse;
import com.bnpaper.agento.common.domain.BriefStatus;
import com.bnpaper.agento.common.error.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignBriefRepository briefRepository;
    private final GeneratedCampaignRepository generatedRepository;
    private final BrandService brandService;
    private final AgentClient agentClient;
    private final CampaignMapper mapper;

    @Transactional
    public CampaignBriefResponse createBrief(CreateCampaignBriefRequest request) {
        brandService.requireBrand(request.brandId());
        CampaignBrief brief = CampaignBrief.builder()
                .brandId(request.brandId())
                .objective(request.objective())
                .channel(request.channel())
                .audience(request.audience())
                .product(request.product())
                .durationWeeks(request.durationWeeks())
                .status(BriefStatus.NEW)
                .createdAt(Instant.now())
                .build();
        return mapper.toResponse(briefRepository.save(brief));
    }

    @Transactional(readOnly = true)
    public List<CampaignBriefResponse> listBriefs() {
        return briefRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CampaignBriefResponse getBrief(Long id) {
        return mapper.toResponse(requireBrief(id));
    }

    /**
     * Delegates weekly campaign generation to agento-agent and persists the result.
     * Not wrapped in a single transaction so terminal status writes commit
     * independently of the agent call outcome.
     */
    public GeneratedCampaignResponse generate(Long briefId) {
        CampaignBrief brief = requireBrief(briefId);
        Brand brand = brandService.requireBrand(brief.getBrandId());

        brief.setStatus(BriefStatus.GENERATING);
        briefRepository.save(brief);

        try {
            AgentGenerationResult result = agentClient.generateWeeklyCampaign(new GenerateCampaignRequest(
                    brand.getCode(),
                    brief.getProduct(),
                    brief.getObjective(),
                    brief.getChannel(),
                    brief.getAudience(),
                    brief.getDurationWeeks()
            ));

            GeneratedCampaign saved = generatedRepository.save(GeneratedCampaign.builder()
                    .campaignBriefId(brief.getId())
                    .output(result.output())
                    .qualityScore(result.qualityScore())
                    .isSafe(result.isSafe())
                    .provider(result.provider())
                    .createdAt(Instant.now())
                    .build());

            brief.setStatus(BriefStatus.GENERATED);
            briefRepository.save(brief);

            return mapper.toResponse(saved);
        } catch (RuntimeException ex) {
            brief.setStatus(BriefStatus.FAILED);
            briefRepository.save(brief);
            throw ex;
        }
    }

    private CampaignBrief requireBrief(Long id) {
        return briefRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("CampaignBrief", id));
    }
}
