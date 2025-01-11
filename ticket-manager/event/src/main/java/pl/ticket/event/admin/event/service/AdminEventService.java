package pl.ticket.event.admin.event.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.event.admin.event.dto.*;
import pl.ticket.event.admin.event.mapper.AdminEventMapper;
import pl.ticket.event.admin.event.service.validation.AdminEventServiceValidator;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;
import pl.ticket.event.admin.image.model.AdminImage;
import pl.ticket.event.admin.image.service.AdminImageService;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.service.AdminTicketService;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminEventService {
    private final AdminEventRepository adminEventRepository;
    private final AdminEventOccurrenceService adminEventOccurrenceService;
    private final AdminEventServiceValidator adminEventServiceValidator;
    private final AdminEventUtils adminEventUtils;
    private final AdminTicketService adminTicketService;
    private final AdminEventMapper adminEventMapper;
    private final AdminImageService imageService;

    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto)
    {
        /*TODO: dodać obrazki*/
        if (!adminEventOccasionalCreationDto.getEventType().equals(EventType.OCCASIONAL)) {
            throw new NoSuchElementException("Wrong event type!");
        }
        AdminEvent event = adminEventMapper.mapToAdminEvent(adminEventOccasionalCreationDto);
        // lista wystąpień z requestu
        List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences = adminEventOccasionalCreationDto.getEventOccurrences();

        List<AdminEventOccurrence> adminEventOccurrences = mapToAdminEventOccurrence(event, eventOccurrences, adminEventOccasionalCreationDto.getIsCommonTicketPool());
        adminEventOccurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = adminEventMapper.prepareTicketsForEachOccurrence(event,
                adminEventOccurrences,  adminEventOccasionalCreationDto);
        adminTicketService.createTickets(tickets);
    }

    private static List<AdminEventOccurrence> mapToAdminEventOccurrence(AdminEvent event,
                                                                        List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences, Boolean isCommonTicketPool) {
        return eventOccurrences.stream()
                .map(eventOccurrence -> AdminEventOccurrence.builder()
                        .date(eventOccurrence.getDate())
                        .time(eventOccurrence.getTime())
                        .isCommonPool(isCommonTicketPool)
                        .eventId(event.getId())
                        .build())
                .toList();
    }

    @Transactional
    public void createEventRegular(AdminEventRegularCreationDto adminEventRegularCreationDto)
    {
        adminEventServiceValidator.validateAdminEventRegularCreationDto(adminEventRegularCreationDto);

        AdminImage image = imageService.findById(adminEventRegularCreationDto.getImageId());


        /*TODO: Sprawdzić to bo nie wtorzą sie eventy z ostatniego dnia jakby zakres był wyłączny*/
        List<LocalDate> datesFromRange = adminEventUtils.datesFromRange(adminEventRegularCreationDto.getStartDate(), adminEventRegularCreationDto.getEndDate());

        AdminEvent event = adminEventMapper.mapToAdminEvent(adminEventRegularCreationDto);
        event.setImage(image);
        adminEventRepository.save(event);

        List<AdminEventOccurrence> adminEventOccurrences = adminEventMapper.prepareOccurrencesForRequestedRangeOfDate(adminEventRegularCreationDto, datesFromRange, event.getId());
        adminEventOccurrenceService.createEventOccurrences(adminEventOccurrences);

        List<AdminTicket> tickets = adminEventMapper.prepareTicketsForEachOccurrence(event, adminEventOccurrences,  adminEventRegularCreationDto);
        adminTicketService.createTickets(tickets);
    }

    @Transactional
    public void deleteEventById(Long id)
    {
        List<AdminEventOccurrence> eventOccurrences = adminEventOccurrenceService.findByEventId(id);

        eventOccurrences.forEach(occurrence -> adminTicketService.deleteTickets(occurrence.getTickets()));

        adminEventOccurrenceService.deleteOccurrences(eventOccurrences);

        adminEventRepository.deleteById(id);
    }

    public void updateEvent(Long id, AdminEventUpdateDto adminEventUpdateDto)
    {
        AdminEvent adminEvent = adminEventRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie ma takiego Eventu"));

        adminEvent.setTitle(adminEventUpdateDto.getTitle());
        adminEvent.setDescription(adminEventUpdateDto.getDescription());
        adminEvent.setCategoryId(adminEventUpdateDto.getCategoryId());
        adminEvent.setSlug(adminEventUpdateDto.getSlug());

        adminEventRepository.save(adminEvent);
    }
}
