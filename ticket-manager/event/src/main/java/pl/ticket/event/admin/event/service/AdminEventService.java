package pl.ticket.event.admin.event.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EventDto;
import pl.ticket.event.admin.event.dto.*;
import pl.ticket.event.admin.event.mapper.AdminEventMapper;
import pl.ticket.event.admin.event.service.validation.AdminEventServiceValidator;
import pl.ticket.event.admin.event.utils.AdminEventUtils;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.event.repository.AdminEventRepository;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;
import pl.ticket.event.admin.event_occurrence.service.AdminEventOccurrenceService;
import pl.ticket.event.admin.image.model.AdminImage;
import pl.ticket.event.admin.image.service.AdminImageService;
import pl.ticket.event.admin.ticket.dto.AdminTicketInitUpdateDto;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.model.AdminTicketType;
import pl.ticket.event.admin.ticket.service.AdminTicketService;
import pl.ticket.event.common.mapper.EventMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final EventMapper eventMapper;

    public void createEventOccasional(AdminEventOccasionalCreationDto adminEventOccasionalCreationDto)
    {
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

        AdminImage image = imageService.findById(adminEventRegularCreationDto.getImageId()).orElseThrow(() -> new NotFoundException("Nie ma takiego Obrazu"));

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

        eventOccurrences.forEach(adminTicketService::deleteTickets);

        adminEventOccurrenceService.deleteOccurrences(eventOccurrences);

        adminEventRepository.deleteById(id);
    }

    @Transactional
    public void updateEvent(Long id, AdminEventUpdateDto adminEventUpdateDto)
    {
        AdminEvent adminEvent = adminEventRepository.findById(id).orElseThrow(() -> new NotFoundException("Nie ma takiego Eventu"));

        adminEvent.setTitle(adminEventUpdateDto.getTitle());
        adminEvent.setDescription(adminEventUpdateDto.getDescription());
        adminEvent.setCategoryId(adminEventUpdateDto.getCategoryId());
        adminEvent.setSlug(adminEventUpdateDto.getSlug());
        adminEvent.setDiscountTag(adminEventUpdateDto.getDiscountTag());

        adminEventUpdateDto.getTickets().forEach(ticket -> {adminTicketService.updateTicketsByEventId(id, ticket);});

        if(adminEventUpdateDto.getImageId() != null
                && !adminEventUpdateDto.getImageId().equals(adminEvent.getImage().getId())) {
            Optional<AdminImage> byId = imageService.findById(adminEventUpdateDto.getImageId());
            byId.ifPresent(adminEvent::setImage);
        }

        adminEventRepository.save(adminEvent);
    }

    public AdminEventInitUpdateDto getEventForUpdate(Long id)
    {
        AdminEvent event = adminEventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        List<AdminTicketUpdateDto> tickets = adminTicketService.findTicketsByEventId(id);

        Map<AdminTicketType, List<BigDecimal>> ticketPrices = tickets.stream()
                .collect(Collectors.groupingBy(
                        AdminTicketUpdateDto::type,
                        Collectors.mapping(AdminTicketUpdateDto::price,
                                Collectors.collectingAndThen(Collectors.toSet(),
                                        set -> set.stream()
                                                .sorted(Comparator.reverseOrder())
                                                .toList()))
                ));
        List<AdminTicketInitUpdateDto> list = ticketPrices.entrySet().stream()
                .map(entry -> new AdminTicketInitUpdateDto(entry.getKey(), entry.getValue()))
                .toList();

        return AdminEventInitUpdateDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .image(event.getImage().getThumbImage())
                .slug(event.getSlug())
                .tickets(list)
                .categoryId(event.getCategoryId())
                .capacity(event.getCapacity())
                .build();

    }


    public Page<EventDto> getEventsWithoutOccurrences(Pageable pageable) {
        List<AdminEvent> allPaged = adminEventRepository.findAllPaged(pageable);
        List<EventDto> pagedEventsDto = allPaged.stream().map(eventMapper::mapToEventDtoWithoutOccurrences).collect(Collectors.toList());
        return new PageImpl<>(pagedEventsDto, pageable, pagedEventsDto.size());
    }

    @Transactional
    public void updateDiscountTagForEvents(List<Long> eventIds, String discountTag) {
        if (eventIds.isEmpty()) {
            throw new IllegalArgumentException("Lista eventów nie może być pusta");
        }

        adminEventRepository.updateDiscountTagForEvents(eventIds, discountTag);
    }
}
