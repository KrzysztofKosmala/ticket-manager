package pl.ticket.aiagent.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AiAgentResponse(
        String answer,
        Status status
) {

    public enum Status {
        COMPLETED,
        PARTIAL,
        FALLBACK,
        FAILED
    }
}
