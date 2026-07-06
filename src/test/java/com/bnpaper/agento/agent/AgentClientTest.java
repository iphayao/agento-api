package com.bnpaper.agento.agent;

import com.bnpaper.agento.agent.dto.AgentGenerationResult;
import com.bnpaper.agento.agent.dto.GenerateContentRequest;
import com.bnpaper.agento.common.error.AgentGenerationFailedException;
import com.bnpaper.agento.common.error.AgentServiceUnavailableException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Exercises {@link AgentClient} against a mock HTTP server so no real
 * agento-agent is required.
 */
class AgentClientTest {

    private MockWebServer server;
    private AgentClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        WebClient webClient = WebClient.builder().baseUrl(server.url("/").toString()).build();
        client = new AgentClient(webClient, new AgentProperties(server.url("/").toString(), 2000));
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    private GenerateContentRequest sampleRequest() {
        return new GenerateContentRequest("SOCLEAN", "facial tissue", "facebook", "awareness", "homes", 3);
    }

    @Test
    void generateContent_returnsMappedResult() {
        server.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {"output":"3 clean ideas","qualityScore":0.92,"isSafe":true,"provider":"claude"}
                        """));

        AgentGenerationResult result = client.generateContent(sampleRequest());

        assertThat(result.output()).isEqualTo("3 clean ideas");
        assertThat(result.qualityScore()).isEqualTo(0.92);
        assertThat(result.isSafe()).isTrue();
        assertThat(result.provider()).isEqualTo("claude");
    }

    @Test
    void generateContent_serverError_mapsToUnavailable() {
        server.enqueue(new MockResponse().setResponseCode(503));

        assertThatThrownBy(() -> client.generateContent(sampleRequest()))
                .isInstanceOf(AgentServiceUnavailableException.class);
    }

    @Test
    void generateContent_badRequest_mapsToGenerationFailed() {
        server.enqueue(new MockResponse().setResponseCode(400).setBody("bad input"));

        assertThatThrownBy(() -> client.generateContent(sampleRequest()))
                .isInstanceOf(AgentGenerationFailedException.class);
    }

    @Test
    void generateContent_connectionRefused_mapsToUnavailable() throws IOException {
        server.shutdown(); // nothing listening now
        assertThatThrownBy(() -> client.generateContent(sampleRequest()))
                .isInstanceOf(AgentServiceUnavailableException.class);
    }
}
