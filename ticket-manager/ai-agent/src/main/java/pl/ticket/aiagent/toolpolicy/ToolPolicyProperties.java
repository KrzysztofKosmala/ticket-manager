package pl.ticket.aiagent.toolpolicy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "ai-agent.tools")
public class ToolPolicyProperties {

    private List<String> allowList = List.of();
    private boolean enforceScopes;
    private Map<String, ToolMetadata> metadata = Map.of();

    public List<String> getAllowList() {
        return allowList;
    }

    public void setAllowList(List<String> allowList) {
        this.allowList = allowList == null ? List.of() : List.copyOf(allowList);
    }

    public boolean isEnforceScopes() {
        return enforceScopes;
    }

    public void setEnforceScopes(boolean enforceScopes) {
        this.enforceScopes = enforceScopes;
    }

    public Map<String, ToolMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, ToolMetadata> metadata) {
        this.metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public ToolMetadata metadataFor(String toolName) {
        return metadata.getOrDefault(toolName, new ToolMetadata());
    }

    public static class ToolMetadata {

        private boolean enabled = true;
        private ToolAccessMode accessMode = ToolAccessMode.READ;
        private List<String> requiredScopes = List.of();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public ToolAccessMode getAccessMode() {
            return accessMode;
        }

        public void setAccessMode(ToolAccessMode accessMode) {
            this.accessMode = accessMode == null ? ToolAccessMode.READ : accessMode;
        }

        public List<String> getRequiredScopes() {
            return requiredScopes;
        }

        public void setRequiredScopes(List<String> requiredScopes) {
            this.requiredScopes = requiredScopes == null ? List.of() : List.copyOf(requiredScopes);
        }
    }
}
