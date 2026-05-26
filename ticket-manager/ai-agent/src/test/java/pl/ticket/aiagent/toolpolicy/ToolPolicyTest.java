package pl.ticket.aiagent.toolpolicy;

import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ToolPolicyTest {

    @Test
    void shouldAllowReadToolWhenScopeEnforcementIsDisabled() {
        ToolPolicyProperties properties = propertiesWithOrderTool();
        properties.setEnforceScopes(false);
        ToolPolicy policy = new ToolPolicy(properties);

        ToolPolicyDecision decision = policy.evaluate(orderSearch(), CallerContext.anonymous());

        assertThat(decision.allowed()).isTrue();
        assertThat(decision.denialReason()).isEmpty();
    }

    @Test
    void shouldDenyToolWhenRequiredScopeIsMissingAndScopeEnforcementIsEnabled() {
        ToolPolicyProperties properties = propertiesWithOrderTool();
        properties.setEnforceScopes(true);
        ToolPolicy policy = new ToolPolicy(properties);

        ToolPolicyDecision decision = policy.evaluate(orderSearch(), CallerContext.anonymous());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.denialReason()).contains(ToolPolicyDenialReason.MISSING_SCOPE);
    }

    @Test
    void shouldAllowToolWhenRequiredScopeIsPresent() {
        ToolPolicyProperties properties = propertiesWithOrderTool();
        properties.setEnforceScopes(true);
        ToolPolicy policy = new ToolPolicy(properties);
        CallerContext callerContext = new CallerContext(
                "user-123",
                Set.of("tools:orders.read"),
                Set.of("CUSTOMER")
        );

        ToolPolicyDecision decision = policy.evaluate(orderSearch(), callerContext);

        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void shouldDenyToolThatIsNotAllowListed() {
        ToolPolicyProperties properties = propertiesWithOrderTool();
        properties.setRegistry(Map.of());
        ToolPolicy policy = new ToolPolicy(properties);

        ToolPolicyDecision decision = policy.evaluate(orderSearch(), CallerContext.anonymous());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.denialReason()).contains(ToolPolicyDenialReason.NOT_ALLOW_LISTED);
    }

    @Test
    void shouldDenyWriteSideToolInCallbackOnlyIteration() {
        ToolPolicyProperties properties = propertiesWithOrderTool();
        ToolPolicyProperties.ToolMetadata metadata = metadata();
        metadata.setAccessMode(ToolAccessMode.WRITE);
        properties.setRegistry(Map.of("tm.orders.search", metadata));
        ToolPolicy policy = new ToolPolicy(properties);

        ToolPolicyDecision decision = policy.evaluate(orderSearch(), CallerContext.anonymous());

        assertThat(decision.allowed()).isFalse();
        assertThat(decision.denialReason()).contains(ToolPolicyDenialReason.WRITE_SIDE_UNSUPPORTED);
    }

    private ToolPolicyProperties propertiesWithOrderTool() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        ToolPolicyProperties.ToolMetadata metadata = metadata();
        metadata.setRequiredScopes(List.of("tools:orders.read"));
        properties.setRegistry(Map.of("tm.orders.search", metadata));
        return properties;
    }

    private ToolPolicyProperties.ToolMetadata metadata() {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setSource(ToolSourceType.INTERNAL_MCP);
        metadata.setAccessMode(ToolAccessMode.READ);
        metadata.setRiskLevel(ToolRiskLevel.LOW);
        return metadata;
    }

    private ToolCandidate orderSearch() {
        return new ToolCandidate("tm.orders.search");
    }
}
