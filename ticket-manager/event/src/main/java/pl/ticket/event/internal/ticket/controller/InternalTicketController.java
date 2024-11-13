package pl.ticket.event.internal.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.dto.TicketWithDetailsDto;
import pl.ticket.event.internal.ticket.service.InternalTicketService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/internal/tickets")
public record InternalTicketController(InternalTicketService ticketService) {

    @GetMapping
    public List<TicketWithDetailsDto> getTicketsForEvent(@RequestParam List<Long> ticketIds){
        return ticketService.getTicketsForIds(ticketIds);
    }

}
