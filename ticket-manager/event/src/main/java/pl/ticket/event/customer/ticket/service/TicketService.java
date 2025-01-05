package pl.ticket.event.customer.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.model.dto.TicketDto;
import pl.ticket.event.customer.ticket.repository.TicketRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TicketService
{
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public List<TicketDto> getTicketsForEventOccurrence(Long id)
    {
        List<Ticket> ticketsByEventOccurrenceId = ticketRepository.findTicketsByEventOccurrenceId(id);

        return ticketsByEventOccurrenceId.stream().map(ticketMapper::mapToTicketDto).toList();
    }
}
