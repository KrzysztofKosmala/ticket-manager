package pl.ticket.aiagent.tools;

import pl.ticket.aiagent.security.CallerContext;

import java.util.List;

public interface ToolCandidateSelector {

    default List<ToolCandidate> selectFor(String userMessage) {
        return selectFor(userMessage, CallerContext.anonymous());
    }

    List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext);
}
