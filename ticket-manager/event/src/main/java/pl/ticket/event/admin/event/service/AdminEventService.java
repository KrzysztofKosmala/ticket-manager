package pl.ticket.event.admin.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.exception.InvalidRequestedDataException;
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

    private final SlugifyUtils slugifyUtils;
    private final AdminTicketService adminTicketService;
    private final Clock clock;

    /**
     * {
     * "title": "Event 1",
     * "description": "Desc Event 1",
     * "capacity": 200,
     * "slug": "ev1",
     * "categoryId": 1,
     * "eventType": "OCCASIONAL",
     * "eventOccurrences": [
     * {
     * "date": "2024-08-13",
     * "time": "11:55",
     * "spaceLeft": 100
     * },
     * {
     * "date": "2024-10-28",
     * "time": "14:15",
     * "spaceLeft": 50
     * }
     * ]
     * }
     */
    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto)
    {
        if (adminEventOccasionalCreationDto.getEventType().equals(EventType.OCCASIONAL)) {

            AdminEvent event = prepareEvent(adminEventOccasionalCreationDto);
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

    /**
     * {
     *   "title": "Ev1",
     *   "description": "Desv Ev1",
     *   "capacity": 100,
     *   "slug": "ev1",
     *   "categoryId": 1,
     *   "eventType": "REGULAR",
     *   "from": "2024-08-14",
     *   "to": "2024-08-31",
     *   "eventOccurrencesRegular": [
     *     {
     *       "time": "12:55",
     *       "spaceLeft": 50,
     *       "requestedNameDayOfWeek": "sobota"
     *     },
     *     {
     *       "time": "18:55",
     *       "spaceLeft": 100,
     *       "requestedNameDayOfWeek": "środa"
     *     },
     *     {
     *       "time": "20:20",
     *       "spaceLeft": 50,
     *       "requestedNameDayOfWeek": "poniedziałek"
     *     }
     *   ]
     * }
     */
    public void createEventRegular(AdminEventRegularCreationDto adminEventRegularCreationDto) {
        if (adminEventRegularCreationDto.getEventType().equals(EventType.REGULAR)) {
            AdminEvent event = prepareEvent(adminEventRegularCreationDto);
            // pobieramy wszystkie daty z podanego przedziału z requestu
            // czy zakres nie jest za duży? check
            List<LocalDate> datesFromRange = datesFromRange(adminEventRegularCreationDto.getStartDate(),
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
        validateAdminEventRegularCreationDto(adminEventRegularCreationDto);

        List<LocalDate> datesFromRange = datesFromRange(adminEventRegularCreationDto.getStartDate(), adminEventRegularCreationDto.getEndDate());

        AdminEvent event = prepareEvent(adminEventRegularCreationDto);
        adminEventRepository.save(event);

        List<AdminEventOccurrence> adminEventOccurrences = prepareOccurrencesForRequestedRangeOfDate2(adminEventRegularCreationDto, datesFromRange, event.getId());
        adminEventOcurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = prepareTicketsForEachOccurrence(event, adminEventOccurrences,  adminEventRegularCreationDto);
        adminTicketService.createTickets(tickets);
    }

    private void validateAdminEventRegularCreationDto(AdminEventRegularCreationDto adminEventRegularCreationDto)
    {
        LocalDate now = LocalDate.now(clock);

        if (!adminEventRegularCreationDto.getEventType().equals(EventType.REGULAR))
            throw new InvalidRequestedDataException("Zły typ eventu!");

        if(adminEventRegularCreationDto.getStartDate().isBefore(now) || adminEventRegularCreationDto.getEndDate().isBefore(now))
            throw new InvalidRequestedDataException("Nie można stworzyć eventu w podanym zakresie czasowym.");

        if(adminEventRegularCreationDto.getEndDate().isBefore(adminEventRegularCreationDto.getStartDate()))
            throw new InvalidRequestedDataException("Podany został zły zakres czasowy.");
    }

    private AdminEvent prepareEvent(AdminEventCreationDto eventCreationDto) {
        AdminEvent event = AdminEvent.builder()
                .title(eventCreationDto.getTitle())
                .description(eventCreationDto.getDescription())
                .capacity(eventCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(eventCreationDto.getSlug()))
                .categoryId(eventCreationDto.getCategoryId())
                .build();

        return event;
    }

    private List<AdminEventOccurrence> prepareOccurrencesForRequestedRangeOfDate2(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                                                  List<LocalDate> datesFromRange, Long eventId) {
        return adminEventRegularCreationDto.getOccurrences()
                .stream()
                .flatMap(regularEvent -> createOccurrencesForRegularEvent(regularEvent, datesFromRange, eventId, adminEventRegularCreationDto.getCapacity()).stream())
                .toList();
    }

    private List<AdminEventOccurrence> createOccurrencesForRegularEvent(AdminEventOccurrenceRegularCreationDto regularEvent,
                                                                        List<LocalDate> datesFromRange,
                                                                        Long eventId,
                                                                        Integer capacity) {
        return datesFromRange.stream()
                .filter(date -> isMatchingDayOfWeek(date, regularEvent.getDay()))
                .map(date -> createOccurrence(eventId, date, regularEvent.getTime(), capacity))
                .toList();
    }

    private boolean isMatchingDayOfWeek(LocalDate date, String expectedDay) {
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"));
        return dayOfWeek.equals(expectedDay);
    }

    private AdminEventOccurrence createOccurrence(Long eventId, LocalDate date, LocalTime time, Integer capacity) {
        return AdminEventOccurrence.builder()
                .eventId(eventId)
                .date(date)
                .time(time)
                .spaceLeft(capacity)
                .build();
    }

    private List<AdminTicket> prepareTicketsForEachOccurrence(AdminEvent adminEvent,  List<AdminEventOccurrence> adminEventOccurrences, AdminEventCreationDto adminEventCreationDto)
    {

       return adminEventOccurrences.stream()
            .flatMap(occurrence -> createTicketsForOccurrence(occurrence, adminEvent, adminEventCreationDto).stream())
            .toList();
    }

    private List<AdminTicket> createTicketsForOccurrence(AdminEventOccurrence adminEventOccurrence,
                                                         AdminEvent adminEvent,
                                                         AdminEventCreationDto adminEventCreationDto) {
        return adminEventCreationDto.getTickets().stream()
                .map(ticketDto -> createTicket(adminEvent, adminEventOccurrence, ticketDto, adminEventCreationDto.getIsCommonTicketPool(), adminEventCreationDto.getCapacity()))
                .toList();
    }

    private AdminTicket createTicket(AdminEvent adminEvent,
                                     AdminEventOccurrence occurrence,
                                     AdminTicketCreationDto ticketDto,
                                     boolean isCommonTicketPool,
                                     int eventCapacity)
    {
        int ticketAmount = isCommonTicketPool ? eventCapacity : ticketDto.amount();

        return AdminTicket.builder()
                .event(adminEvent)
                .eventOccurrence(occurrence)
                .type(ticketDto.type())
                .price(ticketDto.price())
                .amount(ticketAmount)
                .build();
    }

    private List<LocalDate> datesFromRange(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> datesFromRange = startDate.datesUntil(endDate)
                .collect(Collectors.toList());

        //TODO: 365 z pliku ma sie zaczytywać
        if(datesFromRange.size() > 365) {
            throw new InvalidRequestedDataException("Maksymalnie można stwożyć eventy na rok w przód.");
        }

        return datesFromRange;
    }
}
