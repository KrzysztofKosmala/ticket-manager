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


        List<AdminTicket> ticketsByEventIdAndFutureDate = adminTicketRepository.findTicketsByEventIdTypeAndFutureDate(eventId, nowDate, nowTime, adminTicketUpdateDto.type());

        ticketsByEventIdAndFutureDate.forEach(adminTicket ->
        {
            if (!adminTicket.getPrice().equals(adminTicketUpdateDto.price())) {
                adminTicket.setOldPrice(adminTicket.getPrice());
                adminTicket.setPrice(adminTicketUpdateDto.price());
            }
        });


        //jesli wystąpi jakis blad to obslużyć i zwrocic wyjatek

    }
}
