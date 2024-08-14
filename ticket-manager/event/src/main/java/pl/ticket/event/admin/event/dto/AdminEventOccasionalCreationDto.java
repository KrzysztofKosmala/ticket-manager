package pl.ticket.event.admin.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceOccasionalCreationDto;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventOccasionalCreationDto extends AdminEventCreationDto{
    @NotNull
    private List<AdminEventOccurrenceOccasionalCreationDto> eventOccurrences;
}
