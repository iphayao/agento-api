package com.bnpaper.agento.content;

import com.bnpaper.agento.agent.AgentClient;
import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.brand.Brand;
import com.bnpaper.agento.brand.BrandService;
import com.bnpaper.agento.common.domain.BriefStatus;
import com.bnpaper.agento.common.error.AgentServiceUnavailableException;
import com.bnpaper.agento.common.error.ResourceNotFoundException;
import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.bnpaper.agento.content.dto.CreateContentBriefRequest;
import com.bnpaper.agento.content.dto.GeneratedContentResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    ContentBriefRepository briefRepository;
    @Mock
    GeneratedContentRepository generatedRepository;
    @Mock
    BrandService brandService;
    @Mock
    AgentClient agentClient;
    @Spy
    ContentMapper mapper = Mappers.getMapper(ContentMapper.class);

    @InjectMocks
    ContentService service;

    private Brand soClean() {
        return Brand.builder().id(1L).code("SOCLEAN").name("SoClean").active(true).build();
    }

    private ContentBrief brief() {
        return ContentBrief.builder()
                .id(10L).brandId(1L).channel("facebook").contentGoal("awareness")
                .audience("homes").product("facial tissue").numberOfIdeas(3)
                .status(BriefStatus.NEW).createdAt(java.time.Instant.now()).build();
    }

    @Test
    void createBrief_persistsWithNewStatus() {
        when(brandService.requireBrand(1L)).thenReturn(soClean());
        when(briefRepository.save(any(ContentBrief.class))).thenAnswer(inv -> {
            ContentBrief b = inv.getArgument(0);
            b.setId(10L);
            return b;
        });

        ContentBriefResponse response = service.createBrief(
                new CreateContentBriefRequest(1L, "facebook", "awareness", "homes", "facial tissue", 3));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo(BriefStatus.NEW);
        assertThat(response.channel()).isEqualTo("facebook");
    }

    @Test
    void generate_happyPath_savesGeneratedContentAndMarksGenerated() {
        when(briefRepository.findById(10L)).thenReturn(Optional.of(brief()));
        when(brandService.requireBrand(1L)).thenReturn(soClean());
        when(briefRepository.save(any(ContentBrief.class))).thenAnswer(inv -> inv.getArgument(0));
        when(agentClient.generateContent(any()))
                .thenReturn(new AgentGenerationResult("idea 1\nidea 2\nidea 3", 0.9, true, "claude"));
        when(generatedRepository.save(any(GeneratedContent.class))).thenAnswer(inv -> {
            GeneratedContent g = inv.getArgument(0);
            g.setId(100L);
            return g;
        });

        GeneratedContentResponse response = service.generate(10L);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.provider()).isEqualTo("claude");
        assertThat(response.qualityScore()).isEqualTo(0.9);

        ArgumentCaptor<ContentBrief> briefCaptor = ArgumentCaptor.forClass(ContentBrief.class);
        verify(briefRepository, atLeastOnce()).save(briefCaptor.capture());
        assertThat(briefCaptor.getValue().getStatus()).isEqualTo(BriefStatus.GENERATED);
    }

    @Test
    void generate_agentUnavailable_marksBriefFailedAndPropagates() {
        when(briefRepository.findById(10L)).thenReturn(Optional.of(brief()));
        when(brandService.requireBrand(1L)).thenReturn(soClean());
        when(briefRepository.save(any(ContentBrief.class))).thenAnswer(inv -> inv.getArgument(0));
        when(agentClient.generateContent(any()))
                .thenThrow(new AgentServiceUnavailableException("down"));

        assertThatThrownBy(() -> service.generate(10L))
                .isInstanceOf(AgentServiceUnavailableException.class);

        ArgumentCaptor<ContentBrief> briefCaptor = ArgumentCaptor.forClass(ContentBrief.class);
        verify(briefRepository, atLeastOnce()).save(briefCaptor.capture());
        assertThat(briefCaptor.getValue().getStatus()).isEqualTo(BriefStatus.FAILED);
    }

    @Test
    void getBrief_missing_throwsNotFound() {
        when(briefRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getBrief(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
