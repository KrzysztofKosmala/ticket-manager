package pl.ticket.aiagent.tools;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolPolicyPropertiesTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void shouldRejectRegistryEntryMissingRequiredPolicyFields() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(Map.of("tm.orders.search", new ToolPolicyProperties.ToolMetadata()));

        Set<ConstraintViolation<ToolPolicyProperties>> violations = validator.validate(properties);

        assertThat(violations)
                .extracting(violation -> violation.getPropertyPath().toString())
                .contains(
                        "registry[tm.orders.search].source",
                        "registry[tm.orders.search].accessMode"
                )
                .doesNotContain("registry[tm.orders.search].riskLevel");
    }

    @Test
    void shouldAcceptRegistryEntryWithRequiredPolicyFields() {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setSource(ToolSourceType.INTERNAL_MCP);
        metadata.setAccessMode(ToolAccessMode.READ);

        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(Map.of("tm.orders.search", metadata));

        Set<ConstraintViolation<ToolPolicyProperties>> violations = validator.validate(properties);

        assertThat(violations).isEmpty();
    }
}
