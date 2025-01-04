package pl.ticket.event.customer.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EventDto;
import pl.ticket.dto.EventOccurrenceDto;
import pl.ticket.event.customer.event.exception.EventDateException;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.event_occurrence.repository.EventOccurrenceRepository;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.event.model.dto.EventTicketDto;
import pl.ticket.event.customer.ticket.repository.TicketRepository;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventOccurrenceRepository eventOccurrenceRepository;
    private final TicketRepository ticketRepository;

    private final EventMapper eventMapper;

    public EventDto getEventById(Long id) {
        Event event = eventRepository.findByIdWithOccurrences(id).orElseThrow();

        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .capacity(event.getCapacity())
                .slug(event.getSlug())
                .categoryId(event.getCategoryId())
                .occurrences(event.getOccurrences().stream()
                        .map(this::mapToEventOccurrenceDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public EventDto getEventByIdAndDate(Long eventId, LocalDate date) {

        Event event = eventRepository.findByIdWithMatchingDateOccurrences(eventId, date)
                .orElseThrow(() -> new EventDateException("Nie znaleziono takiego wydarzenia."));

        return eventMapper.mapToEventDto(event);
    }

    public Page<EventDto> getEvents(Pageable pageable) {
        List<Event> allPaged = eventRepository.findAllPaged(pageable);
        List<EventDto> pagedEventsDto = allPaged.stream().map(eventMapper::mapToEventDtoWithoutOccurrences).collect(Collectors.toList());
        return new PageImpl<>(pagedEventsDto, pageable, pagedEventsDto.size());
    }




    /*TODO: poprawić reszte i testy*/
    public Page<EventDateTimeDto> getEventsByDate(LocalDate date, Pageable pageable) {

        List<Event> events = eventRepository.findByDatePaged(date, pageable);
        List<EventDateTimeDto> result = eventMapper.mapToListEventDateTimeDto(events, date);

        if (result.isEmpty()) {
            throw new EventDateException("W danym dniu nie ma żadnego wydarzenia!");
        }
        return new PageImpl<>(result, pageable, result.size());
    }

    public CapacityCheckResponse checkCapacity(Integer eventId) {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }

    /*TODO: skoro chcemy zwrócić event occurance to chyba powinno być w paczce event occurance a nie event*/
    public List<EventTicketDto> getEventOccurrenceByDateAndTime(Long eventId, String time, LocalDate date) {
        LocalTime timeParsed = LocalTime.parse(time);
        /*TODO:
           - te dwa requesty da sie pewnie zrobić za jednym zamachem wyciągając przez ticket repository z joinem na event occurance i odpoweidnimi warunkami na czas i id eventu
           - nie powinno sie korzystać z repo z innej paczki bezpośrednio tylko korzystać z servisu z innej paczki także jak coś to nie ticketRepository tylko korzystamy z ticketService*/
        EventOccurrence eventOccurrence = eventOccurrenceRepository.findEventOccurrenceByEventIdAndTime(eventId, timeParsed, date);
        List<Ticket> ticketsForOccurrence = ticketRepository.findTicketsOccurrenceId(eventOccurrence.getId());

        return ticketsForOccurrence.stream()
                .map(ticket -> eventMapper.mapToEventTicketDto(ticket)).toList();
    }

    private EventOccurrenceDto mapToEventOccurrenceDto(EventOccurrence occurrence) {
        return EventOccurrenceDto.builder()
                .id(occurrence.getId())
                .eventId(occurrence.getEventId())
                .date(occurrence.getDate())
                .time(occurrence.getTime())
                .build();
    }
}
