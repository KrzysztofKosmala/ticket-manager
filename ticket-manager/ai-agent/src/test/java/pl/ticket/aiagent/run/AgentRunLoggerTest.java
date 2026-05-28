package pl.ticket.aiagent.run;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(OutputCaptureExtension.class)
class AgentRunLoggerTest {

    @Test
    void shouldLogCompletedRunSummaryWithoutPromptOrAnswer(CapturedOutput output) {
        ToolCandidate resolvedTool = new ToolCandidate("tm.orders.search");
        ToolCandidate missingTool = new ToolCandidate("tm.knowledge.search");
        ToolCallback callback = mock(ToolCallback.class);
        AgentRun run = AgentRun.started(
                        "Pokaz moje zamowienia",
                        new CallerContext("user-123", Set.of("tools:orders.read"), Set.of("CUSTOMER"))
                )
                .withSelectedTools(List.of(resolvedTool, missingTool))
                .withResolvedCallbacks(new ToolCallbackResolution(List.of(callback), List.of(missingTool)))
                .completed("Masz 2 zamowienia.");
        AgentRunLogger logger = new AgentRunLogger();

        logger.logCompleted(run);

        assertThat(output)
                .contains("AI agent run completed")
                .contains("subject=user-123")
                .contains("selectedTools=[tm.orders.search, tm.knowledge.search]")
                .contains("resolvedCallbacks=1")
                .contains("missingTools=[tm.knowledge.search]")
                .doesNotContain("Pokaz moje zamowienia")
                .doesNotContain("Masz 2 zamowienia.");
    }
}
