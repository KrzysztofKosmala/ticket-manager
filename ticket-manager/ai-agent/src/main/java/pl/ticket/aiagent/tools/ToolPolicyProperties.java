package pl.ticket.aiagent.tools;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Validated
@ConfigurationProperties(prefix = "ai-agent.tools")
public class ToolPolicyProperties {

    private boolean enforceScopes;
    private Map<String, @Valid ToolMetadata> registry = Map.of();

    public boolean isEnforceScopes() {
        return enforceScopes;
    }

    public void setEnforceScopes(boolean enforceScopes) {
        this.enforceScopes = enforceScopes;
    }

    public Map<String, @Valid ToolMetadata> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<String, ToolMetadata> registry) {
        this.registry = registry == null ? Map.of() : new LinkedHashMap<>(registry);
    }

    public List<String> enabledToolNames() {
        return registry.entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .map(Map.Entry::getKey)
                .toList();
    }

    public Optional<ToolMetadata> metadataFor(String toolName) {
        return Optional.ofNullable(registry.get(toolName));
    }

    public static class ToolMetadata {

        private boolean enabled = true;
        @NotNull
        private ToolSourceType source;
        @NotNull
        private ToolAccessMode accessMode;
        private List<String> requiredScopes = List.of();

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public ToolSourceType getSource() {
            return source;
        }

        public void setSource(ToolSourceType source) {
            this.source = source;
        }

        public ToolAccessMode getAccessMode() {
            return accessMode;
        }

        public void setAccessMode(ToolAccessMode accessMode) {
            this.accessMode = accessMode;
        }

        public List<String> getRequiredScopes() {
            return requiredScopes;
        }

        public void setRequiredScopes(List<String> requiredScopes) {
            this.requiredScopes = requiredScopes == null ? List.of() : List.copyOf(requiredScopes);
        }
    }
}
