package pl.ticket.event.admin.event.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.event_occurrence.dto.AdminEventOccurrenceRegularCreationDto;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventRegularCreationDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Integer capacity;

    @NotNull
    private String slug;

    @NotNull
    private Long categoryId;

    @NotNull
    private EventType eventType;

    @NotNull
    private List<AdminEventOccurrenceRegularCreationDto> eventOccurrencesRegular;


}
