package pl.ticket.event.admin.event_occurrence.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceUpdateDto;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;

@Slf4j
@RestController
@RequestMapping("api/v1/admin/occurrences")
@AllArgsConstructor
public class AdminEventOccurrenceController
{

    private final AdminEventOccurrenceService adminEventOccurrenceService;

    @DeleteMapping
    public void deleteOccurrenceById(@RequestParam("id") Long id)
    {
        adminEventOccurrenceService.deleteOccurrenceWithTickets(id);
    }

    @PutMapping("{id}")
    public void updateEventOccurrence(@RequestBody @Valid AdminEventOccurrenceUpdateDto adminEventOccurrenceUpdateDto
            , @PathVariable("id") Long id)
    {
        adminEventOccurrenceService.updateEventOccurrence(adminEventOccurrenceUpdateDto, id);
    }
}
