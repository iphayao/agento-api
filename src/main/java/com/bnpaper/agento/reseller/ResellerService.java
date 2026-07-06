package com.bnpaper.agento.reseller;

import com.bnpaper.agento.agent.AgentClient;
import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.agent.dto.GenerateResellerMessageRequest;
import com.bnpaper.agento.brand.Brand;
import com.bnpaper.agento.brand.BrandService;
import com.bnpaper.agento.common.domain.BriefStatus;
import com.bnpaper.agento.common.error.ResourceNotFoundException;
import com.bnpaper.agento.reseller.dto.CreateResellerLeadRequest;
import com.bnpaper.agento.reseller.dto.CreateResellerMessageBriefRequest;
import com.bnpaper.agento.reseller.dto.GeneratedResellerMessageResponse;
import com.bnpaper.agento.reseller.dto.ResellerLeadResponse;
import com.bnpaper.agento.reseller.dto.ResellerMessageBriefResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResellerService {

    private final ResellerLeadRepository leadRepository;
    private final ResellerMessageBriefRepository briefRepository;
    private final GeneratedResellerMessageRepository generatedRepository;
    private final BrandService brandService;
    private final AgentClient agentClient;
    private final ResellerMapper mapper;

    // --- Leads ---

    @Transactional
    public ResellerLeadResponse createLead(CreateResellerLeadRequest request) {
        brandService.requireBrand(request.brandId());
        ResellerLead lead = ResellerLead.builder()
                .brandId(request.brandId())
                .name(request.name())
                .contact(request.contact())
                .resellerType(request.resellerType())
                .location(request.location())
                .notes(request.notes())
                .createdAt(Instant.now())
                .build();
        return mapper.toResponse(leadRepository.save(lead));
    }

    @Transactional(readOnly = true)
    public List<ResellerLeadResponse> listLeads() {
        return leadRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ResellerLeadResponse getLead(Long id) {
        return mapper.toResponse(requireLead(id));
    }

    // --- Message briefs ---

    @Transactional
    public ResellerMessageBriefResponse createMessageBrief(CreateResellerMessageBriefRequest request) {
        brandService.requireBrand(request.brandId());
        requireLead(request.resellerLeadId());
        ResellerMessageBrief brief = ResellerMessageBrief.builder()
                .brandId(request.brandId())
                .resellerLeadId(request.resellerLeadId())
                .messageGoal(request.messageGoal())
                .tone(request.tone())
                .product(request.product())
                .status(BriefStatus.NEW)
                .createdAt(Instant.now())
                .build();
        return mapper.toResponse(briefRepository.save(brief));
    }

    /**
     * Delegates reseller message generation to agento-agent and persists the result.
     * Not wrapped in a single transaction so terminal status writes commit
     * independently of the agent call outcome.
     */
    public GeneratedResellerMessageResponse generate(Long messageBriefId) {
        ResellerMessageBrief brief = requireBrief(messageBriefId);
        Brand brand = brandService.requireBrand(brief.getBrandId());
        ResellerLead lead = requireLead(brief.getResellerLeadId());

        brief.setStatus(BriefStatus.GENERATING);
        briefRepository.save(brief);

        try {
            AgentGenerationResult result = agentClient.generateResellerMessage(new GenerateResellerMessageRequest(
                    brand.getCode(),
                    brief.getProduct(),
                    lead.getName(),
                    lead.getResellerType(),
                    brief.getMessageGoal(),
                    brief.getTone()
            ));

            GeneratedResellerMessage saved = generatedRepository.save(GeneratedResellerMessage.builder()
                    .resellerMessageBriefId(brief.getId())
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

    private ResellerLead requireLead(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ResellerLead", id));
    }

    private ResellerMessageBrief requireBrief(Long id) {
        return briefRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ResellerMessageBrief", id));
    }
}
