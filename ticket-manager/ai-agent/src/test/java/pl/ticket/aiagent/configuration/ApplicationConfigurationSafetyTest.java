package pl.ticket.aiagent.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

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

    private Properties loadYaml(String resourceName) {
        ClassPathResource resource = new ClassPathResource(resourceName);
        assertThat(resource.exists())
                .as("%s should exist on classpath", resourceName)
                .isTrue();

        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(resource);
        return Objects.requireNonNull(yaml.getObject());
    }
}
