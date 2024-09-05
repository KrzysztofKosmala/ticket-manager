package pl.ticket.event.admin.integrity.event.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;
import pl.ticket.event.admin.integrity.event.PrePost;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminEventOccurrenceDeleteTest extends PrePost
{

    @Autowired
    private AdminEventOccurrenceRepository eventOccurrenceRepository;

    @Autowired
    private AdminTicketRepository adminTicketRepository;
    @Autowired
    private AdminEventRepository adminEventRepository;
    @Autowired
    private AdminEventOccurrenceService adminEventOccurrenceService;
    @Test
    public void shouldDeleteEventOccurrencesWithTickets()
    {
        adminEventOccurrenceService.deleteOccurrenceWithTickets(111L);

        long countedEventsAfter = adminEventRepository.count();
        long countedOccurrencesAfter = eventOccurrenceRepository.count();
        long countedTicketsAfter = adminTicketRepository.count();

        assertEquals(2, countedEventsAfter , "Unexpected event count after creation");
        assertEquals(2, countedOccurrencesAfter , "Unexpected occurrences count after creation");
        assertEquals(4, countedTicketsAfter , "Unexpected tickets count after creation");
    }
}
