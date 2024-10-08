package pl.ticket.event.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AdminEventDto
{
    private Long id;
    private Integer capacity;
    private Long categoryId;
    private String title;
    private String description;
    private String slug;
    private List<AdminEventOccurrenceDto> occurrences;
}
