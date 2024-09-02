package pl.ticket.event.admin.integrity.event.admin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.ticket.event.admin.event.data_provider.AdminEventRegularCreationDtoProvider;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event.service.AdminEventService;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;
import pl.ticket.event.admin.integrity.PrePost;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdminEventDeleteTest extends PrePost
{

    @Autowired
    private AdminEventService adminEventService;

    @Autowired
    private AdminEventRepository adminEventRepository;

    @Autowired
    private AdminEventOccurrenceRepository eventOccurrenceRepository;

    @Autowired
    private AdminTicketRepository adminTicketRepository;

    @Test
    public void shouldDeleteEventWithOccurrencesAndTickets()
    {

        adminEventService.deleteEventById(111L);


        long countedEventsAfter = adminEventRepository.count();
        long countedOccurrencesAfter = eventOccurrenceRepository.count();
        long countedTicketsAfter = adminTicketRepository.count();

        assertEquals(1, countedEventsAfter , "Unexpected event count after creation");
        assertEquals(1, countedOccurrencesAfter , "Unexpected occurrences count after creation");
        assertEquals(2, countedTicketsAfter , "Unexpected tickets count after creation");
    }
}
