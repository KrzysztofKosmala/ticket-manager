package pl.ticket.event.internal.event.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.event.internal.event.repository.EventAiSearchRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventAiSearchServiceTest {

    @Test
    void shouldSearchEventsAndMapOccurrenceAvailability() {
        EventAiSearchRepository repository = mock(EventAiSearchRepository.class);
        EventAiSearchService service = new EventAiSearchService(repository);
        EventSearchRequest request = new EventSearchRequest(
                new EventSearchRequest.Filters("Dziady", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), null, true),
                null,
                10,
                0
        );
        EventSearchResponse.EventSummary event = new EventSearchResponse.EventSummary(
                1L,
                10L,
                "Dziady",
                "Dramat Adama Mickiewicza",
                1L,
                LocalDate.of(2025, 2, 15),
                LocalTime.of(20, 0),
                new BigDecimal("25.00"),
                100L
        );
        when(repository.search("Dziady", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), null, true, PageRequest.of(0, 10)))
                .thenReturn(List.of(event));

        EventSearchResponse response = service.searchEvents(request);

        assertThat(response.items())
                .containsExactly(event);
    }
}
