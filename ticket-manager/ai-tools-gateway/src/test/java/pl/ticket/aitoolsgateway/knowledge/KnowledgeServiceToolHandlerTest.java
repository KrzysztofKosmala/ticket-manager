package pl.ticket.aitoolsgateway.knowledge;

import org.junit.jupiter.api.Test;
import pl.ticket.dto.KnowledgeSearchRequest;
import pl.ticket.dto.KnowledgeSearchResult;
import pl.ticket.feign.knowledge.KnowledgeClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KnowledgeServiceToolHandlerTest {

    @Test
    void shouldSearchThreeKnowledgeChunksByDefault() {
        KnowledgeClient knowledgeClient = mock(KnowledgeClient.class);
        KnowledgeSearchRequest request = new KnowledgeSearchRequest("What does PAID mean?", 3);
        List<KnowledgeSearchResult> expected = List.of(
                new KnowledgeSearchResult("PAID means accepted payment.", "statuses.md", 0.9)
        );
        when(knowledgeClient.search(request)).thenReturn(expected);
        KnowledgeServiceToolHandler handler = new KnowledgeServiceToolHandler(knowledgeClient);

        List<KnowledgeSearchResult> result = handler.searchKnowledge("What does PAID mean?");

        assertThat(result).isSameAs(expected);
        verify(knowledgeClient).search(request);
    }
}
