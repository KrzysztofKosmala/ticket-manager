package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.configuration.properties.ToolPolicyProperties;
import pl.ticket.aiagent.model.tools.ToolAccessMode;
import pl.ticket.aiagent.model.tools.ToolCandidate;
import pl.ticket.aiagent.model.tools.ToolSourceType;
import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StaticToolCandidateSelectorTest {

    @Test
    void shouldSelectConfiguredOrderSearchToolWhenPolicyAllowsIt() {
        ToolPolicyProperties properties = properties();
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Jakikolwiek niepusty prompt", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm_my_orders_search");
    }

    @Test
    void shouldSelectAllConfiguredToolsWhenPolicyAllowsThem() {
        ToolPolicyProperties properties = properties();
        Map<String, ToolPolicyProperties.ToolMetadata> registry = new LinkedHashMap<>();
        registry.put("tm_my_orders_search", metadataWithRequiredScope("tools:orders.read"));
        registry.put("tm_knowledge_search", metadata());
        properties.setRegistry(registry);
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje dane i wiedze", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm_my_orders_search", "tm_knowledge_search");
    }

    @Test
    void shouldNotSelectDisabledConfiguredTools() {
        ToolPolicyProperties.ToolMetadata disabledMetadata = metadata();
        disabledMetadata.setEnabled(false);
        Map<String, ToolPolicyProperties.ToolMetadata> registry = new LinkedHashMap<>();
        registry.put("tm_my_orders_search", metadataWithRequiredScope("tools:orders.read"));
        registry.put("tm_knowledge_search", disabledMetadata);
        ToolPolicyProperties properties = new ToolPolicyProperties();
        properties.setRegistry(registry);
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje dane i wiedze", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm_my_orders_search");
    }

    @Test
    void shouldReturnNoCandidatesWhenPolicyDeniesTool() {
        ToolPolicyProperties properties = properties();
        properties.setRegistry(Map.of());
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesForBlankMessage() {
        ToolPolicyProperties properties = properties();
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("   ", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesWhenRequiredScopeIsMissing() {
        ToolPolicyProperties properties = properties();
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldSelectOrderSearchToolWhenRequiredScopeIsPresent() {
        ToolPolicyProperties properties = properties();
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = selector(properties);
        CallerContext callerContext = new CallerContext("user-123", Set.of("tools:orders.read"), Set.of("CUSTOMER"));

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", callerContext);

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm_my_orders_search");
    }

    private ToolPolicyProperties properties() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        ToolPolicyProperties.ToolMetadata metadata = metadataWithRequiredScope("tools:orders.read");
        properties.setRegistry(Map.of("tm_my_orders_search", metadata));
        return properties;
    }

    private StaticToolCandidateSelector selector(ToolPolicyProperties properties) {
        return new StaticToolCandidateSelector(new ToolPolicy(properties));
    }

    private ToolPolicyProperties.ToolMetadata metadataWithRequiredScope(String requiredScope) {
        ToolPolicyProperties.ToolMetadata metadata = metadata();
        metadata.setRequiredScopes(List.of(requiredScope));
        return metadata;
    }

    private ToolPolicyProperties.ToolMetadata metadata() {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setSource(pl.ticket.aiagent.model.tools.ToolSourceType.INTERNAL_MCP);
        metadata.setAccessMode(pl.ticket.aiagent.model.tools.ToolAccessMode.READ);
        return metadata;
    }
}
