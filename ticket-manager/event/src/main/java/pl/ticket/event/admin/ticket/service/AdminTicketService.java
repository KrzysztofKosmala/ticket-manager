package pl.ticket.event.admin.ticket.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminTicketService
{
    private final Clock clock;
    private final AdminTicketRepository adminTicketRepository;
    private final AdminConcreteTicketService adminConcreteTicketService;

    public void createTickets(List<AdminTicket> tickets)
    {
        adminTicketRepository.saveAll(tickets);
    }

    public void deleteTickets(AdminEventOccurrence occurrence)
    {

        occurrence.getTickets().forEach(ticket ->
        {
            adminConcreteTicketService.processDeletingConcreteTickets(occurrence.getDate(), ticket);
        });

        adminTicketRepository.deleteAll(occurrence.getTickets());
    }

    public void updateTicketsByEventId(Long eventId, AdminTicketUpdateDto adminTicketUpdateDto)
    {
        LocalDate nowDate = LocalDate.now(clock);
        LocalTime nowTime = LocalTime.now(clock);


        int updatedRows = adminTicketRepository.updateTicketsByEventId(
                eventId,
                adminTicketUpdateDto.type(),
                adminTicketUpdateDto.price(),
                nowDate,
                nowTime
        );

        log.trace("updateTicketsByEventId updatedRows: {}", updatedRows);

    }

    public List<AdminTicketUpdateDto> findTicketsByEventId(Long eventId)
    {
        return adminTicketRepository.findTicketsByEventId(eventId);
    }
}
