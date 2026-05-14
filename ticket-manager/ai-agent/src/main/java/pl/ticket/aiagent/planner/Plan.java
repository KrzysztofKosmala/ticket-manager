package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Plan(
        String schemaVersion,
        Intent intent,
        List<Step> steps,
        List<String> constraints,
        String fallback
) {

    public static final String CURRENT_SCHEMA_VERSION = "1.0";

    public enum Intent {
        ORDER_INQUIRY,
        PROMOTION_INQUIRY,
        KNOWLEDGE_INQUIRY,
        UNKNOWN
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Step(
            String id,
            StepType type,
            String name,
            Map<String, Object> args,
            List<String> constraints,
            Boolean requiresConfirmation
    ) {
    }

    public enum StepType {
        TOOL,
        KNOWLEDGE_SEARCH,
        ANSWER,
        ASK_CLARIFY
    }
}
