package com.bnpaper.agento.content;

import com.bnpaper.agento.agent.AgentClient;
import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.agent.dto.GenerateContentRequest;
import com.bnpaper.agento.brand.Brand;
import com.bnpaper.agento.brand.BrandService;
import com.bnpaper.agento.common.domain.BriefStatus;
import com.bnpaper.agento.common.error.ResourceNotFoundException;
import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.bnpaper.agento.content.dto.CreateContentBriefRequest;
import com.bnpaper.agento.content.dto.GeneratedContentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentBriefRepository briefRepository;
    private final GeneratedContentRepository generatedRepository;
    private final BrandService brandService;
    private final AgentClient agentClient;
    private final ContentMapper mapper;

    @Transactional
    public ContentBriefResponse createBrief(CreateContentBriefRequest request) {
        brandService.requireBrand(request.brandId());
        ContentBrief brief = ContentBrief.builder()
                .brandId(request.brandId())
                .channel(request.channel())
                .contentGoal(request.contentGoal())
                .audience(request.audience())
                .product(request.product())
                .numberOfIdeas(request.numberOfIdeas())
                .status(BriefStatus.NEW)
                .createdAt(Instant.now())
                .build();
        return mapper.toResponse(briefRepository.save(brief));
    }

    @Transactional(readOnly = true)
    public List<ContentBriefResponse> listBriefs() {
        return briefRepository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ContentBriefResponse getBrief(Long id) {
        return mapper.toResponse(requireBrief(id));
    }

    @Transactional(readOnly = true)
    public GeneratedContentResponse getGenerated(Long id) {
        GeneratedContent generated = generatedRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("GeneratedContent", id));
        return mapper.toResponse(generated);
    }

    /**
     * Delegates generation to agento-agent and persists the result. The brief
     * status reflects the outcome; agent failures propagate as domain exceptions.
     *
     * <p>Intentionally not wrapped in a single transaction so that the terminal
     * {@code GENERATING}/{@code GENERATED}/{@code FAILED} status writes each
     * commit independently of the (potentially failing) agent call.
     */
    public GeneratedContentResponse generate(Long briefId) {
        ContentBrief brief = requireBrief(briefId);
        Brand brand = brandService.requireBrand(brief.getBrandId());

        brief.setStatus(BriefStatus.GENERATING);
        briefRepository.save(brief);

        try {
            AgentGenerationResult result = agentClient.generateContent(new GenerateContentRequest(
                    brand.getCode(),
                    brief.getProduct(),
                    brief.getChannel(),
                    brief.getContentGoal(),
                    brief.getAudience(),
                    brief.getNumberOfIdeas()
            ));

            GeneratedContent saved = generatedRepository.save(GeneratedContent.builder()
                    .contentBriefId(brief.getId())
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

    private ContentBrief requireBrief(Long id) {
        return briefRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("ContentBrief", id));
    }
}
