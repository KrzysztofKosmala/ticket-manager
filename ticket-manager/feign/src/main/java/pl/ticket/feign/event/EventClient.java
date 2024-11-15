package pl.ticket.feign.event;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.dto.TicketWithDetailsDto;

import java.util.List;

@FeignClient("event")
public interface EventClient
{
    @GetMapping("api/v1/events/capacity-check/{eventId}")
    CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId);

    @GetMapping("api/v1/internal/tickets")
    List<TicketWithDetailsDto> getTicketsWithDetailsByTicketIds(@RequestParam("ticketIds") List<Long> ticketIds);

}
