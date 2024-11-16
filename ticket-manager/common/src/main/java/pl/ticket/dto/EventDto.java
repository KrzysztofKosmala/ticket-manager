package pl.ticket.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EventDto
{
    private Long id;
    private Integer capacity;
    private Long categoryId;
    private String title;
    private String description;
    private String slug;
    private List<EventOccurrenceDto> occurrences;
}
