package pl.ticket.event.admin.event_occurrence.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;
import pl.ticket.event.admin.ticket.service.AdminTicketService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminEventOccurrenceService
{
    private final AdminEventOccurrenceRepository eventOccurrenceRepository;
    private final AdminTicketService adminTicketService;

    public AdminEventOccurrence addEventOccurrence(AdminEventOccurrence eventOccurrence)
    {

        return eventOccurrenceRepository.save(eventOccurrence);
    }

    public void createEventOccurrences(List<AdminEventOccurrence> eventOccurrences)
    {
        eventOccurrenceRepository.saveAll(eventOccurrences);
    }

    public List<AdminEventOccurrence> findByEventId(Long eventId)
    {
        return eventOccurrenceRepository.findByEventId(eventId);
    }

    public void deleteOccurrences(List<AdminEventOccurrence> eventOccurrences)
    {
        eventOccurrenceRepository.deleteAll(eventOccurrences);
    }

    public void deleteOccurrenceWithTickets(Long id)
    {
        AdminEventOccurrence adminEventOccurrence = eventOccurrenceRepository.findById(id).orElseThrow();

        adminTicketService.deleteTickets(adminEventOccurrence.getTickets());

        eventOccurrenceRepository.deleteById(id);
    }
}
