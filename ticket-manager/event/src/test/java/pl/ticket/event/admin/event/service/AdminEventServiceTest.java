package pl.ticket.event.admin.event.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event.data_provider.AdminEventRegularCreationDtoProvider;
import pl.ticket.event.admin.event.service.validation.AdminEventServiceValidator;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;
import pl.ticket.event.admin.ticket.service.AdminTicketService;
import pl.ticket.event.utils.SlugifyUtils;

import java.util.List;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminEventServiceTest
{


    private static final AdminEventRegularCreationDtoProvider adminEventRegularCreationDtoProvider = new AdminEventRegularCreationDtoProvider();

    private static Stream<Arguments> provideAdminEventRegularCreationDtos()
    {

        return Stream.of(
                Arguments.of(
                        adminEventRegularCreationDtoProvider.correct(),
                        6,
                        12
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideAdminEventRegularCreationDtos")
    void shouldPrepareEventRegular2Successfully(AdminEventRegularCreationDto dto, int expectedOccurrencesCount, int expectedTicketsCount) {

        AdminEventRepository adminEventRepository = Mockito.mock(AdminEventRepository.class);
        AdminEventOccurrenceService adminEventOccurrenceService = Mockito.mock(AdminEventOccurrenceService.class);
        AdminEventServiceValidator adminEventServiceValidator = Mockito.mock(AdminEventServiceValidator.class);
        AdminEventUtils adminEventUtils = new AdminEventUtils();
        AdminEventMapper adminEventMapper = new AdminEventMapper(new SlugifyUtils(), adminEventUtils);
        AdminTicketService adminTicketService = Mockito.mock(AdminTicketService.class);


        AdminEventService adminEventService = new AdminEventService(
                adminEventRepository,
                adminEventOccurrenceService,
                adminEventServiceValidator,
                adminEventUtils,
                adminTicketService,
                adminEventMapper
        );

        when(adminEventRepository.save(ArgumentMatchers.any(AdminEvent.class)))
                .thenAnswer((Answer<AdminEvent>) invocation -> {
                    AdminEvent event = invocation.getArgument(0);
                    event.setId(1L);
                    return event;
                });

        doNothing().when(adminEventOccurrenceService).createEventOccurrences(anyList());
        doNothing().when(adminTicketService).createTickets(anyList());

        ArgumentCaptor<List<AdminEventOccurrence>> occurrenceCaptor = ArgumentCaptor.forClass(List.class);

        adminEventService.createEventRegular2(dto);

        verify(adminEventOccurrenceService).createEventOccurrences(occurrenceCaptor.capture());
        List<AdminEventOccurrence> capturedOccurrences = occurrenceCaptor.getValue();
        assertEquals(expectedOccurrencesCount, capturedOccurrences.size(), "Liczba przechwyconych wystąpień powinna być równa 6.");
    }


}