package pl.ticket.event.admin.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminTicketService
{
    private final AdminTicketRepository adminTicketRepository;
    private final AdminConcreteTicketService adminConcreteTicketService;

    public void createTickets(List<AdminTicket> tickets)
    {
        adminTicketRepository.saveAll(tickets);
    }

    public void deleteTickets(AdminEventOccurrence occurrence)
    {
        //todo: jesli tickety są już w zamówieniach trzeba tutaj dodać jakąś logike

        occurrence.getTickets().forEach(ticket ->
        {
            adminConcreteTicketService.processDeletingConcreteTickets(occurrence.getDate(), ticket);
        });

        adminTicketRepository.deleteAll(occurrence.getTickets());
    }
}
