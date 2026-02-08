package pl.ticket.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import pl.ticket.aiagent.planner.Plan;
import pl.ticket.aiagent.planner.PlannerService;

@Service
public class AiAgentService
{
    private final PlannerService plannerService;

    public AiAgentService(PlannerService plannerService)
    {
        this.plannerService = plannerService;
    }

    public String testPrompt(String prompt)
    {
        return "";
    }

    public Plan plan(String userMessage) {
        return plannerService.createPlan(userMessage);
    }
}
