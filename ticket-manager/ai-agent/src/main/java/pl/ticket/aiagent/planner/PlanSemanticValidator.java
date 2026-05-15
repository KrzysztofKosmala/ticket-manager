package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import pl.ticket.aiagent.tool.ToolContract;
import pl.ticket.aiagent.tool.ToolExecutionException;
import pl.ticket.aiagent.tool.ToolRegistry;

import java.util.HashSet;
import java.util.Set;

@Component
public class PlanSemanticValidator {

    private final ToolRegistry toolRegistry;
    private final ObjectMapper objectMapper;

    public PlanSemanticValidator(ToolRegistry toolRegistry, ObjectMapper objectMapper) {
        this.toolRegistry = toolRegistry;
        this.objectMapper = objectMapper;
    }

    public void validate(Plan plan) {
        if (plan == null) {
            throw new InvalidPlanException("plan is null");
        }
        if (plan.schemaVersion() == null || !Plan.CURRENT_SCHEMA_VERSION.equals(plan.schemaVersion())) {
            throw new InvalidPlanException("schemaVersion must be %s".formatted(Plan.CURRENT_SCHEMA_VERSION));
        }
        if (plan.intent() == null) {
            throw new InvalidPlanException("intent is required");
        }
        if (plan.steps() == null || plan.steps().isEmpty()) {
            throw new InvalidPlanException("steps must not be empty");
        }
        validateUniqueStepIds(plan);
        for (int i = 0; i < plan.steps().size(); i++) {
            validateStep(plan.steps().get(i), i);
        }
    }

    private void validateUniqueStepIds(Plan plan) {
        Set<String> stepIds = new HashSet<>();
        for (int i = 0; i < plan.steps().size(); i++) {
            Plan.Step step = plan.steps().get(i);
            if (step == null || step.id() == null || step.id().isBlank()) {
                continue;
            }
            if (!stepIds.add(step.id())) {
                throw new InvalidPlanException("step[%d].id is duplicated: %s".formatted(i, step.id()));
            }
        }
    }

    private void validateStep(Plan.Step step, int index) {
        if (step == null) {
            throw new InvalidPlanException("step[%d] is null".formatted(index));
        }
        if (step.id() == null || step.id().isBlank()) {
            throw new InvalidPlanException("step[%d].id is required".formatted(index));
        }
        if (step.type() == null) {
            throw new InvalidPlanException("step[%d].type is required".formatted(index));
        }
        if (step.requiresConfirmation() == null) {
            throw new InvalidPlanException("step[%d].requiresConfirmation is required".formatted(index));
        }
        switch (step.type()) {
            case TOOL -> validateToolStep(step, index);
            case ANSWER, ASK_CLARIFY -> validateUserMessageStep(step, index);
            case KNOWLEDGE_SEARCH -> throw new InvalidPlanException(
                    "step[%d].type KNOWLEDGE_SEARCH is not executable yet".formatted(index)
            );
        }
    }

    private void validateToolStep(Plan.Step step, int index) {
        if (step.name() == null || step.name().isBlank()) {
            throw new InvalidPlanException("step[%d].name is required for TOOL".formatted(index));
        }
        ToolContract contract;
        try {
            contract = toolRegistry.getRequired(step.name());
        } catch (ToolExecutionException ex) {
            throw new InvalidPlanException(
                    "step[%d].name is not a supported tool: %s".formatted(index, step.name()),
                    ex
            );
        }
        try {
            if (step.args() != null) {
                objectMapper.convertValue(step.args(), contract.inputType());
            }
        } catch (IllegalArgumentException ex) {
            throw new InvalidPlanException(
                    "step[%d].args cannot be mapped to %s".formatted(index, contract.inputType().getSimpleName()),
                    ex
            );
        }
    }

    private void validateUserMessageStep(Plan.Step step, int index) {
        if (step.args() == null || !(step.args().get("text") instanceof String text) || text.isBlank()) {
            throw new InvalidPlanException("step[%d].args.text is required for %s".formatted(index, step.type()));
        }
    }

    public static class InvalidPlanException extends RuntimeException {

        public InvalidPlanException(String message) {
            super(message);
        }

        public InvalidPlanException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
