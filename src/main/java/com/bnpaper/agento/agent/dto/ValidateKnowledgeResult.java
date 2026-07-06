package com.bnpaper.agento.agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ValidateKnowledgeResult(
        Boolean valid,
        List<String> issues
) {
}
