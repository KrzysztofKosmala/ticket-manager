package pl.ticket.event.customer.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.model.dto.EventTicketDto;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.ticket.model.Ticket;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@RequiredArgsConstructor
@Component
public class EventMapper {

    public EventDateTimeDto mapToEventDateTimeDto(Event event, LocalDate date, List<LocalTime> times){
        return EventDateTimeDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .date(date)
                .times(times)
                .build();
    }
    public List<EventDateTimeDto> mapToListEventDateTimeDto(List<Event> events, LocalDate date){
        return  events.stream()
                .map(event -> {
                    EventDateTimeDto eventDateTimeDto = new EventDateTimeDto();
                    eventDateTimeDto.setTitle(event.getTitle());
                    eventDateTimeDto.setDescription(event.getDescription());
                    eventDateTimeDto.setDate(date);

                    eventDateTimeDto.setTimes(event.getOccurrences().stream()
                            .map(EventOccurrence::getTime).toList());

                    return eventDateTimeDto;
                }).toList();
    }

    public EventTicketDto mapToEventTicketDto(Ticket ticket) {
        return EventTicketDto.builder()
                .price(ticket.getPrice())
                .type(ticket.getType())
                .amount(ticket.getAmount())
                .build();
    }
}
