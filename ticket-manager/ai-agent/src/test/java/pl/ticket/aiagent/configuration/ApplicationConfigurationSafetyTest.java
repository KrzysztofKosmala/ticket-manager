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
    void shouldNotForceActiveSpringProfileFromSharedApplicationYaml() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties).doesNotContainKey("spring.profiles.active");
    }

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
                .doesNotContainKey("ai-agent.tools.registry[tm.orders.search].enabled")
                .doesNotContainKey("ai-agent.tools.registry[tm.orders.search].source")
                .doesNotContainKey("ai-agent.tools.registry[tm.orders.search].access-mode")
                .doesNotContainKey("ai-agent.tools.registry[tm.orders.search].required-scopes[0]");
    }

    @Test
    void shouldLoadToolRegistryFromDedicatedYaml() {
        Properties properties = loadYaml("ai-agent-tools.yml");

        assertThat(properties)
                .containsEntry("ai-agent.tools.enforce-scopes", false)
                .containsEntry("ai-agent.tools.registry[tm.orders.search].enabled", true)
                .containsEntry("ai-agent.tools.registry[tm.orders.search].source", "INTERNAL_MCP")
                .containsEntry("ai-agent.tools.registry[tm.orders.search].access-mode", "READ")
                .containsEntry("ai-agent.tools.registry[tm.orders.search].required-scopes[0]", "tools:orders.read");
    }

    @Test
    void shouldKeepLocalRuntimeSettingsOutsideSharedApplicationYaml() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties)
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
                .noneMatch(value -> value.startsWith("jdbc:postgresql://"));
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
