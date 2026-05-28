package pl.ticket.aiagent.run;

import pl.ticket.aiagent.caller.CallerContext;
import pl.ticket.aiagent.toolcallback.ToolCallbackResolution;
import pl.ticket.aiagent.toolselection.ToolCandidate;

import java.util.List;

public record AgentRun(
        String userMessage,
        CallerContext callerContext,
        List<ToolCandidate> selectedTools,
        ToolCallbackResolution toolCallbackResolution,
        String answer
) {

    public AgentRun {
        callerContext = callerContext == null ? CallerContext.anonymous() : callerContext;
        selectedTools = selectedTools == null ? List.of() : List.copyOf(selectedTools);
        toolCallbackResolution = toolCallbackResolution == null ? ToolCallbackResolution.empty() : toolCallbackResolution;
    }

    public static AgentRun started(String userMessage, CallerContext callerContext) {
        return new AgentRun(
                userMessage,
                callerContext,
                List.of(),
                ToolCallbackResolution.empty(),
                null
        );
    }

    public AgentRun withSelectedTools(List<ToolCandidate> selectedTools) {
        return new AgentRun(
                userMessage,
                callerContext,
                selectedTools,
                toolCallbackResolution,
                answer
        );
    }

    public AgentRun withResolvedCallbacks(ToolCallbackResolution toolCallbackResolution) {
        return new AgentRun(
                userMessage,
                callerContext,
                selectedTools,
                toolCallbackResolution,
                answer
        );
    }

    public AgentRun completed(String answer) {
        return new AgentRun(
                userMessage,
                callerContext,
                selectedTools,
                toolCallbackResolution,
                answer
        );
    }
}
