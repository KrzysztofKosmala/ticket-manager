package pl.ticket.knowledge.model;

public record KnowledgeIngestionResult(
        String ingestionId,
        int loadedDocumentCount,
        int chunkCount
) {
}
