package pl.ticket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.feign.event.CapacityCheckResponse;

@Slf4j
@RestController
@RequestMapping("api/v1/events")
public record EventController(EventService eventService)
{
    @PostMapping
    public void createEvent(@RequestBody EventCreationRequest eventCreationRequest)
    {
        log.info("Event created {}", eventCreationRequest);
        eventService.createEvent(eventCreationRequest);
    }

    @GetMapping("/capacity-check/{eventId}")
    public CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId)
    {
        log.info("checking capacity for event: {}", eventId);
        CapacityCheckResponse capacityCheckResponse = eventService.checkCapacity(eventId);
        return capacityCheckResponse;
    }
}
