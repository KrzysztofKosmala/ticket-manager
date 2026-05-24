package pl.ticket.aiagent.toolselection;

import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.caller.CallerContext;
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
        StaticToolCandidateSelector selector = new StaticToolCandidateSelector(new ToolPolicy(properties));

        List<ToolCandidate> candidates = selector.selectFor("Jakikolwiek niepusty prompt", CallerContext.anonymous());

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search");
    }

    @Test
    void shouldReturnNoCandidatesWhenPolicyDeniesTool() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of());
        StaticToolCandidateSelector selector = new StaticToolCandidateSelector(new ToolPolicy(properties));

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesForBlankMessage() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        StaticToolCandidateSelector selector = new StaticToolCandidateSelector(new ToolPolicy(properties));

        List<ToolCandidate> candidates = selector.selectFor("   ", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesWhenRequiredScopeIsMissing() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = new StaticToolCandidateSelector(new ToolPolicy(properties));

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldSelectOrderSearchToolWhenRequiredScopeIsPresent() {
        ToolPolicyProperties properties = properties();
        properties.setAllowList(List.of("tm.orders.search"));
        properties.setEnforceScopes(true);
        StaticToolCandidateSelector selector = new StaticToolCandidateSelector(new ToolPolicy(properties));
        CallerContext callerContext = new CallerContext("user-123", Set.of("tools:orders.read"), Set.of("CUSTOMER"));

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", callerContext);

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search");
    }

    private ToolPolicyProperties properties() {
        ToolPolicyProperties properties = new ToolPolicyProperties();
        ToolPolicyProperties.ToolMetadata metadata = new ToolPolicyProperties.ToolMetadata();
        metadata.setRequiredScopes(List.of("tools:orders.read"));
        properties.setMetadata(Map.of("tm.orders.search", metadata));
        return properties;
    }
}
