package pl.ticket.event.admin.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.dto.AdminEventCreationDto;
import pl.ticket.event.admin.model.AdminEvent;
import pl.ticket.event.admin.repository.AdminEventRepository;
import pl.ticket.event.utils.SlugifyUtils;

@Service
@AllArgsConstructor
public class AdminEventService
{
    private final AdminEventRepository adminEventRepository;

    private final SlugifyUtils slugifyUtils;

    public void createEvent(AdminEventCreationDto adminEventCreationDto)
    {

        //

        AdminEvent event = AdminEvent.builder()
                .title(adminEventCreationDto.getTitle())
                .description(adminEventCreationDto.getDescription())
                .capacity(adminEventCreationDto.getCapacity())
                .slug(slugifyUtils.slugifySlug(adminEventCreationDto.getSlug()))
                .build();

        adminEventRepository.save(event);
    }
}
