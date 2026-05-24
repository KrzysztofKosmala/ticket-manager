package pl.ticket.aiagent.toolselection;

import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.caller.CallerContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NoopToolCandidateSelectorTest {

    @Test
    void shouldReturnNoCandidatesInSafeDefaultRuntime() {
        NoopToolCandidateSelector selector = new NoopToolCandidateSelector();

        List<ToolCandidate> candidates = selector.selectFor("Pokaz moje zamowienia", CallerContext.anonymous());

        assertThat(candidates).isEmpty();
    }
}
