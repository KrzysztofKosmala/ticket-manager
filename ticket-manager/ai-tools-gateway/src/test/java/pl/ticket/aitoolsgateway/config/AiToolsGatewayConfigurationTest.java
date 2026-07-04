package pl.ticket.aitoolsgateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class AiToolsGatewayConfigurationTest {

    @Test
    void shouldUseSpringAiAnnotationScannerForToolDiscovery() {
        Properties properties = loadYaml("application.yml");

        assertThat(properties)
                .containsEntry("spring.ai.mcp.server.annotation-scanner.enabled", true);
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
