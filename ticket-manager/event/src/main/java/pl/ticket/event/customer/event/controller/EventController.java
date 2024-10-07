package pl.ticket.event.customer.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.common.dto.AdminEventDto;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.model.dto.EventDateTimeDto;
import pl.ticket.event.customer.event.service.EventService;
import pl.ticket.event.customer.event.model.dto.EventTicketDto;
import pl.ticket.feign.event.CapacityCheckResponse;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/events")
public record EventController(EventService eventService)
{
    @GetMapping
    public Page<Event> getEvents(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size){
        log.info("Getting all events");
        return eventService.getEvents(PageRequest.of(page,size));
    }

    @GetMapping("/{eventId}")
    public AdminEventDto getEventById(@PathVariable("eventId") Long eventId)
    {
        return eventService.getEventById(eventId);
    }

    @GetMapping("/date/{date}")
    public Page<EventDateTimeDto> getEventsByDate(@PathVariable LocalDate date, @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "5") int size) {
        log.info("Getting events for date: {}", date);
        return eventService.getEventsByDate(date,PageRequest.of(page ,size));
    }

    // kiedy wchodzimy w konkretny event i wybrany dzien -> dostajemy opis, date i godziny wystapien danego wydarzenia
    @GetMapping("/{eventId}/{date}")
    public EventDateTimeDto getEvent(@PathVariable Long eventId, @PathVariable LocalDate date) {
        log.info("Getting event for Id: {] date: {}", eventId, date);
        //TODO: może to zwróćmy tylko jak jest jakikolwiek bilet
        return eventService.getEventByIdAndDate(eventId, date);
    }

    // kiedy jestśmy w konkretnym evencie, wybieramy godzine(nasz occurrence) i pokazujemy bilety
    @GetMapping("/{eventId}/{date}/{time}")
    public List<EventTicketDto> getEventOccurrenceByDateAndTime(@PathVariable Long eventId,
                                                                @PathVariable String time,
                                                                @PathVariable LocalDate date) {
        log.info("Getting event for Id: {] time: {}", eventId, time);
        //TODO: może to zwróćmy tylko jak jest jakikolwiek bilet
        return eventService.getEventOccurrenceByDateAndTime(eventId, time, date);
    }

    @GetMapping("/capacity-check/{eventId}")
    public CapacityCheckResponse capacityCheck(@PathVariable("eventId") Integer eventId)
    {
        log.info("checking capacity for event: {}", eventId);
        return eventService.checkCapacity(eventId);
    }

    //po slug jak w niego wejdziesz to zeby bylo juz wiecej info jak wszystkie dostepne godziny tego dnia i rodzaje biletow i ceny

}
