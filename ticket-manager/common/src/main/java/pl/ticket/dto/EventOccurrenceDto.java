package pl.ticket.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EventOccurrenceDto
{
    private Long id;
    private Long eventId;
    private LocalDate date;
    private LocalTime time;
}
