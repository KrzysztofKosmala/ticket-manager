package pl.ticket.event.common.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.ticket.dto.EventDto;
import pl.ticket.dto.EventOccurrenceDto;
import pl.ticket.dto.ImageDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.admin.image.model.AdminImage;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EventMapper {


    public EventDto mapToEventDto(Event event)
    {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .capacity(event.getCapacity())
                .slug(event.getSlug())
                .categoryId(event.getCategoryId())
                .occurrences(event.getOccurrences().stream().map(this::mapToEventOccurrenceDto).collect(Collectors.toList()))
                .imageDto(mapToImageDto(event.getImage()))
                .build();
    }

    public EventOccurrenceDto mapToEventOccurrenceDto(EventOccurrence eventOccurrence)
    {
        return EventOccurrenceDto.builder()
                .date(eventOccurrence.getDate())
                .time(eventOccurrence.getTime())
                .eventId(eventOccurrence.getEventId())
                .id(eventOccurrence.getId())
                .build();
    }

    public EventDto mapToEventDtoWithoutOccurrences(Event event)
    {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .capacity(event.getCapacity())
                .slug(event.getSlug())
                .categoryId(event.getCategoryId())
                .imageDto(mapToImageDto(event.getImage()))
                .build();
    }

    public EventDto mapToEventDtoWithoutOccurrences(AdminEvent event)
    {
        return EventDto.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .capacity(event.getCapacity())
                .slug(event.getSlug())
                .imageDto(mapToImageDto(event.getImage()))
                .categoryId(event.getCategoryId())
                .build();
    }

    private ImageDto mapToImageDto(AdminImage image)
    {
        return ImageDto.builder()
                .name(image.getName())
                .thumbImage(image.getThumbImage())
                .id(image.getId())
                .build();
    }
}
