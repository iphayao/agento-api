package com.bnpaper.agento.agent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QualityCheckResult(
        Double qualityScore,
        Boolean isSafe,
        List<String> issues
) {
}
