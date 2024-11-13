package pl.ticket.event.internal.ticket.service;

import org.springframework.context.annotation.Configuration;
import pl.ticket.dto.EventDto;
import pl.ticket.dto.EventOccurrenceDto;
import pl.ticket.dto.TicketWithDetailsDto;

import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.internal.event.model.InternalEvent;
import pl.ticket.event.internal.ticket.model.InternalTicket;

@Configuration
public class InternalTicketMapper
{
    public TicketWithDetailsDto toTicketWithDetailsDto(InternalTicket internalTicket) {
        return TicketWithDetailsDto.builder()
                .id(internalTicket.getId())
                .price(internalTicket.getPrice())
                .amount(internalTicket.getAmount())
                .eventOccurrence(convertToDto(internalTicket.getEventOccurrence()))  // Konwersja na EventOccurrenceDto
                .event(convertToDto(internalTicket.getEvent()))                      // Konwersja na EventDto
                .version(internalTicket.getVersion())
                .build();
    }

    // Metoda konwertująca EventOccurrence na EventOccurrenceDto
    private EventOccurrenceDto convertToDto(EventOccurrence eventOccurrence) {
        return EventOccurrenceDto.builder()
                .id(eventOccurrence.getId())
                .eventId(eventOccurrence.getEventId())
                .date(eventOccurrence.getDate())
                .time(eventOccurrence.getTime())
                .build();
    }

    // Metoda konwertująca Event na EventDto
    private EventDto convertToDto(InternalEvent event) {
        return EventDto.builder()
                .id(event.getId())
                .capacity(event.getCapacity())
                .categoryId(event.getCategoryId())
                .title(event.getTitle())
                .description(event.getDescription())
                .slug(event.getSlug())
                .build();
    }
}
