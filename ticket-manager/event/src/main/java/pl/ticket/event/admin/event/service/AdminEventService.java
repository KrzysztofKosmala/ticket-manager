package pl.ticket.event.admin.event.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.AdminEventCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOcurrenceService;
import pl.ticket.event.utils.SlugifyUtils;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Service
@AllArgsConstructor
public class AdminEventService
{
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOcurrenceService adminEventOcurrenceService;

    private final SlugifyUtils slugifyUtils;

    public void createEvent(AdminEventCreationDto adminEventCreationDto)
    {

        //poki co na sztywno wszystko, trzeba będzie dodać tworzenie ticketow, odpowiednich occurances dodać sprawdzania testy itp

        AdminEvent event = AdminEvent.builder()
                .title(adminEventCreationDto.getTitle())
                .description(adminEventCreationDto.getDescription())
                .capacity(adminEventCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(adminEventCreationDto.getSlug()))
                .build();

        adminEventRepository.save(event);

        AdminEventOccurrence adminEventOccurrence = new AdminEventOccurrence();
        adminEventOccurrence.setDate(LocalDate.now());
        adminEventOccurrence.setTime(LocalTime.now());
        adminEventOccurrence.setSpaceLeft(20);
        adminEventOccurrence.setEventId(event.getId());


        adminEventOcurrenceService.addEventOccurrence(adminEventOccurrence);
    }
}
