package pl.ticket.aiagent.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationConfigurationSafetyTest {

    @Test
    void shouldImportDedicatedToolRegistryYamlFromSharedApplicationYaml() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties)
                .containsEntry("spring.config.import[0]", "optional:classpath:ai-agent-tools.yml");
    }

    @Test
    void shouldKeepToolRegistryOutsideSharedApplicationYaml() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties)
                .doesNotContainKey("ai-agent.tools.enforce-scopes")
                .doesNotContainKey("ai-agent.tools.registry[tm_orders_search].enabled")
                .doesNotContainKey("ai-agent.tools.registry[tm_orders_search].source")
                .doesNotContainKey("ai-agent.tools.registry[tm_orders_search].access-mode")
                .doesNotContainKey("ai-agent.tools.registry[tm_orders_search].required-scopes[0]");
    }

    @Test
    void shouldLoadToolRegistryFromDedicatedYaml() {
        Properties properties = loadYaml("ai-agent-tools.yml");

        assertThat(properties)
                .containsEntry("ai-agent.tools.enforce-scopes", false)
                .containsEntry("ai-agent.tools.registry[tm_orders_search].enabled", true)
                .containsEntry("ai-agent.tools.registry[tm_orders_search].source", "INTERNAL_MCP")
                .containsEntry("ai-agent.tools.registry[tm_orders_search].access-mode", "READ")
                .containsEntry("ai-agent.tools.registry[tm_orders_search].required-scopes[0]", "tools:orders.read")
                .containsEntry("ai-agent.tools.registry[tm_smoke_ping].enabled", true)
                .containsEntry("ai-agent.tools.registry[tm_smoke_ping].source", "INTERNAL_MCP")
                .containsEntry("ai-agent.tools.registry[tm_smoke_ping].access-mode", "READ");
    }

    @Test
    void shouldKeepLocalRuntimeSettingsOutsideSharedApplicationYaml() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties)
                .doesNotContainKey("spring.ai.mcp.client.enabled")
                .doesNotContainKey("spring.ai.ollama.base-url")
                .doesNotContainKey("spring.ai.ollama.chat.options.model")
                .doesNotContainKey("spring.security.oauth2.resourceserver.jwt.issuer-uri")
                .doesNotContainKey("eureka.client.enabled")
                .doesNotContainKey("openapi.service.url")
                .doesNotContainKey("spring.datasource.url")
                .doesNotContainKey("spring.datasource.username")
                .doesNotContainKey("spring.datasource.password")
                .doesNotContainKey("spring.jpa.hibernate.ddl-auto")
                .doesNotContainKey("spring.jpa.show-sql")
                .doesNotContainKey("spring.sql.init.mode")
                .doesNotContainKey("spring.liquibase.drop-first");

        assertThat(stringValues(properties))
                .noneMatch(value -> value.contains("localhost"))
                .noneMatch(value -> value.contains("127.0.0.1"))
                .noneMatch(value -> value.startsWith("jdbc:postgresql://"))
                .noneMatch(value -> value.startsWith("${AI_AGENT_"));
    }

    @Test
    void shouldLoadLocalRuntimeSettingsFromLocalProfileYaml() {
        Properties properties = loadYaml("application-local.yml");

        assertThat(properties)
                .containsEntry("spring.ai.mcp.client.enabled", true)
                .containsEntry("spring.ai.mcp.client.sse.connections.ai-tools-gateway.url", "http://localhost:8105")
                .containsEntry("spring.ai.mcp.client.sse.connections.ai-tools-gateway.sse-endpoint", "/sse")
                .containsEntry("spring.ai.ollama.base-url", "http://127.0.0.1:7869")
                .containsEntry("spring.ai.ollama.chat.options.model", "qwen3:8b")
                .containsEntry("spring.security.oauth2.resourceserver.jwt.issuer-uri", "http://localhost:8085/realms/ticket-manager-realm")
                .containsEntry("eureka.client.service-url.defaultZone", "http://localhost:8761/eureka")
                .containsEntry("openapi.service.url", "http://localhost:8099")
                .containsEntry("management.zipkin.tracing.endpoint", "http://localhost:9411/api/v2/spans");

        assertThat(properties)
                .doesNotContainKey("spring.datasource.url")
                .doesNotContainKey("spring.jpa.hibernate.ddl-auto")
                .doesNotContainKey("spring.liquibase.drop-first");
    }

    @Test
    void shouldLoadProductionRuntimeSettingsFromProdProfileYaml() {
        Properties properties = loadYaml("application-prod.yml");

        assertThat(properties)
                .containsEntry("spring.ai.mcp.client.enabled", "${AI_AGENT_MCP_CLIENT_ENABLED:false}")
                .containsEntry("spring.ai.mcp.client.sse.connections.ai-tools-gateway.url", "${AI_AGENT_MCP_GATEWAY_URL}")
                .containsEntry("spring.ai.ollama.base-url", "${AI_AGENT_OLLAMA_BASE_URL}")
                .containsEntry("spring.ai.ollama.chat.options.model", "${AI_AGENT_OLLAMA_MODEL}")
                .containsEntry("spring.security.oauth2.resourceserver.jwt.issuer-uri", "${AI_AGENT_JWT_ISSUER_URI}")
                .containsEntry("eureka.client.service-url.defaultZone", "${AI_AGENT_EUREKA_DEFAULT_ZONE}")
                .containsEntry("openapi.service.url", "${AI_AGENT_OPENAPI_URL}")
                .containsEntry("management.zipkin.tracing.endpoint", "${AI_AGENT_ZIPKIN_TRACING_ENDPOINT}");
    }

    @Test
    void shouldLoadTestRuntimeSettingsFromTestProfileYaml() {
        Properties properties = loadYaml("application-test.yml");

        assertThat(properties)
                .containsEntry("spring.ai.mcp.client.enabled", false)
                .containsEntry("spring.ai.ollama.base-url", "http://localhost:11434")
                .containsEntry("spring.ai.ollama.chat.options.model", "test-model")
                .containsEntry("spring.security.oauth2.resourceserver.jwt.issuer-uri", "http://localhost:8085/realms/test")
                .containsEntry("eureka.client.enabled", false)
                .containsEntry("openapi.service.url", "http://localhost:8099");
    }

    private Properties loadYaml(String resourceName) {
        ClassPathResource resource = new ClassPathResource(resourceName);
        assertThat(resource.exists())
                .as("%s should exist on classpath", resourceName)
                .isTrue();

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(resource);
        return Objects.requireNonNull(yaml.getObject());
    }

    private List<String> stringValues(Properties properties) {
        return properties.values().stream()
                .map(Object::toString)
                .toList();
    }
}
