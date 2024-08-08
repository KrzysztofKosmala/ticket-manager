package pl.ticket.event.customer.event_occurrence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;
import pl.ticket.event.customer.event_occurrence.repository.EventOccurrenceRepository;

@Service
@RequiredArgsConstructor
public class EventOcurrenceService
{
    private final EventOccurrenceRepository eventOccurrenceRepository;

    public EventOccurrence addEventOccurrence(EventOccurrence eventOccurrence)
    {

        return eventOccurrenceRepository.save(eventOccurrence);
    }
}
