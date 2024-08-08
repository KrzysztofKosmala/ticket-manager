package pl.ticket.event.admin.event_occurrence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;

@Service
@RequiredArgsConstructor
public class AdminEventOcurrenceService
{
    private final AdminEventOccurrenceRepository eventOccurrenceRepository;

    public AdminEventOccurrence addEventOccurrence(AdminEventOccurrence eventOccurrence)
    {

        return eventOccurrenceRepository.save(eventOccurrence);
    }
}
