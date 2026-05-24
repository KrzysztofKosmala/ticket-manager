package pl.ticket.aiagent.toolselection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ToolCandidateSelectorTest {

    @Test
    void shouldSelectOrderSearchToolForOrderQuestionWhenAllowed() {
        ToolSelectionProperties properties = new ToolSelectionProperties();
        properties.setAllowList(List.of("tm.orders.search"));
        ToolCandidateSelector selector = new ToolCandidateSelector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje ostatnie zamowienia");

        assertThat(candidates)
                .extracting(ToolCandidate::name)
                .containsExactly("tm.orders.search");
    }

    @Test
    void shouldReturnNoCandidatesForGeneralConversation() {
        ToolSelectionProperties properties = new ToolSelectionProperties();
        properties.setAllowList(List.of("tm.orders.search"));
        ToolCandidateSelector selector = new ToolCandidateSelector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Jak dziala ten asystent?");

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldNotSelectOrderSearchToolWhenItIsNotAllowed() {
        ToolSelectionProperties properties = new ToolSelectionProperties();
        properties.setAllowList(List.of());
        ToolCandidateSelector selector = new ToolCandidateSelector(properties);

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia");

        assertThat(candidates).isEmpty();
    }

    @Test
    void shouldReturnNoCandidatesForBlankMessage() {
        ToolSelectionProperties properties = new ToolSelectionProperties();
        properties.setAllowList(List.of("tm.orders.search"));
        ToolCandidateSelector selector = new ToolCandidateSelector(properties);

        List<ToolCandidate> candidates = selector.selectFor("   ");

        assertThat(candidates).isEmpty();
    }
}
