package pl.ticket.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record KnowledgeSearchRequest(
        @NotBlank String query,
        @Min(1) @Max(10) int topK
) {
}
