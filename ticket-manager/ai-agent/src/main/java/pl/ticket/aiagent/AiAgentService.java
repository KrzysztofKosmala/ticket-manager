package pl.ticket.aiagent;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import pl.ticket.aiagent.planner.Plan;
import pl.ticket.aiagent.planner.PlannerService;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.feign.order.OrderClient;

import java.util.List;

@Service
public class AiAgentService
{
    private static final String ORDER_SEARCH_TOOL = "order-service.searchOrders";
    private final PlannerService plannerService;
    private final OrderClient orderClient;
    private final ObjectMapper objectMapper;

    public AiAgentService(PlannerService plannerService, OrderClient orderClient, ObjectMapper objectMapper)
    {
        this.plannerService = plannerService;
        this.orderClient = orderClient;
        this.objectMapper = objectMapper;
    }

    public String testPrompt(String prompt)
    {
        return "";
    }

    public Plan plan(String userMessage) {
        return plannerService.createPlan(userMessage);
    }


    public PlanExecutionResult executePlan(String userMessage) {
        Plan plan = plannerService.createPlan(userMessage);
        OrderSearchResponse orderSearchResponse = executeTool(plan.steps);
        return new PlanExecutionResult(plan, orderSearchResponse);
    }

    private OrderSearchResponse executeTool(List<Plan.Step> steps) {
        if (steps == null || steps.isEmpty()) {
            return null;
        }
        Plan.Step toolStep = steps.stream()
                .filter(step -> step.type == Plan.StepType.TOOL)
                .findFirst()
                .orElse(null);
        if (toolStep == null || toolStep.name == null) {
            return null;
        }
        if (!ORDER_SEARCH_TOOL.equals(toolStep.name)) {
            throw new IllegalArgumentException("Unsupported tool: " + toolStep.name);
        }
        OrderSearchRequest request = toolStep.args != null
                ? objectMapper.convertValue(toolStep.args, OrderSearchRequest.class)
                : null;
        return orderClient.searchOrders(request);
    }
}
