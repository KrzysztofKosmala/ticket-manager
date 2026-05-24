package pl.ticket.aiagent.toolselection;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.caller.CallerContext;

import java.util.List;

@Component
@Profile("!local & !test & !smoke")
public class NoopToolCandidateSelector implements ToolCandidateSelector {

    @Override
    public List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext) {
        return List.of();
    }
}
