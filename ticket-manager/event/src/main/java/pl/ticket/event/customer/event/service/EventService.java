package pl.ticket.event.customer.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.common.dto.EventDto;
import pl.ticket.event.common.dto.EventOccurrenceDto;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService
{
    private final EventRepository eventRepository;


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

    private EventOccurrenceDto mapToEventOccurrenceDto(EventOccurrence occurrence) {
        return EventOccurrenceDto.builder()
                .id(occurrence.getId())
                .eventId(occurrence.getEventId())
                .date(occurrence.getDate())
                .time(occurrence.getTime())
                .spaceLeft(occurrence.getSpaceLeft())
                .build();
    }

    public CapacityCheckResponse checkCapacity(Integer eventId)
    {
        return new CapacityCheckResponse(eventRepository.hasAvailableCapacity(eventId));
    }
}
