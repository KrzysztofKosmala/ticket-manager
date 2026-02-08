
package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Plan {
    public Intent intent;
    public List<Step> steps;
    public List<String> constraints;
    public String fallback;

    public enum Intent {
        GET_USER_ORDERS,
        GET_PROMO_TERMS,
        QNA_KNOWLEDGE,
        UNKNOWN
    }

    public static class Step {
        public StepType type;
        public String name;
        public Map<String, Object> args;
        public List<String> constraints;

        public Step() {}

        public Step(StepType type, String name, Map<String,Object> args) {
            this.type = type;
            this.name = name;
            this.args = args;
        }
    }

    public enum StepType {
        TOOL,
        RAG,
        ANSWER,
        ASK_CLARIFY,
        ASYNC_JOB
    }
}
