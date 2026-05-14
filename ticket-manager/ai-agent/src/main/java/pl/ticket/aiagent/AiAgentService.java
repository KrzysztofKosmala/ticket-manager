package pl.ticket.aiagent;

import org.springframework.stereotype.Service;
import pl.ticket.aiagent.planner.Plan;
import pl.ticket.aiagent.planner.PlannerService;
import pl.ticket.aiagent.tool.CallerContext;
import pl.ticket.aiagent.tool.ToolExecutionService;
import pl.ticket.dto.OrderSearchResponse;

import java.util.List;

@Service
public class AiAgentService
{
    private final PlannerService plannerService;
    private final ToolExecutionService toolExecutionService;

    public AiAgentService(PlannerService plannerService, ToolExecutionService toolExecutionService)
    {
        this.plannerService = plannerService;
        this.toolExecutionService = toolExecutionService;
    }

    public String testPrompt(String prompt)
    {
        return "";
    }

    public Plan plan(String userMessage) {
        return plannerService.createPlan(userMessage);
    }


    public PlanExecutionResult executePlan(String userMessage, CallerContext callerContext) {
        Plan plan = plannerService.createPlan(userMessage);
        OrderSearchResponse orderSearchResponse = executeTool(plan.steps(), callerContext);
        return new PlanExecutionResult(plan, orderSearchResponse);
    }

    private OrderSearchResponse executeTool(List<Plan.Step> steps, CallerContext callerContext) {
        if (steps == null || steps.isEmpty()) {
            return null;
        }
        Plan.Step toolStep = steps.stream()
                .filter(step -> step.type() == Plan.StepType.TOOL)
                .findFirst()
                .orElse(null);
        if (toolStep == null || toolStep.name() == null) {
            return null;
        }
        Object result = toolExecutionService.execute(toolStep, callerContext);
        return result instanceof OrderSearchResponse response ? response : null;
    }
}
