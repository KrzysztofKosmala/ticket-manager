package pl.ticket.aiagent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AiAgentResponse(
        String answer,
        Status status,
        String conversationId
) {

    public AiAgentResponse(String answer, Status status) {
        this(answer, status, null);
    }

    public enum Status {
        COMPLETED,
        PARTIAL,
        FALLBACK,
        FAILED
    }
}
