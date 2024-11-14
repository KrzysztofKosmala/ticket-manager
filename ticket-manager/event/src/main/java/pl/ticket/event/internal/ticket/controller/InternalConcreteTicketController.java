package pl.ticket.event.internal.ticket.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.dto.TicketWithDetailsDto;
import pl.ticket.event.internal.ticket.service.InternalConcreteTicketService;
import pl.ticket.event.internal.ticket.service.InternalTicketService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/internal/concretetickets")
public record InternalConcreteTicketController(InternalConcreteTicketService internalConcreteTicketService) {

    @PostMapping
    public List<ConcreteTicketDto> createConcreteTicketsThatWereBought(@RequestBody List<OrderRowDto> orderRows){
        return internalConcreteTicketService.createConcreteTickets(orderRows);
    }

}
