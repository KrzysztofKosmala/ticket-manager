package pl.ticket.event.admin.event.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.*;
import pl.ticket.event.admin.event.mapper.AdminEventMapper;
import pl.ticket.event.admin.event.service.validation.AdminEventServiceValidator;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.service.AdminTicketService;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOccurrenceService adminEventOccurrenceService;
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

            List<AdminEventOccurrence> adminEventOccurrences = mapToAdminEventOccurrence(event, eventOccurrences, adminEventOccasionalCreationDto.getIsCommonTicketPool());
            adminEventOccurrenceService.createEventOccurrences(adminEventOccurrences);

            List<AdminTicket> tickets = adminEventMapper.prepareTicketsForEachOccurrence(event,
                    adminEventOccurrences,  adminEventOccasionalCreationDto);
            adminTicketService.createTickets(tickets);
        } else {
            throw new NoSuchElementException("Wrong event type!");
        }
    }

    private static List<AdminEventOccurrence> mapToAdminEventOccurrence(AdminEvent event,
                                                                        List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences, Boolean isCommonTicketPool) {
        return eventOccurrences.stream()
                .map(eventOccurrence -> AdminEventOccurrence.builder()
                        .date(eventOccurrence.getDate())
                        .time(eventOccurrence.getTime())
                        .isCommonPool(isCommonTicketPool)
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
                            .isCommonPool(adminEventRegularCreationDto.getIsCommonTicketPool())
                            .build());
                }
            }
            adminEventOccurrenceService.createEventOccurrences(occurrences);
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
        adminEventOccurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = adminEventMapper.prepareTicketsForEachOccurrence(event, adminEventOccurrences,  adminEventRegularCreationDto);
        adminTicketService.createTickets(tickets);
    }

    @Transactional
    public void deleteEventById(Long id)
    {
        List<AdminEventOccurrence> eventOccurrences = adminEventOccurrenceService.findByEventId(id);

        eventOccurrences.forEach(occurrence -> adminTicketService.deleteTickets(occurrence.getTickets()));

        adminEventOccurrenceService.deleteOccurrences(eventOccurrences);

        adminEventRepository.deleteById(id);
    }

    public void updateEvent(Long id, AdminEventUpdateDto adminEventUpdateDto)
    {
        AdminEvent adminEvent = adminEventRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie ma takiego Eventu"));

        adminEvent.setTitle(adminEventUpdateDto.getTitle());
        adminEvent.setDescription(adminEventUpdateDto.getDescription());
        adminEvent.setCategoryId(adminEventUpdateDto.getCategoryId());
        adminEvent.setSlug(adminEventUpdateDto.getSlug());

        adminEventRepository.save(adminEvent);
    }
}
