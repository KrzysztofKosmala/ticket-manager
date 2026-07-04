package pl.ticket.aiagent.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class RuntimeConfigurationStartupLoggerTest {

    @Test
    void shouldLogActiveProfilesAndMcpClientConfiguration(CapturedOutput output) {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("spring.profiles.active", "local")
                .withProperty("spring.ai.mcp.client.enabled", "true")
                .withProperty("spring.ai.mcp.client.sse.connections.ai-tools-gateway.url", "http://localhost:8105")
                .withProperty("spring.ai.mcp.client.sse.connections.ai-tools-gateway.sse-endpoint", "/sse");
        environment.setActiveProfiles("local");
        RuntimeConfigurationStartupLogger logger = new RuntimeConfigurationStartupLogger(environment);

        logger.logRuntimeConfiguration();

        assertThat(output)
                .contains("AI agent active profiles: [local]")
                .contains("AI agent configured spring.profiles.active: local")
                .contains("AI agent MCP client enabled: true")
                .contains("AI agent MCP ai-tools-gateway URL: http://localhost:8105")
                .contains("AI agent MCP ai-tools-gateway SSE endpoint: /sse");
    }
}
