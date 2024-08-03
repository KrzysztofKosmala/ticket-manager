package pl.ticket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/tickets")
public record TicketController(TicketService ticketService)
{
    @PostMapping
    public void createTicket(@RequestBody TicketCreationRequest ticketCreationRequest) {
        log.info("Ticket created {}", ticketCreationRequest);
        System.out.println("ticket!!!!!!!!!!!!!1");
        ticketService.createTicket(ticketCreationRequest);
    }
    @GetMapping("/{id}")
    public Ticket getTicket(@PathVariable Integer id)
    {
        return ticketService.getTicket(id);
    }
    @GetMapping
    public List<Ticket> getTicket()
    {
        return ticketService.getTickets();
    }

}
