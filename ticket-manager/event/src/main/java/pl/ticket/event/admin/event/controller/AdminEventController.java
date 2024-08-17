package pl.ticket.event.admin.event.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.admin.event.dto.AdminEventOccasionalCreationDto;
import pl.ticket.event.admin.event.dto.AdminEventRegularCreationDto;
import pl.ticket.event.admin.event.service.AdminEventService;
@Slf4j
@RestController
@RequestMapping("api/v1/admin/events")
@AllArgsConstructor
public class AdminEventController
{
    private final AdminEventService eventService;

    @PostMapping("/occasional")
    public void createEvent(@RequestBody @Valid AdminEventOccasionalCreationDto adminEventOccasionalCreationDto)
    {
        log.info("Event created {}", adminEventOccasionalCreationDto);
        eventService.createEventOccasional(adminEventOccasionalCreationDto);
    }
    @PostMapping("/regular")
    public void createEventWithRange(@RequestBody @Valid AdminEventRegularCreationDto adminEventRegularCreationDto)
    {
        log.info("Event created {}", adminEventRegularCreationDto);
        eventService.createEventRegular(adminEventRegularCreationDto);
    }

    @PostMapping("/regular2")
    public void createEventWithRange2(@RequestBody @Valid AdminEventRegularCreationDto adminEventRegularCreationDto)
    {
        log.info("Event created {}", adminEventRegularCreationDto);
        eventService.createEventRegular2(adminEventRegularCreationDto);
    }
}
