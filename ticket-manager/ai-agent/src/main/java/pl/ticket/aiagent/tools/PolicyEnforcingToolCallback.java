package pl.ticket.aiagent.tools;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import pl.ticket.aiagent.security.CallerContext;

final class PolicyEnforcingToolCallback implements ToolCallback {

    private final ToolCallback delegate;
    private final ToolCandidate candidate;
    private final CallerContext callerContext;
    private final ToolPolicy toolPolicy;

    PolicyEnforcingToolCallback(
            ToolCallback delegate,
            ToolCandidate candidate,
            CallerContext callerContext,
            ToolPolicy toolPolicy
    ) {
        this.delegate = delegate;
        this.candidate = candidate;
        this.callerContext = callerContext;
        this.toolPolicy = toolPolicy;
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return delegate.getToolDefinition();
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return delegate.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        assertAllowed();
        return delegate.call(toolInput);
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        assertAllowed();
        return delegate.call(toolInput, toolContext);
    }

    private void assertAllowed() {
        ToolPolicyDecision decision = toolPolicy.evaluate(candidate, callerContext);
        if (!decision.allowed()) {
            throw new ToolExecutionDeniedException(candidate.name(), decision.denialReason().orElse(null));
        }
    }
}
