package pl.ticket.event.admin.integrity;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.ticket.event.admin.event.data_provider.AdminEventRegularCreationDtoProvider;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event.service.AdminEventService;
import pl.ticket.event.admin.event_occurrence.repository.AdminEventOccurrenceRepository;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class AdminEventIntegrityTest extends PrePost
{

    @Autowired
    private AdminEventService adminEventService;

    @Autowired
    private AdminEventRepository adminEventRepository;

    @Autowired
    private AdminEventOccurrenceRepository eventOccurrenceRepository;

    @Autowired
    private AdminTicketRepository adminTicketRepository;

    private static final AdminEventRegularCreationDtoProvider adminEventRegularCreationDtoProvider = new AdminEventRegularCreationDtoProvider();

    private static Stream<Arguments> provideAdminEventRegularCreationDtos()
    {

        return Stream.of(
                Arguments.of(
                        adminEventRegularCreationDtoProvider.correct(),
                        6,
                        12,
                        1
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideAdminEventRegularCreationDtos")
    public void shouldCreateEventWithOccurrencesAndTickets(AdminEventRegularCreationDto dto, long expectedOccurrencesCount, long expectedTicketsCount, long expectedEventCount)
    {

        long countedEventsBefore = adminEventRepository.count();
        long countedOccurrencesBefore = eventOccurrenceRepository.count();
        long countedTicketsBefore = adminTicketRepository.count();


        adminEventService.createEventRegular2(dto);


        long countedEventsAfter = adminEventRepository.count();
        long countedOccurrencesAfter = eventOccurrenceRepository.count();
        long countedTicketsAfter = adminTicketRepository.count();

        assertEquals(expectedEventCount, countedEventsAfter - countedEventsBefore, "Unexpected event count after creation");
        assertEquals(expectedOccurrencesCount, countedOccurrencesAfter - countedOccurrencesBefore, "Unexpected occurrences count after creation");
        assertEquals(expectedTicketsCount, countedTicketsAfter - countedTicketsBefore, "Unexpected tickets count after creation");
    }
}
