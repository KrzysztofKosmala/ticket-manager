package pl.ticket.event.customer.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pl.ticket.dto.EventDto;
import pl.ticket.event.customer.event.service.EventService;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("api/v1/events")
public record EventController(EventService eventService)
{

    /*TODO: Return with image*/

    @GetMapping
    public Page<EventDto> getEvents(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "5") int size){
        log.info("Getting all events");
        return eventService.getEventsWithoutOccurrences(PageRequest.of(page,size));
    }

    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable("eventId") Long eventId)
    {
        return eventService.getEventDetailsById(eventId);
    }


    @GetMapping("/date/{date}")
    public Page<EventDto> getEventsByDate(@PathVariable LocalDate date, @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size) {
        log.info("Getting events for date: {}", date);
        return eventService.getEventsWithOccurrencesOnDate(date,PageRequest.of(page ,size));
    }


    @GetMapping("/{eventId}/{date}")
    public EventDto getEvent(@PathVariable Long eventId, @PathVariable LocalDate date) {
        log.info("Getting event for Id: {] date: {}", eventId, date);

        return eventService.getEventDetailsByIdAndDate(eventId, date);
    }

    @GetMapping("/capacity-check/{eventId}")
    public CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId)
    {
        log.info("checking capacity for event: {}", eventId);
        return eventService.checkCapacity(eventId);
    }


}
