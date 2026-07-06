package com.bnpaper.agento.content;

import com.bnpaper.agento.common.domain.BriefStatus;
import com.bnpaper.agento.content.dto.ContentBriefResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentController.class)
class ContentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ContentService contentService;

    @Test
    void createBrief_happyPath_returns201() throws Exception {
        when(contentService.createBrief(any())).thenReturn(new ContentBriefResponse(
                1L, 1L, "facebook", "awareness", "homes", "facial tissue", 3,
                BriefStatus.NEW, Instant.parse("2026-01-01T00:00:00Z")));

        String body = objectMapper.writeValueAsString(Map.of(
                "brandId", 1,
                "channel", "facebook",
                "contentGoal", "awareness",
                "audience", "homes",
                "product", "facial tissue",
                "numberOfIdeas", 3));

        mockMvc.perform(post("/api/v1/content/briefs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void createBrief_missingRequiredFields_returnsValidationError() throws Exception {
        String body = objectMapper.writeValueAsString(Map.of(
                "channel", "facebook")); // missing brandId, contentGoal, numberOfIdeas

        mockMvc.perform(post("/api/v1/content/briefs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.message").value("Invalid request"));
    }
}
