package pl.ticket.event.customer.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.common.dto.EventDto;
import pl.ticket.event.customer.event.service.EventService;
import pl.ticket.feign.event.CapacityCheckResponse;

@Slf4j
@RestController
@RequestMapping("api/v1/events")
public record EventController(EventService eventService)
{


    @GetMapping("/capacity-check/{eventId}")
    public CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId)
    {
        log.info("checking capacity for event: {}", eventId);
        CapacityCheckResponse capacityCheckResponse = eventService.checkCapacity(eventId);
        return capacityCheckResponse;
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable("eventId") Long eventId)
    {

        EventDto eventById = eventService.getEventById(eventId);
        return eventById;
    }
}
