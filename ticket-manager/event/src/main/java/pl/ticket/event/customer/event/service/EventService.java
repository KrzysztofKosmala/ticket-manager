package pl.ticket.event.customer.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EventDto;
import pl.ticket.event.customer.event.exception.EventDataException;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event_occurrence.repository.EventOccurrenceRepository;
import pl.ticket.event.customer.ticket.repository.TicketRepository;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;

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
                        .map(eventMapper::mapToEventOccurrenceDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public EventDto getEventByIdAndDate(Long eventId, LocalDate date) {

        Event event = eventRepository.findByIdWithMatchingDateOccurrences(eventId, date)
                .orElseThrow(() -> new EventDataException("Nie znaleziono takiego wydarzenia."));

        return eventMapper.mapToEventDto(event);
    }

    public Page<EventDto> getEvents(Pageable pageable) {
        List<Event> allPaged = eventRepository.findAllPaged(pageable);
        List<EventDto> pagedEventsDto = allPaged.stream().map(eventMapper::mapToEventDtoWithoutOccurrences).collect(Collectors.toList());
        return new PageImpl<>(pagedEventsDto, pageable, pagedEventsDto.size());
    }

    public Page<EventDto> getEventsByDate(LocalDate date, Pageable pageable) {

        List<Event> events = eventRepository.findByDatePaged(date, pageable);

        if (events.isEmpty()) {
            throw new EventDataException("W danym dniu nie ma żadnego wydarzenia!");
        }

        List<EventDto> result = events.stream().map(eventMapper::mapToEventDto).toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    public CapacityCheckResponse checkCapacity(Integer eventId) {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }


}
