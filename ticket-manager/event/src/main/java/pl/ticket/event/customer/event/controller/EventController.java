package pl.ticket.event.customer.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.common.dto.AdminEventDto;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.service.EventService;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;

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
    public AdminEventDto getEventById(@PathVariable("eventId") Long eventId)
    {
        AdminEventDto eventById = eventService.getEventById(eventId);
        return eventById;
    }
    @GetMapping
    public Page<Event> getEvents(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size){
        log.info("Getting all events");
        return eventService.getEvents(PageRequest.of(page,size));
    }

    @GetMapping("/{date}")
    public Page<EventDateTimeDto> getEventsByDate(@PathVariable LocalDate date, @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size) {
        log.info("Getting events for date: {}", date);
        return eventService.getEventsByDate(date,PageRequest.of(page ,size));
    }

    @GetMapping("/{eventId}/{date}")
    public EventDateTimeDto getEvent(@PathVariable Long eventId, @PathVariable LocalDate date) {
        log.info("Getting event for Id: {] date: {}", eventId, date);
        return eventService.getEventByIdAndDate(eventId, date);
    }


    //po slug jak w niego wejdziesz to zeby bylo juz wiecej info jak wszystkie dostepne godziny tego dnia i rodzaje biletow i ceny
}
