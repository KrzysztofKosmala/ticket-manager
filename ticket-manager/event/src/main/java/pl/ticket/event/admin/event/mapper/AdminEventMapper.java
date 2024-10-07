package pl.ticket.event.admin.event.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.common.dto.AdminTicketCreationDto;
import pl.ticket.event.utils.SlugifyUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AdminEventMapper
{

    private final SlugifyUtils slugifyUtils;
    private final AdminEventUtils adminEventUtils;

    public AdminEvent mapToAdminEvent(AdminEventCreationDto eventCreationDto)
    {
        AdminEvent event = AdminEvent.builder()
                .title(eventCreationDto.getTitle())
                .description(eventCreationDto.getDescription())
                .capacity(eventCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(eventCreationDto.getSlug()))
                .categoryId(eventCreationDto.getCategoryId())
                .build();

        return event;
    }
    public List<AdminEventOccurrence> prepareOccurrencesForRequestedRangeOfDate(AdminEventRegularCreationDto adminEventRegularCreationDto,
                                                                                  List<LocalDate> datesFromRange, Long eventId)
    {

        return adminEventRegularCreationDto.getOccurrences()
                .stream()
                .flatMap(regularEvent -> createOccurrencesForRegularEvent(regularEvent, datesFromRange, eventId, adminEventRegularCreationDto.getIsCommonTicketPool()).stream())
                .toList();
    }

    public List<AdminTicket> prepareTicketsForEachOccurrence(AdminEvent adminEvent, List<AdminEventOccurrence> adminEventOccurrences, AdminEventCreationDto adminEventCreationDto)
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
    private List<AdminEventOccurrence> createOccurrencesForRegularEvent(AdminEventOccurrenceRegularCreationDto regularEvent,
                                                                        List<LocalDate> datesFromRange,
                                                                        Long eventId,
                                                                        Boolean isCommonTicketPool) {
        return datesFromRange.stream()
                .filter(date -> adminEventUtils.isMatchingDayOfWeek(date, regularEvent.getDay()))
                .map(date -> createOccurrence(eventId, date, regularEvent.getTime(), isCommonTicketPool))
                .toList();
    }



    private AdminEventOccurrence createOccurrence(Long eventId, LocalDate date, LocalTime time, Boolean isCommonTicketPool) {
        return AdminEventOccurrence.builder()
                .eventId(eventId)
                .date(date)
                .time(time)
                .isCommonPool(isCommonTicketPool)
                .build();
    }
}
