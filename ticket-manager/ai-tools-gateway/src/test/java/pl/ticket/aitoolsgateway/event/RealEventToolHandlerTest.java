package pl.ticket.aitoolsgateway.event;

import org.junit.jupiter.api.Test;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.feign.event.EventClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RealEventToolHandlerTest {

    @Test
    void shouldDelegateSearchToEventClient() {
        EventClient eventClient = mock(EventClient.class);
        RealEventToolHandler handler = new RealEventToolHandler(eventClient);
        EventSearchRequest request = new EventSearchRequest(
                new EventSearchRequest.Filters("Dziady", LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), null, null),
                null,
                10,
                0
        );
        EventSearchResponse expectedResponse = new EventSearchResponse(List.of(new EventSearchResponse.EventSummary(
                        1L,
                        10L,
                        "Dziady",
                        "Dramat Adama Mickiewicza",
                        1L,
                        LocalDate.of(2025, 2, 15),
                        LocalTime.of(20, 0),
                        new BigDecimal("25.00"),
                        100L
                )));
        when(eventClient.searchEvents(request)).thenReturn(expectedResponse);

        EventSearchResponse response = handler.searchEvents(request);

        assertThat(response)
                .isSameAs(expectedResponse);
        verify(eventClient).searchEvents(request);
    }
}
