package pl.ticket.aitoolsgateway.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventToolHandler {

    EventSearchResponse searchEvents(EventSearchRequest request);

    EventCapacityResponse checkEventCapacity(EventCapacityRequest request);

    EventDetailsResponse getEventDetails(EventDetailsRequest request);

    record EventSearchRequest(
            String eventName,
            LocalDate dateFrom,
            LocalDate dateTo,
            String city
    ) {
    }

    record EventSearchResponse(
            List<EventSummary> events
    ) {
    }

    record EventSummary(
            Long eventId,
            String eventName,
            LocalDateTime occurrenceDate,
            String city,
            String venue,
            int availableSeats,
            BigDecimal minPrice
    ) {
    }

    record EventCapacityRequest(
            String eventName,
            LocalDate occurrenceDate,
            String city
    ) {
    }

    record EventCapacityResponse(
            Long eventId,
            String eventName,
            LocalDateTime occurrenceDate,
            String city,
            boolean hasAvailableCapacity,
            int availableSeats,
            String status,
            boolean found
    ) {
    }

    record EventDetailsRequest(
            String eventName,
            LocalDate occurrenceDate,
            String city
    ) {
    }

    record EventDetailsResponse(
            Long eventId,
            String eventName,
            LocalDateTime occurrenceDate,
            String city,
            String venue,
            String ticketType,
            BigDecimal minPrice,
            int availableSeats,
            boolean hasAvailableCapacity,
            boolean found
    ) {
    }
}
