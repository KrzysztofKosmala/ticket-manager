package pl.ticket.aiagent;

import com.fasterxml.jackson.annotation.JsonInclude;
import pl.ticket.aiagent.planner.Plan;
import pl.ticket.dto.OrderSearchResponse;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PlanExecutionResult(
        Plan plan,
        OrderSearchResponse orderSearchResponse
) {
}
