package pl.ticket.event.admin.event.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOcurrenceService;
import pl.ticket.event.admin.ticket.service.AdminTicketService;
import pl.ticket.event.utils.SlugifyUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminEventServiceTest
{

    private final Clock fixedClock = Clock.fixed(Instant.parse("2024-08-15T00:00:00Z"), ZoneId.of("UTC"));
    private static final TestDataProvider testDataProvider = new TestDataProvider();

    private static Stream<Arguments> provideAdminEventRegularCreationDtos()
    {

        return Stream.of(
                Arguments.of(
                        testDataProvider.createAdminEventRegularCreationDtoCorrect(),
                        6,
                        12
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideAdminEventRegularCreationDtos")
    void shouldPrepareEventRegular2Successfully(AdminEventRegularCreationDto dto, int expectedOccurrencesCount, int expectedTicketsCount) {

        AdminEventRepository adminEventRepository = Mockito.mock(AdminEventRepository.class);
        AdminEventOcurrenceService adminEventOcurrenceService = Mockito.mock(AdminEventOcurrenceService.class);
        SlugifyUtils slugifyUtils = new SlugifyUtils();
        AdminTicketService adminTicketService = Mockito.mock(AdminTicketService.class);


        AdminEventService adminEventService = new AdminEventService(
                adminEventRepository,
                adminEventOcurrenceService,
                slugifyUtils,
                adminTicketService,
                fixedClock
        );

        when(adminEventRepository.save(ArgumentMatchers.any(AdminEvent.class)))
                .thenAnswer((Answer<AdminEvent>) invocation -> {
                    AdminEvent event = invocation.getArgument(0);
                    event.setId(1L);
                    return event;
                });

        doNothing().when(adminEventOcurrenceService).createEventOccurrences(anyList());
        doNothing().when(adminTicketService).createTickets(anyList());

        ArgumentCaptor<List<AdminEventOccurrence>> occurrenceCaptor = ArgumentCaptor.forClass(List.class);

        adminEventService.createEventRegular2(dto);

        verify(adminEventOcurrenceService).createEventOccurrences(occurrenceCaptor.capture());
        List<AdminEventOccurrence> capturedOccurrences = occurrenceCaptor.getValue();
        assertEquals(expectedOccurrencesCount, capturedOccurrences.size(), "Liczba przechwyconych wystąpień powinna być równa 6.");
    }


}