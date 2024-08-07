package pl.ticket.event.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventDto
{
    private Long id;
    private Integer capacity;
    private String title;
    private String description;
    private String slug;
}
