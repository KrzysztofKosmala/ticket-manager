package pl.ticket.event.customer.ticket.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.customer.event.exception.EventDateException;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;
import pl.ticket.event.customer.ticket.model.Ticket;
import pl.ticket.event.customer.ticket.model.dto.TicketListDto;
import pl.ticket.event.customer.ticket.model.dto.TicketDto;
import pl.ticket.event.customer.ticket.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TicketService
{
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final TicketMapper ticketMapper;

    public TicketDto getTicketsForEvent(Long id) {
        Optional<Event> optionalEvent = eventRepository.findByIdWithOccurrences(id);

        if(!optionalEvent.isPresent()){
            throw new EventDateException("Nie znaleziono wydarzenia o id: + " + id);
        }
        Event event = optionalEvent.get();
        List<Ticket> tickets = ticketRepository.findTicketsByEventId(id);

        List<TicketListDto> ticketListDtos = ticketMapper.mapToListTicketListDto(tickets);

        return ticketMapper.mapToTicketDto(event, ticketListDtos);
    }
}
