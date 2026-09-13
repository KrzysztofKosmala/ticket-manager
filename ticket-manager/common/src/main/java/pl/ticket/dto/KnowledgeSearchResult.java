package pl.ticket.dto;

public record KnowledgeSearchResult(
        String text,
        String source,
        Double score
) {
}
