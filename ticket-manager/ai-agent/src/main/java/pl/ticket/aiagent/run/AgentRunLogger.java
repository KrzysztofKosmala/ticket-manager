package pl.ticket.aiagent.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;

@Component
public class AgentRunLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AgentRunLogger.class);

    public void logCompleted(AgentRun run) {
        LOGGER.info(
                "AI agent run completed: subject={}, selectedTools={}, resolvedCallbacks={}, missingTools={}",
                run.callerContext().subject(),
                toolNames(run.selectedTools()),
                run.toolCallbackResolution().callbacks().size(),
                toolNames(run.toolCallbackResolution().missingCandidates())
        );
    }

    private List<String> toolNames(List<ToolCandidate> candidates) {
        return candidates.stream()
                .map(ToolCandidate::name)
                .toList();
    }
}
