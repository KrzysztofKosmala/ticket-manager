package pl.ticket.event.customer.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.event.customer.ticket.model.dto.TicketDto;
import pl.ticket.event.customer.ticket.service.TicketService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/tickets")
public record TicketController(TicketService ticketService) {

    @GetMapping({"/{eventOccurrenceId}"})
    public List<TicketDto> getTicketsForEventOccurrence(@PathVariable Long eventOccurrenceId){
        log.info("Getting tickets for eventOccurrenceId: {}", eventOccurrenceId);
        return ticketService.getTicketsForEventOccurrence(eventOccurrenceId);
    }

}
