package pl.ticket.aiagent.planner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PlanSchemaValidator {
    private final JsonSchema schema;
    private final ObjectMapper mapper = new ObjectMapper();

    public PlanSchemaValidator() throws Exception {
        try (InputStream is = new ClassPathResource("plan-schema.json").getInputStream()) {
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            JsonNode schemaNode = mapper.readTree(is);
            this.schema = factory.getSchema(schemaNode);
        }
    }

    public void validate(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            Set<ValidationMessage> errors = schema.validate(node);
            if (!errors.isEmpty()) {
                String msg = errors.stream()
                        .map(ValidationMessage::getMessage)
                        .collect(Collectors.joining("; "));
                throw new InvalidPlanException(msg);
            }
        } catch (InvalidPlanException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidPlanException("invalid json: " + e.getMessage());
        }
    }

    public static class InvalidPlanException extends RuntimeException {
        public InvalidPlanException(String message) { super(message); }
    }
}
