package pl.ticket.aitoolsgateway.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.event.handler", havingValue = "fake", matchIfMissing = true)
public class FakeEventToolHandler implements EventToolHandler {

    private static final List<EventDetailsResponse> EVENTS = List.of(
            new EventDetailsResponse(
                    9001L,
                    "Spring Festival",
                    LocalDateTime.of(2026, 7, 12, 19, 0),
                    "Warsaw",
                    "Main Hall",
                    "VIP",
                    new BigDecimal("199.99"),
                    42,
                    true,
                    true
            ),
            new EventDetailsResponse(
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
            ),
            new EventDetailsResponse(
                    9003L,
                    "Jazz Night",
                    LocalDateTime.of(2026, 7, 12, 20, 30),
                    "Warsaw",
                    "Blue Stage",
                    "STANDARD",
                    new BigDecimal("129.00"),
                    12,
                    true,
                    true
            )
    );

    @Override
    public EventSearchResponse searchEvents(EventSearchRequest request) {
        return new EventSearchResponse(EVENTS.stream()
                .filter(event -> matches(event, request))
                .map(this::toSummary)
                .toList());
    }

    @Override
    public EventCapacityResponse checkEventCapacity(EventCapacityRequest request) {
        return EVENTS.stream()
                .filter(event -> matches(event, request))
                .findFirst()
                .map(this::toCapacity)
                .orElseGet(() -> missingCapacity(request));
    }

    @Override
    public EventDetailsResponse getEventDetails(EventDetailsRequest request) {
        return EVENTS.stream()
                .filter(event -> matches(event, request))
                .findFirst()
                .orElseGet(() -> missingDetails(request));
    }

    private boolean matches(EventDetailsResponse event, EventSearchRequest request) {
        if (request == null) {
            return true;
        }

        return matchesText(event.eventName(), request.eventName())
                && matchesDateRange(event.occurrenceDate().toLocalDate(), request.dateFrom(), request.dateTo())
                && matchesText(event.city(), request.city());
    }

    private boolean matches(EventDetailsResponse event, EventCapacityRequest request) {
        if (request == null) {
            return true;
        }

        return matchesText(event.eventName(), request.eventName())
                && matchesDate(event.occurrenceDate().toLocalDate(), request.occurrenceDate())
                && matchesText(event.city(), request.city());
    }

    private boolean matches(EventDetailsResponse event, EventDetailsRequest request) {
        if (request == null) {
            return true;
        }

        return matchesText(event.eventName(), request.eventName())
                && matchesDate(event.occurrenceDate().toLocalDate(), request.occurrenceDate())
                && matchesText(event.city(), request.city());
    }

    private boolean matchesText(String value, String expected) {
        return expected == null || expected.isBlank() || value.toLowerCase().contains(expected.toLowerCase());
    }

    private boolean matchesDate(LocalDate value, LocalDate expected) {
        return expected == null || value.equals(expected);
    }

    private boolean matchesDateRange(LocalDate value, LocalDate dateFrom, LocalDate dateTo) {
        return (dateFrom == null || !value.isBefore(dateFrom))
                && (dateTo == null || !value.isAfter(dateTo));
    }

    private EventSummary toSummary(EventDetailsResponse event) {
        return new EventSummary(
                event.eventId(),
                event.eventName(),
                event.occurrenceDate(),
                event.city(),
                event.venue(),
                event.availableSeats(),
                event.minPrice()
        );
    }

    private EventCapacityResponse toCapacity(EventDetailsResponse event) {
        return new EventCapacityResponse(
                event.eventId(),
                event.eventName(),
                event.occurrenceDate(),
                event.city(),
                event.hasAvailableCapacity(),
                event.availableSeats(),
                event.hasAvailableCapacity() ? "AVAILABLE" : "SOLD_OUT",
                true
        );
    }

    private EventCapacityResponse missingCapacity(EventCapacityRequest request) {
        return new EventCapacityResponse(
                null,
                request == null ? null : request.eventName(),
                null,
                request == null ? null : request.city(),
                false,
                0,
                "UNKNOWN",
                false
        );
    }

    private EventDetailsResponse missingDetails(EventDetailsRequest request) {
        return new EventDetailsResponse(
                null,
                request == null ? null : request.eventName(),
                null,
                request == null ? null : request.city(),
                null,
                null,
                BigDecimal.ZERO,
                0,
                false,
                false
        );
    }
}
