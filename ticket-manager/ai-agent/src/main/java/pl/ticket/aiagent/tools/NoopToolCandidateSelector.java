package pl.ticket.aiagent.tools;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.security.CallerContext;

import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-agent.tools.selection-mode", havingValue = "none", matchIfMissing = true)
public class NoopToolCandidateSelector implements ToolCandidateSelector {

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        return List.of();
    }
}
