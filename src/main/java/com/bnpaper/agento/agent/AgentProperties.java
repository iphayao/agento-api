package com.bnpaper.agento.agent;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds the {@code agento.agent.*} configuration namespace.
 */
@ConfigurationProperties(prefix = "agento.agent")
public record AgentProperties(
        String baseUrl,
        Integer timeoutMs
) {
    public AgentProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:8081";
        }
        if (timeoutMs == null) {
            timeoutMs = 30_000;
        }
    }
}
