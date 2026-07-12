package pl.ticket.aiagent.tools;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_tool_invocation")
public class AiToolInvocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String conversationId;
    private String runId;
    private String toolName;
    @Column(columnDefinition = "TEXT")
    private String argumentsJson;
    @Column(columnDefinition = "TEXT")
    private String resultJson;
    @Enumerated(EnumType.STRING)
    private ToolInvocationStatus status;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    private LocalDateTime createdAt;

    protected AiToolInvocationEntity() {
    }

    public AiToolInvocationEntity(
            String conversationId,
            String runId,
            String toolName,
            String argumentsJson,
            String resultJson,
            ToolInvocationStatus status,
            String errorMessage
    ) {
        this.conversationId = conversationId;
        this.runId = runId;
        this.toolName = toolName;
        this.argumentsJson = argumentsJson;
        this.resultJson = resultJson;
        this.status = status;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
    }

    public ToolInvocation toToolInvocation() {
        return new ToolInvocation(conversationId, runId, toolName, argumentsJson, status, resultJson, errorMessage);
    }
}
