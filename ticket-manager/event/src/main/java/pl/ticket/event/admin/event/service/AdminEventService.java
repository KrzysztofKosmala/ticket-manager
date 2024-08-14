package pl.ticket.event.admin.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.EventType;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOcurrenceService;
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
    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto) {
        //poki co na sztywno wszystko, trzeba będzie dodać tworzenie ticketow, odpowiednich occurances dodać sprawdzania testy itp

        if (adminEventOccasionalCreationDto.getEventType().equals(EventType.OCCASIONAL)) {

            AdminEvent event = createEvent(adminEventOccasionalCreationDto);
            // lista wystąpień z requestu
            List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences = adminEventOccasionalCreationDto.getEventOccurrences();

            adminEventOcurrenceService.addEventOccurrences(mapToAdminEventOccurrence(event, eventOccurrences));
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
                        .spaceLeft(eventOccurrence.getSpaceLeft())
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
            List<LocalDate> datesFromRange = datesFromRange(adminEventRegularCreationDto.getFrom(),
                    adminEventRegularCreationDto.getTo());

            prepareOccurrencesForRequestedRangeOfDate(adminEventRegularCreationDto, datesFromRange, event.getId());
        } else {
            throw new NoSuchElementException("Wrong event type!");
        }
    }

    private void prepareOccurrencesForRequestedRangeOfDate(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                           List<LocalDate> datesFromRange, Long eventId) {
        List<AdminEventOccurrence> occurrences = new ArrayList<>();

        // pobieramy liste naszych occurences
        for (AdminEventOccurrenceRegularCreationDto regularEvent : adminEventRegularCreationDto.getEventOccurrencesRegular()) {
            for (LocalDate date : datesFromRange) {
                // wyciągamy z daty dzień tygodnia w j. polskim np. wtorek, sobota ..
                String namOfDayWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"));
                // parametr po jakim dniu tygodnia mamy tworzyć wystąpienie - przyrownujemy z obiektem ktory otrzymalismy
                if (namOfDayWeek.equals(regularEvent.getRequestedNameDayOfWeek())) {
                    // tworzymy listę wystąpień dla konkretnego czasu, dnia tygodnia wraz z pozostałymi miejscami
                    occurrences.add(AdminEventOccurrence.builder()
                            .eventId(eventId)
                            .date(date)
                            .time(regularEvent.getTime())
                            .spaceLeft(regularEvent.getSpaceLeft())
                            .build());
                }
            }
            adminEventOcurrenceService.addEventOccurrences(occurrences);
        }
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

    private List<LocalDate> datesFromRange(
            LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate)
                .collect(Collectors.toList());
    }
}
