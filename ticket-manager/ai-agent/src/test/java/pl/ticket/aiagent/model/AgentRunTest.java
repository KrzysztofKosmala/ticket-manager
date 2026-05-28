package pl.ticket.aiagent.model;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallback;
import pl.ticket.aiagent.security.CallerContext;
import pl.ticket.aiagent.tools.ToolCallbackResolution;
import pl.ticket.aiagent.tools.ToolCandidate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AgentRunTest {

    @Test
    void shouldCaptureSingleRequestFlowWithoutMutatingPreviousRun() {
        CallerContext callerContext = new CallerContext(
                "user-123",
                Set.of("tools:orders.read"),
                Set.of("CUSTOMER")
        );
        AgentRun startedRun = AgentRun.started("Pokaz moje zamowienia", callerContext);
        ToolCandidate orderSearch = new ToolCandidate("tm.orders.search");
        ToolCallback orderSearchCallback = mock(ToolCallback.class);
        ToolCallbackResolution resolution = ToolCallbackResolution.of(List.of(orderSearchCallback));

        AgentRun completedRun = startedRun
                .withSelectedTools(List.of(orderSearch))
                .withResolvedCallbacks(resolution)
                .completed("Masz 2 zamowienia.");

        assertThat(startedRun.selectedTools()).isEmpty();
        assertThat(startedRun.toolCallbackResolution().callbacks()).isEmpty();
        assertThat(startedRun.answer()).isNull();

        assertThat(completedRun.userMessage()).isEqualTo("Pokaz moje zamowienia");
        assertThat(completedRun.callerContext()).isEqualTo(callerContext);
        assertThat(completedRun.selectedTools()).containsExactly(orderSearch);
        assertThat(completedRun.toolCallbackResolution()).isEqualTo(resolution);
        assertThat(completedRun.answer()).isEqualTo("Masz 2 zamowienia.");
    }
}
