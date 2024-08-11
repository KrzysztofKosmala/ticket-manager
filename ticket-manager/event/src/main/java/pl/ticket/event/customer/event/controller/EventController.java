package pl.ticket.event.customer.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.common.dto.EventDto;
import pl.ticket.event.customer.event.model.Event;
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

    //po dacie zeby pozyskiwal event z godzinami

    //po slug jak w niego wejdziesz to zeby bylo juz wiecej info jak wszystkie dostepne godziny tego dnia i rodzaje biletow i ceny
}
