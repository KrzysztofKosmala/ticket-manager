package pl.ticket.aiagent.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RuntimeConfigurationStartupLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeConfigurationStartupLogger.class);

    private static final String MCP_CLIENT_ENABLED = "spring.ai.mcp.client.enabled";
    private static final String MCP_GATEWAY_URL = "spring.ai.mcp.client.sse.connections.ai-tools-gateway.url";
    private static final String MCP_GATEWAY_SSE_ENDPOINT = "spring.ai.mcp.client.sse.connections.ai-tools-gateway.sse-endpoint";

    private final Environment environment;

    public RuntimeConfigurationStartupLogger(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logRuntimeConfiguration() {
        LOGGER.info("AI agent active profiles: {}", Arrays.toString(environment.getActiveProfiles()));
        LOGGER.info("AI agent configured spring.profiles.active: {}", environment.getProperty("spring.profiles.active", "<not set>"));
        LOGGER.info("AI agent MCP client enabled: {}", environment.getProperty(MCP_CLIENT_ENABLED, "<not set>"));
        LOGGER.info("AI agent MCP ai-tools-gateway URL: {}", environment.getProperty(MCP_GATEWAY_URL, "<not set>"));
        LOGGER.info("AI agent MCP ai-tools-gateway SSE endpoint: {}", environment.getProperty(MCP_GATEWAY_SSE_ENDPOINT, "<not set>"));
    }
}
