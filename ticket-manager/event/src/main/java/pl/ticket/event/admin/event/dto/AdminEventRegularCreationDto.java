package pl.ticket.event.admin.event.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventRegularCreationDto extends AdminEventCreationDto {
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
    @NotNull
    private List<AdminEventOccurrenceRegularCreationDto> eventOccurrencesRegular;
}
