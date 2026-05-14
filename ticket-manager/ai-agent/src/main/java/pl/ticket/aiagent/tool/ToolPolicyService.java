package pl.ticket.aiagent.tool;

import org.springframework.stereotype.Service;

@Service
public class ToolPolicyService {

    private final AiAgentToolProperties properties;

    public ToolPolicyService(AiAgentToolProperties properties) {
        this.properties = properties;
    }

    public void assertAllowed(ToolContract contract, CallerContext callerContext) {
        if (!properties.getAllowList().contains(contract.name())) {
            throw new ToolExecutionException("Tool is not allow-listed: " + contract.name());
        }
        if (properties.isEnforceScopes()
                && contract.requiredScope() != null
                && !callerContext.scopes().contains(contract.requiredScope())) {
            throw new ToolExecutionException("Missing required scope for tool: " + contract.name());
        }
    }
}
