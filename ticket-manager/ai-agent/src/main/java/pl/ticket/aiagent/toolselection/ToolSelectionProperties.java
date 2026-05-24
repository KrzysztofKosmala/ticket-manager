package pl.ticket.aiagent.toolselection;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "ai-agent.tools")
public class ToolSelectionProperties {

    private List<String> allowList = List.of();

    public List<String> getAllowList() {
        return allowList;
    }

    public void setAllowList(List<String> allowList) {
        this.allowList = allowList == null ? List.of() : List.copyOf(allowList);
    }
}
