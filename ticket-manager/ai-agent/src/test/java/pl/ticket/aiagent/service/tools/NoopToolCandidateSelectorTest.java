package pl.ticket.aiagent.service.tools;

import pl.ticket.aiagent.model.tools.ToolCandidate;
import org.junit.jupiter.api.Test;
import pl.ticket.aiagent.security.CallerContext;

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
