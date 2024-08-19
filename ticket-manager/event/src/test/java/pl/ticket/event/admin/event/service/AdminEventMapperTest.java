package pl.ticket.event.admin.event.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pl.ticket.event.admin.event.data_provider.AdminEventRegularCreationDtoProvider;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.utils.SlugifyUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
class AdminEventMapperTest
{
    private static final AdminEventRegularCreationDtoProvider adminEventRegularCreationDtoProvider = new AdminEventRegularCreationDtoProvider();

    private static Stream<Arguments> provideAdminEventOccurrenceTestData() {
        AdminEventRegularCreationDto correctDto = adminEventRegularCreationDtoProvider.correct();
        return Stream.of(
                Arguments.of(
                        correctDto, // Populate this DTO as needed
                        correctDto.getStartDate().datesUntil(correctDto.getEndDate()).collect(Collectors.toList()), // Example dates
                        1L,
                        List.of(
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,21))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(9,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,24))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(10,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,24))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(11,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,28))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(9,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,31))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(10,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,31))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(11,0))
                                        .build()
                                )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideAdminEventOccurrenceTestData")
    void shouldPrepareOccurrencesForRequestedRangeOfDate(AdminEventRegularCreationDto dto, List<LocalDate> dates, Long eventId, List<AdminEventOccurrence> expectedOccurrences) {
        // Given

        AdminEventMapper underTest = new AdminEventMapper(new SlugifyUtils(), new AdminEventUtils());

        // When
        List<AdminEventOccurrence> result = underTest.prepareOccurrencesForRequestedRangeOfDate(dto, dates, eventId);

        // Then
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator() // Rekursywne porównanie wszystkich pól obiektów
                .containsExactlyInAnyOrderElementsOf(expectedOccurrences);
    }



    //TODO: za dużo pisania trzeba wymyslic jakiś inny sposob na testowanie takiech metod
    private static Stream<Arguments> provideDataForCreateTicketForEachOccurrence() {
        AdminEventRegularCreationDto correctDto = adminEventRegularCreationDtoProvider.correct();
        AdminEventMapper underTest = new AdminEventMapper(new SlugifyUtils(), new AdminEventUtils());
        AdminEvent adminEvent = underTest.mapToAdminEvent(correctDto);
        return Stream.of(
                Arguments.of(// Populate this DTO as needed
                        correctDto, // Example dates
                        List.of(
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,21))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(9,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,24))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(10,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,24))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(11,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,28))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(9,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,31))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(10,0))
                                        .build(),
                                AdminEventOccurrence.builder()
                                        .eventId(1L)
                                        .date(LocalDate.of(2024,8,31))
                                        .spaceLeft(100)
                                        .time(LocalTime.of(11,0))
                                        .build()
                        ),
                        List.of(
                                AdminTicket.builder()
                                        .eventOccurrence(AdminEventOccurrence.builder()
                                                .eventId(1L)
                                                .date(LocalDate.of(2024,8,21))
                                                .spaceLeft(100)
                                                .time(LocalTime.of(9,0))
                                                .build())
                                        .event(adminEvent)
                                        .build()
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForCreateTicketForEachOccurrence")
    void shouldPrepareTicketsForEachOccurrence(AdminEventCreationDto eventCreationDto, List<AdminEventOccurrence> occurrences, List<AdminTicket> expectedTickets) {
        // Given
/*        AdminEventMapper underTest = new AdminEventMapper(new SlugifyUtils(), new AdminEventUtils());
        AdminEvent adminEvent = underTest.mapToAdminEvent(eventCreationDto);

        // When
        List<AdminTicket> result = underTest.prepareTicketsForEachOccurrence(adminEvent, occurrences, eventCreationDto);

        // Then
        assertThat(result)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrderElementsOf(expectedTickets);*/
    }
}