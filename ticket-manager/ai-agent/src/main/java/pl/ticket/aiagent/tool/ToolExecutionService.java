package pl.ticket.aiagent.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import pl.ticket.aiagent.planner.Plan;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
public class ToolExecutionService {

    private final ToolRegistry toolRegistry;
    private final ToolPolicyService toolPolicyService;
    private final ToolAuditService toolAuditService;
    private final ObjectMapper objectMapper;
    private final ObjectProvider<ToolCallbackProvider> toolCallbackProvider;

    public ToolExecutionService(
            ToolRegistry toolRegistry,
            ToolPolicyService toolPolicyService,
            ToolAuditService toolAuditService,
            ObjectMapper objectMapper,
            ObjectProvider<ToolCallbackProvider> toolCallbackProvider
    ) {
        this.toolRegistry = toolRegistry;
        this.toolPolicyService = toolPolicyService;
        this.toolAuditService = toolAuditService;
        this.objectMapper = objectMapper;
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public Object execute(Plan.Step step, CallerContext callerContext) {
        if (step == null || step.name() == null) {
            return null;
        }
        ToolContract contract = toolRegistry.getRequired(step.name());
        toolPolicyService.assertAllowed(contract, callerContext);
        Object typedArgs = convertArgs(step.args(), contract.inputType());

        Instant startedAt = Instant.now();
        try {
            Object result = executeMcp(contract, typedArgs);
            toolAuditService.success(callerContext, contract, typedArgs, Duration.between(startedAt, Instant.now()));
            return result;
        } catch (RuntimeException ex) {
            toolAuditService.failure(callerContext, contract, typedArgs, Duration.between(startedAt, Instant.now()), ex);
            throw ex;
        }
    }

    private Object executeMcp(ToolContract contract, Object typedArgs) {
        FunctionCallback callback = findToolCallback(contract);
        String toolInput = toMcpToolInput(typedArgs);
        String rawResult = callback.call(toolInput);
        return readResult(rawResult, contract.outputType());
    }

    private FunctionCallback findToolCallback(ToolContract contract) {
        ToolCallbackProvider provider = toolCallbackProvider.getIfAvailable();
        if (provider == null) {
            throw new ToolExecutionException("MCP tool callback provider is not available");
        }
        for (FunctionCallback callback : provider.getToolCallbacks()) {
            String discoveredName = callback.getName();
            if (contract.name().equals(discoveredName) || contract.legacyName().equals(discoveredName)) {
                return callback;
            }
        }
        throw new ToolExecutionException("MCP tool not found: " + contract.name());
    }

    private Object convertArgs(Map<String, Object> args, Class<?> inputType) {
        if (args == null) {
            return null;
        }
        return objectMapper.convertValue(args, inputType);
    }

    private String toMcpToolInput(Object typedArgs) {
        try {
            if (typedArgs == null) {
                return "{}";
            }
            return objectMapper.writeValueAsString(Map.of("request", typedArgs));
        } catch (JsonProcessingException ex) {
            throw new ToolExecutionException("Cannot serialize MCP tool input", ex);
        }
    }

    private Object readResult(String rawResult, Class<?> outputType) {
        try {
            return objectMapper.readValue(rawResult, outputType);
        } catch (JsonProcessingException ex) {
            throw new ToolExecutionException("Cannot parse MCP tool result", ex);
        }
    }
}
