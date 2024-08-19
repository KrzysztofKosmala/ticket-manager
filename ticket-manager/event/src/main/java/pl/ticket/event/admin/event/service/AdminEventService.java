package pl.ticket.event.admin.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;
import pl.ticket.event.admin.event.service.validation.AdminEventServiceValidator;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.EventType;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOcurrenceService;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.service.AdminTicketService;
import pl.ticket.event.common.dto.AdminTicketCreationDto;
import pl.ticket.event.utils.SlugifyUtils;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOcurrenceService adminEventOcurrenceService;
    private final AdminEventServiceValidator adminEventServiceValidator;
    private final AdminEventUtils adminEventUtils;
    private final AdminTicketService adminTicketService;
    private final AdminEventMapper adminEventMapper;

    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto)
    {
        if (adminEventOccasionalCreationDto.getEventType().equals(EventType.OCCASIONAL)) {

            AdminEvent event = adminEventMapper.mapToAdminEvent(adminEventOccasionalCreationDto);
            // lista wystąpień z requestu
            List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences = adminEventOccasionalCreationDto.getEventOccurrences();

            adminEventOcurrenceService.createEventOccurrences(mapToAdminEventOccurrence(event, eventOccurrences));
        } else {
            throw new NoSuchElementException("Wrong event type!");
        }
    }

    private static List<AdminEventOccurrence> mapToAdminEventOccurrence(AdminEvent event,
                                                                        List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences) {
        return eventOccurrences.stream()
                .map(eventOccurrence -> AdminEventOccurrence.builder()
                        .date(eventOccurrence.getDate())
                        .time(eventOccurrence.getTime())
                        .spaceLeft(event.getCapacity())
                        .eventId(event.getId())
                        .build())
                .toList();
    }


    public void createEventRegular(AdminEventRegularCreationDto adminEventRegularCreationDto) {
        if (adminEventRegularCreationDto.getEventType().equals(EventType.REGULAR)) {
            AdminEvent event = adminEventMapper.mapToAdminEvent(adminEventRegularCreationDto);
            // pobieramy wszystkie daty z podanego przedziału z requestu
            // czy zakres nie jest za duży? check
            List<LocalDate> datesFromRange = adminEventUtils.datesFromRange(adminEventRegularCreationDto.getStartDate(),
                    adminEventRegularCreationDto.getEndDate());

            prepareOccurrencesForRequestedRangeOfDate(adminEventRegularCreationDto, datesFromRange, event.getId());
        } else {
            throw new NoSuchElementException("Wrong event type!");
        }
    }

    private void prepareOccurrencesForRequestedRangeOfDate(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                           List<LocalDate> datesFromRange, Long eventId) {
        List<AdminEventOccurrence> occurrences = new ArrayList<>();

        // pobieramy liste naszych occurences
        for (AdminEventOccurrenceRegularCreationDto regularEvent : adminEventRegularCreationDto.getOccurrences()) {
            for (LocalDate date : datesFromRange) {
                // wyciągamy z daty dzień tygodnia w j. polskim np. wtorek, sobota ..
                String namOfDayWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"));
                // parametr po jakim dniu tygodnia mamy tworzyć wystąpienie - przyrownujemy z obiektem ktory otrzymalismy
                if (namOfDayWeek.equals(regularEvent.getDay())) {
                    // tworzymy listę wystąpień dla konkretnego czasu, dnia tygodnia wraz z pozostałymi miejscami
                    occurrences.add(AdminEventOccurrence.builder()
                            .eventId(eventId)
                            .date(date)
                            .time(regularEvent.getTime())
                            .spaceLeft(adminEventRegularCreationDto.getCapacity())
                            .build());
                }
            }
            adminEventOcurrenceService.createEventOccurrences(occurrences);
        }
    }

    @Transactional
    public void createEventRegular2(AdminEventRegularCreationDto adminEventRegularCreationDto)
    {
        adminEventServiceValidator.validateAdminEventRegularCreationDto(adminEventRegularCreationDto);

        List<LocalDate> datesFromRange = adminEventUtils.datesFromRange(adminEventRegularCreationDto.getStartDate(), adminEventRegularCreationDto.getEndDate());

        AdminEvent event = adminEventMapper.mapToAdminEvent(adminEventRegularCreationDto);
        adminEventRepository.save(event);

        List<AdminEventOccurrence> adminEventOccurrences = adminEventMapper.prepareOccurrencesForRequestedRangeOfDate(adminEventRegularCreationDto, datesFromRange, event.getId());
        adminEventOcurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = adminEventMapper.prepareTicketsForEachOccurrence(event, adminEventOccurrences,  adminEventRegularCreationDto);
        adminTicketService.createTickets(tickets);
    }
}
