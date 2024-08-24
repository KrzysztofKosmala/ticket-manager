package pl.ticket.event.admin.event_occurrence.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;

@Slf4j
@RestController
@RequestMapping("api/v1/admin/occurrences")
@AllArgsConstructor
public class adminEventOccurrenceController
{

    private final AdminEventOccurrenceService adminEventOccurrenceService;

    @DeleteMapping
    public void deleteOccurrenceById(@RequestParam("id") Long id)
    {
        adminEventOccurrenceService.deleteOccurrenceWithTickets(id);
    }
}
