package pl.ticket.aitoolsgateway.event;

import org.junit.jupiter.api.Test;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class FakeEventToolHandlerTest {

    @Test
    void shouldSearchEventsByDateAndName() {
        FakeEventToolHandler handler = new FakeEventToolHandler();
        EventSearchRequest request = new EventSearchRequest(
                new EventSearchRequest.Filters("spring", LocalDate.of(2026, 7, 12), LocalDate.of(2026, 7, 12), null, null),
                null,
                10,
                0
        );

        EventSearchResponse response = handler.searchEvents(request);

        assertThat(response.items())
                .containsExactly(new EventSearchResponse.EventSummary(
                        9001L,
                        9101L,
                        "Spring Festival",
                        "Letni festiwal muzyczny w Warszawie.",
                        null,
                        LocalDate.of(2026, 7, 12),
                        LocalTime.of(19, 0),
                        new BigDecimal("199.99"),
                        42L
                ));
    }
}
