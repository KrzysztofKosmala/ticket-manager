package pl.ticket.event.admin.event_occurrence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventOccurrenceRegularCreationDto {

    private LocalTime time;
    private String day;

}
