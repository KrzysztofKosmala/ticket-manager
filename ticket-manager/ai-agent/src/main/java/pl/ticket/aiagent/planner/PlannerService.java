// src/main/java/pl/ticket/aiagent/planner/PlannerService.java
package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PlannerService {
    private final ObjectMapper mapper;
    private final PlanSchemaValidator validator;
    private final ChatClient chatClient;
    private final PlannerPromptBuilder promptBuilder;

    public PlannerService(
            PlanSchemaValidator validator,
            ChatClient.Builder builder,
            PlannerPromptBuilder promptBuilder,
            ObjectMapper mapper
    )
    {
        this.validator = validator;
        this.chatClient = builder.build();
        this.promptBuilder = promptBuilder;
        this.mapper = mapper;
    }

    public Plan createPlan(String userMessage) {
        String systemPrompt = promptBuilder.system();      // stałe reguły
        String userPrompt = promptBuilder.user(userMessage); // tylko treść usera

        // 1st attempt
        String raw = callPlanner(systemPrompt, userPrompt);

        // validate + parse (retry once with feedback)
        try {
            validator.validate(raw);
            return mapper.readValue(raw, Plan.class);
        } catch (Exception firstError) {
            String repairUserPrompt = promptBuilder.repair(userMessage, raw, firstError.getMessage());
            String repaired = callPlanner(systemPrompt, repairUserPrompt);

            validator.validate(repaired);
            try {
                return mapper.readValue(repaired, Plan.class);
            } catch (Exception e) {
                throw new RuntimeException("Planner returned JSON valid by schema but cannot be parsed to Plan", e);
            }
        }
    }

    private String callPlanner(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                // jeśli chcesz wymusić deterministykę:
                // .options(ChatOptions.builder().temperature(0.0).build())
                .call()
                .content();
    }
}
