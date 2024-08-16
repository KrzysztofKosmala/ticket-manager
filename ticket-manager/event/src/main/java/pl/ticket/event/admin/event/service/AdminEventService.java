package pl.ticket.event.admin.event.service;

import lombok.AllArgsConstructor;
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

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOcurrenceService adminEventOcurrenceService;

    private final SlugifyUtils slugifyUtils;
    private final AdminTicketService adminTicketService;

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

            AdminEvent event = createEvent(adminEventOccasionalCreationDto);
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
            AdminEvent event = createEvent(adminEventRegularCreationDto);
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

    public void createEventRegular2(AdminEventRegularCreationDto adminEventRegularCreationDto) {
        if (!adminEventRegularCreationDto.getEventType().equals(EventType.REGULAR))
            throw new InvalidRequestedDataException("Zły typ eventu!");

        List<LocalDate> datesFromRange = datesFromRange(adminEventRegularCreationDto.getStartDate(),
                adminEventRegularCreationDto.getEndDate());

        //TODO: 365 z pliku ma sie zaczytywać
        if(datesFromRange.size() > 365)
            throw new InvalidRequestedDataException("Maksymalnie można stwożyć eventy na rok w przód.");


        AdminEvent event = createEvent(adminEventRegularCreationDto);

        List<AdminEventOccurrence> adminEventOccurrences = prepareOccurrencesForRequestedRangeOfDate2(adminEventRegularCreationDto, datesFromRange, event.getId());
        List<AdminEventOccurrence> eventOccurrences = adminEventOcurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = prepareTicketsForEachOccurrence(event, eventOccurrences,  adminEventRegularCreationDto);

        adminTicketService.createTickets(tickets);

    }

    private List<AdminEventOccurrence> prepareOccurrencesForRequestedRangeOfDate2(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                                                  List<LocalDate> datesFromRange, Long eventId) {

        return adminEventRegularCreationDto.getOccurrences().stream()
                .flatMap(regularEvent -> datesFromRange.stream()
                        .filter(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"))
                                .equals(regularEvent.getDay()))
                        .map(date -> AdminEventOccurrence.builder()
                                .eventId(eventId)
                                .date(date)
                                .time(regularEvent.getTime())
                                .spaceLeft(adminEventRegularCreationDto.getCapacity())
                                .build()))
                .toList();
    }

    private AdminEvent createEvent(AdminEventCreationDto eventCreationDto) {
        AdminEvent event = AdminEvent.builder()
                .title(eventCreationDto.getTitle())
                .description(eventCreationDto.getDescription())
                .capacity(eventCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(eventCreationDto.getSlug()))
                .categoryId(eventCreationDto.getCategoryId())
                .build();

        return adminEventRepository.save(event);
    }

    private List<AdminTicket> prepareTicketsForEachOccurrence(AdminEvent event,  List<AdminEventOccurrence> eventOccurrences, AdminEventCreationDto adminEventCreationDto)
    {
       return eventOccurrences.stream()
                .flatMap(occurrence -> adminEventCreationDto.getTickets().stream()
                        .map(ticketDto ->
                                {
                                    if (adminEventCreationDto.getIsCommonTicketPool()) {
                                        return AdminTicket.builder()
                                                .event(event)
                                                .eventOccurrence(occurrence)
                                                .type(ticketDto.type())
                                                .price(ticketDto.price())
                                                .amount(adminEventCreationDto.getCapacity())
                                                .build();
                                    }else{
                                        return AdminTicket.builder()
                                                .event(event)
                                                .eventOccurrence(occurrence)
                                                .type(ticketDto.type())
                                                .price(ticketDto.price())
                                                .amount(ticketDto.amount())
                                                .build();
                                    }
                                }
                        )
                ).toList();
    }

    private List<LocalDate> datesFromRange(
            LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate)
                .collect(Collectors.toList());
    }
}
