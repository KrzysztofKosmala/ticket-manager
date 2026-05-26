package pl.ticket.aiagent.toolpolicy;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
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
        private List<String> intents = List.of();
        @NotNull
        private ToolRiskLevel riskLevel;
        @NotNull
        private Duration timeout = Duration.ofSeconds(3);
        @NotNull
        private DataSize maxResponseSize = DataSize.ofKilobytes(32);

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

        public List<String> getIntents() {
            return intents;
        }

        public void setIntents(List<String> intents) {
            this.intents = intents == null ? List.of() : List.copyOf(intents);
        }

        public ToolRiskLevel getRiskLevel() {
            return riskLevel;
        }

        public void setRiskLevel(ToolRiskLevel riskLevel) {
            this.riskLevel = riskLevel;
        }

        public Duration getTimeout() {
            return timeout;
        }

        public void setTimeout(Duration timeout) {
            this.timeout = timeout == null ? Duration.ofSeconds(3) : timeout;
        }

        public DataSize getMaxResponseSize() {
            return maxResponseSize;
        }

        public void setMaxResponseSize(DataSize maxResponseSize) {
            this.maxResponseSize = maxResponseSize == null ? DataSize.ofKilobytes(32) : maxResponseSize;
        }
    }
}
