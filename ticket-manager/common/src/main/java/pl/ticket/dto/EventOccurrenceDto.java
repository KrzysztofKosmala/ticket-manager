package pl.ticket.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Builder
public class EventOccurrenceDto
{
    private Long id;
    private Long eventId;
    private LocalDate date;
    private LocalTime time;
}
