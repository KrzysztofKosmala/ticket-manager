package pl.ticket.event.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Builder
public class AdminEventOccurrenceDto
{
    private Long id;
    private Long eventId;
    private LocalDate date;
    private LocalTime time;
}
