package pl.ticket.aitoolsgateway.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.event.handler", havingValue = "fake", matchIfMissing = true)
public class FakeEventToolHandler implements EventToolHandler {

    private static final List<EventSearchResponse.EventSummary> EVENTS = List.of(
            new EventSearchResponse.EventSummary(
                    9001L,
                    9101L,
                    "Spring Festival",
                    "Letni festiwal muzyczny w Warszawie.",
                    null,
                    LocalDate.of(2026, 7, 12),
                    LocalTime.of(19, 0),
                    new BigDecimal("199.99"),
                    42L
            ),
            new EventSearchResponse.EventSummary(
                    9002L,
                    9102L,
                    "Architecture Workshop",
                    "Warsztaty architektoniczne w Krakowie.",
                    null,
                    LocalDate.of(2026, 8, 3),
                    LocalTime.of(10, 0),
                    new BigDecimal("79.50"),
                    0L
            ),
            new EventSearchResponse.EventSummary(
                    9003L,
                    9103L,
                    "Jazz Night",
                    "Wieczor jazzowy w Warszawie.",
                    null,
                    LocalDate.of(2026, 7, 12),
                    LocalTime.of(20, 30),
                    new BigDecimal("129.00"),
                    12L
            )
    );

    @Override
    public EventSearchResponse searchEvents(EventSearchRequest request) {
        EventSearchRequest.Filters filters = request == null ? null : request.filters();

        return new EventSearchResponse(EVENTS.stream()
                .filter(event -> matches(event, filters))
                .toList());
    }

    private boolean matches(EventSearchResponse.EventSummary event, EventSearchRequest.Filters filters) {
        if (filters == null) {
            return true;
        }

        return matchesText(event.title(), filters.query())
                && matchesDateRange(event.date(), filters.dateFrom(), filters.dateTo())
                && matchesAvailability(event.availableTickets(), filters.onlyAvailable());
    }

    private boolean matchesText(String value, String expected) {
        return expected == null || expected.isBlank() || value.toLowerCase().contains(expected.toLowerCase());
    }

    private boolean matchesDateRange(java.time.LocalDate value, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        return (dateFrom == null || !value.isBefore(dateFrom))
                && (dateTo == null || !value.isAfter(dateTo));
    }

    private boolean matchesAvailability(Long availableTickets, Boolean onlyAvailable) {
        return !Boolean.TRUE.equals(onlyAvailable) || availableTickets > 0;
    }
}
