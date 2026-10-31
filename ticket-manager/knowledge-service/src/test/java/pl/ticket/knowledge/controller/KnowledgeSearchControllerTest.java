package pl.ticket.knowledge.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.ticket.knowledge.service.KnowledgeSearchService;
import pl.ticket.dto.KnowledgeSearchResult;

import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KnowledgeSearchController.class)
class KnowledgeSearchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private KnowledgeSearchService service;

    @Test
    void shouldReturnSearchResultsAsJson() throws Exception {
        when(service.search("What does PAID mean?", 3)).thenReturn(List.of(
                new KnowledgeSearchResult("Payment accepted.", "statuses.md", 0.9)
        ));

        mvc.perform(post("/internal/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"query":"What does PAID mean?","topK":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Payment accepted."))
                .andExpect(jsonPath("$[0].source").value("statuses.md"))
                .andExpect(jsonPath("$[0].score").value(0.9));
    }

    @Test
    void shouldReturnAnEmptyJsonArrayWhenNothingWasFound() throws Exception {
        when(service.search("Unknown topic", 3)).thenReturn(List.of());

        mvc.perform(post("/internal/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"query":"Unknown topic","topK":3}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"query\":\"   \",\"topK\":3}",
            "{\"topK\":3}",
            "{\"query\":\"Payment\",\"topK\":0}",
            "{\"query\":\"Payment\",\"topK\":11}",
            "{\"query\":\"Payment\"}"
    })
    void shouldRejectInvalidInputBeforeCallingTheModel(String body) throws Exception {
        mvc.perform(post("/internal/knowledge/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
