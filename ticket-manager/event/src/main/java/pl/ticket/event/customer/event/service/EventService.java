package pl.ticket.event.customer.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ticket.event.common.dto.AdminEventDto;
import pl.ticket.event.common.dto.AdminEventOccurrenceDto;
import pl.ticket.event.customer.event.exception.EventDateException;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public AdminEventDto getEventById(Long id) {
        Event event = eventRepository.findByIdWithOccurrences(id).orElseThrow();

        return AdminEventDto.builder()
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

    public EventDateTimeDto getEventByIdAndDate(Long eventId, LocalDate date) {
        Optional<Event> eventById = eventRepository.findByIdWithOccurrences(eventId);

        if(!eventById.isPresent()){
            throw new EventDateException("Nie znaleziono wydarzenia o podanym id: " + eventId);
        }
        Event event = eventById.get();
        List<EventOccurrence> occurrences = event.getOccurrences();

        List<LocalTime> times = occurrences.stream()
                .filter(occurrence -> occurrence.getDate().equals(date))
                .map(occurrence -> occurrence.getTime()).toList();

        if (times.isEmpty()){
            throw new EventDateException("W dniu " + date + " nie ma takiego wydarzenia");
        }

        return EventDateTimeDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .date(date)
                .times(times)
                .build();
    }

    public Page<Event> getEvents(Pageable pageable) {
        List<Event> allEventsPaged = eventRepository.findAllEventsPaged(pageable);
        return new PageImpl<>(allEventsPaged, pageable, allEventsPaged.size());
    }

    public Page<EventDateTimeDto> getEventsByDate(LocalDate date, Pageable pageable) {
        List<Event> events = eventRepository.findByDatePaged(date, pageable);

        List<EventDateTimeDto> result = events.stream()
                .map(event -> {
                    EventDateTimeDto eventDateTimeDto = new EventDateTimeDto();
                    eventDateTimeDto.setTitle(event.getTitle());
                    eventDateTimeDto.setDescription(event.getDescription());
                    eventDateTimeDto.setDate(date);

                    eventDateTimeDto.setTimes(event.getOccurrences().stream()
                            .map(EventOccurrence::getTime).toList());

                    return eventDateTimeDto;
                }).toList();


        if (result.isEmpty()) {
            throw new EventDateException("W danym dniu nie ma żadnego wydarzenia!");
        }
        return new PageImpl<>(result, pageable, result.size());
    }

    public CapacityCheckResponse checkCapacity(Integer eventId) {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }
    private AdminEventOccurrenceDto mapToEventOccurrenceDto(EventOccurrence occurrence) {
        return AdminEventOccurrenceDto.builder()
                .id(occurrence.getId())
                .eventId(occurrence.getEventId())
                .date(occurrence.getDate())
                .time(occurrence.getTime())
                .build();
    }
}
