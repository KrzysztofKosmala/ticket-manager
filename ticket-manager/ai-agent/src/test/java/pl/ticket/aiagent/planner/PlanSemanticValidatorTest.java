package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import pl.ticket.aiagent.tool.ToolRegistry;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanSemanticValidatorTest {

    private final PlanSemanticValidator validator = new PlanSemanticValidator(
            new ToolRegistry(),
            new ObjectMapper()
    );

    @Test
    void shouldAcceptPlanWithUniqueStepIds() {
        Plan plan = planWithSteps(
                answerStep("step-1", "Nie znalazlem jeszcze wystarczajacych informacji."),
                answerStep("step-2", "Sprobuj doprecyzowac pytanie.")
        );

        assertThatCode(() -> validator.validate(plan))
                .doesNotThrowAnyException();
    }

    @Test
    void shouldRejectPlanWithDuplicatedStepIds() {
        Plan plan = planWithSteps(
                answerStep("step-1", "Pierwszy krok."),
                answerStep("step-1", "Drugi krok z takim samym id.")
        );

        assertThatThrownBy(() -> validator.validate(plan))
                .isInstanceOf(PlanSemanticValidator.InvalidPlanException.class)
                .hasMessage("step[1].id is duplicated: step-1");
    }

    @ParameterizedTest
    @EnumSource(value = Plan.StepType.class, names = {"ANSWER", "ASK_CLARIFY"})
    void shouldRejectUserMessageStepWithoutText(Plan.StepType stepType) {
        Plan plan = planWithSteps(
                new Plan.Step(
                        "step-1",
                        stepType,
                        null,
                        Map.of(),
                        null,
                        false
                )
        );

        assertThatThrownBy(() -> validator.validate(plan))
                .isInstanceOf(PlanSemanticValidator.InvalidPlanException.class)
                .hasMessage("step[0].args.text is required for %s".formatted(stepType));
    }

    @Test
    void shouldRejectToolStepWithUnknownToolName() {
        Plan plan = planWithSteps(
                toolStep("step-1", "tm.unknown.tool")
        );

        assertThatThrownBy(() -> validator.validate(plan))
                .isInstanceOf(PlanSemanticValidator.InvalidPlanException.class)
                .hasMessage("step[0].name is not a supported tool: tm.unknown.tool");
    }

    @Test
    void shouldRejectKnowledgeSearchStepUntilItIsExecutable() {
        Plan plan = planWithSteps(
                new Plan.Step(
                        "step-1",
                        Plan.StepType.KNOWLEDGE_SEARCH,
                        null,
                        Map.of("query", "regulamin promocji"),
                        null,
                        false
                )
        );

        assertThatThrownBy(() -> validator.validate(plan))
                .isInstanceOf(PlanSemanticValidator.InvalidPlanException.class)
                .hasMessage("step[0].type KNOWLEDGE_SEARCH is not executable yet");
    }

    private Plan planWithSteps(Plan.Step... steps) {
        return new Plan(
                Plan.CURRENT_SCHEMA_VERSION,
                Plan.Intent.UNKNOWN,
                List.of(steps),
                null,
                null
        );
    }

    private Plan.Step answerStep(String id, String text) {
        return new Plan.Step(
                id,
                Plan.StepType.ANSWER,
                null,
                Map.of("text", text),
                null,
                false
        );
    }

    private Plan.Step toolStep(String id, String name) {
        return new Plan.Step(
                id,
                Plan.StepType.TOOL,
                name,
                Map.of(),
                null,
                false
        );
    }
}
