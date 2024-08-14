package pl.ticket.event.admin.event_occurrence.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class AdminEventOccurrenceRegularCreationDto {
    @JsonIgnore
    private LocalDate date;
    private LocalTime time;
    private Integer spaceLeft;
    private String requestedNameDayOfWeek;

}
