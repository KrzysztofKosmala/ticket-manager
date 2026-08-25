package pl.ticket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventSearchResponse(
        List<EventSummary> items
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EventSummary(
            Long eventId,
            Long occurrenceId,
            String title,
            String description,
            Long categoryId,
            LocalDate date,
            LocalTime time,
            BigDecimal minPrice,
            Long availableTickets
    ) {}
}
