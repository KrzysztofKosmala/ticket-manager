package pl.ticket.aiagent.tool;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "ai-agent.tools")
public class AiAgentToolProperties {

    private Set<String> allowList = new LinkedHashSet<>(Set.of(ToolNames.ORDERS_SEARCH));
    private boolean enforceScopes = false;

    public Set<String> getAllowList() {
        return allowList;
    }

    public void setAllowList(Set<String> allowList) {
        this.allowList = allowList;
    }

    public boolean isEnforceScopes() {
        return enforceScopes;
    }

    public void setEnforceScopes(boolean enforceScopes) {
        this.enforceScopes = enforceScopes;
    }

}
