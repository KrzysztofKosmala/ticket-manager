package pl.ticket;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void createTicket(TicketCreationRequest ticketCreationRequest) {
        Ticket ticket = Ticket.builder()
                .eventId(ticketCreationRequest.eventId())
                .type(ticketCreationRequest.type())
                .price(ticketCreationRequest.price())
                .amount(ticketCreationRequest.amount())
                .build();
        ticketRepository.save(ticket);
    }

    public Ticket getTicket(Integer id) {
        return ticketRepository.findById(id).orElseThrow();
    }

    public List<Ticket> getTickets() {
        return ticketRepository.findAll();
    }
}
