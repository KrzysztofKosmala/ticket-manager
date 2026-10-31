package pl.ticket.aiagent.model.run;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_agent_run")
public class AiAgentRunEntity {

    @Id
    private String id;
    private String conversationId;
    @Column(columnDefinition = "TEXT")
    private String userMessage;
    @Column(columnDefinition = "TEXT")
    private String selectedToolsJson;
    @Column(columnDefinition = "TEXT")
    private String answer;
    private String status;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    protected AiAgentRunEntity() {
    }

    public AiAgentRunEntity(
            String id,
            String conversationId,
            String userMessage,
            String selectedToolsJson,
            LocalDateTime startedAt
    ) {
        this.id = id;
        this.conversationId = conversationId;
        this.userMessage = userMessage;
        this.selectedToolsJson = selectedToolsJson;
        this.status = "RUNNING";
        this.startedAt = startedAt;
    }

    public void complete(String answer) {
        this.answer = answer;
        this.status = "COMPLETED";
        this.completedAt = LocalDateTime.now();
    }

    public void fail(Exception exception) {
        this.status = "FAILED";
        this.errorMessage = exception.getMessage();
        this.completedAt = LocalDateTime.now();
    }

    public String getSelectedToolsJson() {
        return selectedToolsJson;
    }

    public String getAnswer() {
        return answer;
    }

    public String getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
