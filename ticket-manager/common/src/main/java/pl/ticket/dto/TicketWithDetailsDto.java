package pl.ticket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class TicketWithDetailsDto
{
    private Long id;
    private BigDecimal price;
    private int amount;
    private EventOccurrenceDto eventOccurrence;
    private EventDto event;
    private Long version;
}
