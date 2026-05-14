package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import pl.ticket.aiagent.exception.PlannerFailedException;

@Service
public class PlannerService {

    private final ObjectMapper mapper;
    private final PlanSchemaValidator schemaValidator;
    private final PlanSemanticValidator semanticValidator;
    private final ChatClient chatClient;
    private final PlannerPromptBuilder promptBuilder;

    public PlannerService(
            PlanSchemaValidator schemaValidator,
            PlanSemanticValidator semanticValidator,
            ChatClient.Builder builder,
            PlannerPromptBuilder promptBuilder,
            ObjectMapper mapper
    ) {
        this.schemaValidator = schemaValidator;
        this.semanticValidator = semanticValidator;
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
        this.mapper = mapper;
    }

    public Plan createPlan(String userMessage) {
        String systemPrompt = promptBuilder.system();
        String userPrompt = promptBuilder.user(userMessage);
        String raw = callPlanner(systemPrompt, userPrompt);

        try {
             return parseAndValidate(raw);
        } catch (Exception firstError) {
            return repairPlan(userMessage, systemPrompt, raw, firstError);
        }
    }

    private Plan repairPlan(String userMessage, String systemPrompt, String invalidOutput, Exception firstError) {
        String repairUserPrompt = promptBuilder.repair(userMessage, invalidOutput, firstError.getMessage());
        String repaired = callPlanner(systemPrompt, repairUserPrompt);
        try {
            return parseAndValidate(repaired);
        } catch (Exception repairError) {
            throw new PlannerFailedException(
                    "Planner failed to produce a valid plan after repair attempt",
                    repairError
            );
        }
    }

    private Plan parseAndValidate(String raw) {
        schemaValidator.validate(raw);
        try {
            Plan plan = mapper.readValue(raw, Plan.class);
            semanticValidator.validate(plan);
            return plan;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException("Planner returned JSON valid by schema but cannot be parsed to Plan", ex);
        }
    }

    private String callPlanner(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }
}
