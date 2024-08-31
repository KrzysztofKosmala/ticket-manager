package pl.ticket.event.customer.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.event.customer.ticket.model.dto.TicketDto;
import pl.ticket.event.customer.ticket.service.TicketService;

@Slf4j
@RestController
@RequestMapping("api/v1/tickets")
public record TicketController(TicketService ticketService) {

    @GetMapping({"/{eventId}"})
    public TicketDto getTicketsForEvent(@PathVariable Long eventId){
        log.info("Getting tickets for eventId: {} with occurrences", eventId);
        return ticketService.getTicketsForEvent(eventId);
    }

}
