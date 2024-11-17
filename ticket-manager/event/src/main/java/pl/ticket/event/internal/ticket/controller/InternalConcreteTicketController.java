package pl.ticket.event.internal.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.internal.ticket.service.InternalConcreteTicketService;



@Slf4j
@RestController
@RequestMapping("api/v1/internal/concretetickets")
public record InternalConcreteTicketController(InternalConcreteTicketService ticketService) {

    @PostMapping("/validate")
    public boolean validateConcreteTicket(@RequestBody byte[] base64QrCode){
        return ticketService.validateConcreteTicketByQrCode(base64QrCode);
    }

}
