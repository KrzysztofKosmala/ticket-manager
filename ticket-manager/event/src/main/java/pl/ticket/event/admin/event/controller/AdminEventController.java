package pl.ticket.event.admin.event.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.service.AdminEventService;

@Slf4j
@RestController
@RequestMapping("api/v1/admin/events")
@AllArgsConstructor
public class AdminEventController
{
    private final AdminEventService eventService;

    @PostMapping
    public void createEvent(@RequestBody @Valid AdminEventCreationDto adminEventCreationDto)
    {
        log.info("Event created {}", adminEventCreationDto);
        eventService.createEvent(adminEventCreationDto);
    }

}
