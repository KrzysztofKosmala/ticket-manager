package pl.ticket.aiagent.toolselection;

import pl.ticket.aiagent.caller.CallerContext;

import java.util.List;

public interface ToolCandidateSelector {

    default List<ToolCandidate> selectFor(String userMessage) {
        return selectFor(userMessage, CallerContext.anonymous());
    }

    List<ToolCandidate> selectFor(String userMessage, CallerContext callerContext);
}
