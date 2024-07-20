package pl.ticket.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
