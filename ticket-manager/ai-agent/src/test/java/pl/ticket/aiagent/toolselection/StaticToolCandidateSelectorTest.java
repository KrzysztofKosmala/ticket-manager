package pl.ticket.aiagent.toolselection;

import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolcatalog.ToolCatalog;
import pl.ticket.aiagent.toolpolicy.ToolPolicy;
import pl.ticket.aiagent.toolpolicy.ToolPolicyProperties;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StaticToolCandidateSelectorTest {

    @Test
    void shouldSelectConfiguredOrderSearchToolWhenPolicyAllowsIt() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Jakikolwiek niepusty prompt", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search");
    }

    @Test
    void shouldSelectAllConfiguredToolsWhenPolicyAllowsThem() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search", "tm.knowledge.search"));
        ToolPolicyProperties.ToolMetadata knowledgeMetadata = new ToolPolicyProperties.ToolMetadata();
        properties.setMetadata(Map.of(
                "tm.orders.search", metadataWithRequiredScope("tools:orders.read"),
                "tm.knowledge.search", knowledgeMetadata
        ));
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje dane i wiedze", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search", "tm.knowledge.search");
    }

    @Test
    void shouldReturnNoCandidatesWhenPolicyDeniesTool() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of());
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesForBlankMessage() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("   ", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesWhenRequiredScopeIsMissing() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = selector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldSelectOrderSearchToolWhenRequiredScopeIsPresent() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = selector(properties);
        CallerContext callerContext = new CallerContext("user-123", Set.of("tools:orders.read"), Set.of("CUSTOMER"));

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", callerContext);

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search");
    }

    private ToolPolicyProperties properties() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        ToolPolicyProperties.ToolMetadata metadata = metadataWithRequiredScope("tools:orders.read");
        properties.setMetadata(Map.of("tm.orders.search", metadata));
        return properties;
    }

    private StaticToolCandidateSelector selector(ToolPolicyProperties properties) {
        return new StaticToolCandidateSelector(new ToolPolicy(properties), new ToolCatalog(properties, List.of()));
    }

    private ToolPolicyProperties.ToolMetadata metadataWithRequiredScope(String requiredScope) {
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setRequiredScopes(List.of(requiredScope));
        return metadata;
    }
}
