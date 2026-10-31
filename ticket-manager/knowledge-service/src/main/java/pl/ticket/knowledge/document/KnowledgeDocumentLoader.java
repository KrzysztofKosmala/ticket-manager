package pl.ticket.knowledge.document;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KnowledgeDocumentLoader {

    private final List<Resource> resources;

    public KnowledgeDocumentLoader(@Value("classpath*:knowledge/*.md") Resource[] resources) {
        this.resources = List.of(resources);
    }

    public List<Document> load() {
        return resources.stream()
                .flatMap(resource -> read(resource).stream())
                .toList();
    }

    private List<Document> read(Resource resource) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .withAdditionalMetadata(KnowledgeMetadata.SOURCE, resource.getFilename())
                .withAdditionalMetadata(KnowledgeMetadata.DOCUMENT_TYPE, "KNOWLEDGE")
                .build();

        return new MarkdownDocumentReader(resource, config).read();
    }
}
