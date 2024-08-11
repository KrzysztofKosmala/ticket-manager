package pl.ticket.event.admin.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOcurrenceService adminEventOcurrenceService;

    private final SlugifyUtils slugifyUtils;

    /**
     * {
     *   "title": "Event 1",
     *   "description": "Desc Event 1",
     *   "capacity": 200,
     *   "slug": "ev1",
     *   "categoryId": 1,
     *   "eventType": "OCCASIONAL",
     *   "eventOccurrences": [
     *     {
     *       "date": "2024-08-13",
     *       "time": "11:55",
     *       "spaceLeft": 100
     *     },
     *     {
     *       "date": "2024-10-28",
     *       "time": "14:15",
     *       "spaceLeft": 50
     *     }
     *   ]
     * }
     */
    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto) {
        //poki co na sztywno wszystko, trzeba będzie dodać tworzenie ticketow, odpowiednich occurances dodać sprawdzania testy itp
        AdminEvent event = createEventAsOccasional(adminEventOccasionalCreationDto);

        if (adminEventOccasionalCreationDto.getEventType().equals(EventType.OCCASIONAL)) {
            List<AdminEventOccurrence> occurrencesToSave = new ArrayList<>();
            // lista wystąpień
            List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences = adminEventOccasionalCreationDto.getEventOccurrences();

            for (AdminEventOccurrenceOccasionalCreationDto eventOccurrence : eventOccurrences) {
                // tworzymy konkretne egzemplarze wystąpień dla eventu
                AdminEventOccurrence adminEventOccurrence = new AdminEventOccurrence();
                adminEventOccurrence.setDate(eventOccurrence.getDate());
                adminEventOccurrence.setTime(eventOccurrence.getTime());
                adminEventOccurrence.setSpaceLeft(eventOccurrence.getSpaceLeft());
                adminEventOccurrence.setEventId(event.getId());
                occurrencesToSave.add(adminEventOccurrence);
            }
            adminEventOcurrenceService.addEventOccurrences(occurrencesToSave);
        }
    }

    private AdminEvent createEventAsOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto) {
        AdminEvent event = AdminEvent.builder()
                .title(adminEventOccasionalCreationDto.getTitle())
                .description(adminEventOccasionalCreationDto.getDescription())
                .capacity(adminEventOccasionalCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(adminEventOccasionalCreationDto.getSlug()))
                .categoryId(adminEventOccasionalCreationDto.getCategoryId())
                .build();
        return adminEventRepository.save(event);
    }

    /**
     * {
     *   "title": "qwert",
     *   "description": "qwert",
     *   "capacity": 0,
     *   "slug": "stringdsfdf",
     *   "categoryId": 1,
     *   "eventType": "REGULAR",
     *   "eventOccurrencesRegular": [
     *     {
     *       "time": "10:20",
     *       "spaceLeft": 50,
     *       "requiredNameDayOfWeek": "środa"
     *     },
     *     {
     *       "time": "11:45",
     *       "spaceLeft": 100,
     *       "requiredNameDayOfWeek": "sobota"
     *     }
     *   ]
     * }
     */
    public void createEventRegular(AdminEventRegularCreationDto adminEventRegularCreationDto, LocalDate from, LocalDate to) {

        AdminEvent event = createEventAsRegular(adminEventRegularCreationDto);

        if (adminEventRegularCreationDto.getEventType().equals(EventType.REGULAR)) {
            // pobieramy wszystkie daty z podanego przedziału
            List<LocalDate> datesFromRange = datesFromRange(from, to);

            List<AdminEventOccurrenceRegularCreationDto> occurrences = prepareOccurrencesForRequestedRangeOfDate(
                    adminEventRegularCreationDto, datesFromRange);

            // zapisujemy wyżej przygotowane wystąpienia, które wybraliśmy z przedziału dat
            for (AdminEventOccurrenceRegularCreationDto eventOccurrence : occurrences) {
                AdminEventOccurrence adminEventOccurrence = new AdminEventOccurrence();
                adminEventOccurrence.setDate(eventOccurrence.getDate());
                adminEventOccurrence.setTime(eventOccurrence.getTime());
                adminEventOccurrence.setSpaceLeft(eventOccurrence.getSpaceLeft());
                adminEventOccurrence.setEventId(event.getId());
                adminEventOcurrenceService.addEventOccurrence(adminEventOccurrence);
            }
        }
    }

    private List<AdminEventOccurrenceRegularCreationDto> prepareOccurrencesForRequestedRangeOfDate(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                                  List<LocalDate> datesFromRange) {
        List<AdminEventOccurrenceRegularCreationDto> occurrences = new ArrayList<>();

        for (AdminEventOccurrenceRegularCreationDto regularEvent : adminEventRegularCreationDto.getEventOccurrencesRegular()) {
            for (LocalDate date : datesFromRange) {
                // wyciągamy z daty dzień tygodnia w j. polskim
                String namOfDayWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pl-PL"));

                // parametr po jakim dniu tygodnia mamy tworzyć wystąpienie
                if (namOfDayWeek.equals(regularEvent.getRequiredNameDayOfWeek())) {
                    LocalDate dateToSave = LocalDate.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth());

                    // tworzymy listę wystąpień dla konkretnego czasu, dnia tygodnia wraz z pozostałymi miejscami
                    occurrences.add(new AdminEventOccurrenceRegularCreationDto(dateToSave,
                            regularEvent.getTime(),
                            regularEvent.getSpaceLeft(),
                            regularEvent.getRequiredNameDayOfWeek()));
                }
            }
        }
        return occurrences;
    }

    private AdminEvent createEventAsRegular(AdminEventRegularCreationDto adminEventRegularCreationDto) {
        AdminEvent event = AdminEvent.builder()
                .title(adminEventRegularCreationDto.getTitle())
                .description(adminEventRegularCreationDto.getDescription())
                .capacity(adminEventRegularCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(adminEventRegularCreationDto.getSlug()))
                .categoryId(adminEventRegularCreationDto.getCategoryId())
                .build();

        return adminEventRepository.save(event);
    }

    private List<LocalDate> datesFromRange(
            LocalDate startDate, LocalDate endDate) {
        return startDate.datesUntil(endDate)
                .collect(Collectors.toList());
    }
}
