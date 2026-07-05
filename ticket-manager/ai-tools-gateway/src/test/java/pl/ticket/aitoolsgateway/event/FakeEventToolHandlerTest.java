package pl.ticket.aitoolsgateway.event;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FakeEventToolHandlerTest {

    @Test
    void shouldSearchEventsByDateAndName() {
        FakeEventToolHandler handler = new FakeEventToolHandler();
        EventToolHandler.EventSearchRequest request = new EventToolHandler.EventSearchRequest(
                "spring",
                LocalDate.of(2026, 7, 12),
                LocalDate.of(2026, 7, 12),
                null
        );

        EventToolHandler.EventSearchResponse response = handler.searchEvents(request);

        assertThat(response.events())
                .containsExactly(new EventToolHandler.EventSummary(
                        9001L,
                        "Spring Festival",
                        LocalDateTime.of(2026, 7, 12, 19, 0),
                        "Warsaw",
                        "Main Hall",
                        42,
                        new BigDecimal("199.99")
                ));
    }

    @Test
    void shouldReturnCapacityByEventNameAndDate() {
        FakeEventToolHandler handler = new FakeEventToolHandler();
        EventToolHandler.EventCapacityRequest request = new EventToolHandler.EventCapacityRequest(
                "Spring Festival",
                LocalDate.of(2026, 7, 12),
                "Warsaw"
        );

        EventToolHandler.EventCapacityResponse response = handler.checkEventCapacity(request);

        assertThat(response)
                .isEqualTo(new EventToolHandler.EventCapacityResponse(
                        9001L,
                        "Spring Festival",
                        LocalDateTime.of(2026, 7, 12, 19, 0),
                        "Warsaw",
                        true,
                        42,
                        "AVAILABLE",
                        true
                ));
    }

    @Test
    void shouldReturnEventDetailsByName() {
        FakeEventToolHandler handler = new FakeEventToolHandler();
        EventToolHandler.EventDetailsRequest request = new EventToolHandler.EventDetailsRequest("Architecture Workshop", null, null);

        EventToolHandler.EventDetailsResponse response = handler.getEventDetails(request);

        assertThat(response)
                .isEqualTo(new EventToolHandler.EventDetailsResponse(
                        9002L,
                        "Architecture Workshop",
                        LocalDateTime.of(2026, 8, 3, 10, 0),
                        "Krakow",
                        "Studio 2",
                        "STANDARD",
                        new BigDecimal("79.50"),
                        0,
                        false,
                        true
                ));
    }
}
