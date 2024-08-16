package pl.ticket.event.admin.event_occurrence.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventOccurrenceOccasionalCreationDto {

    private LocalDate date;

    private LocalTime time;

}
