package pl.ticket.event.admin.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.repository.AdminTicketRepository;
import pl.ticket.event.common.dto.AdminTicketCreationDto;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminTicketService
{
    private final AdminTicketRepository adminTicketRepository;

    public List<AdminTicket> createTickets(List<AdminTicket> tickets)
    {
        return adminTicketRepository.saveAll(tickets);
    }
}
