package pl.ticket.event.admin.event.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventCreationDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Integer capacity;

    @NotNull
    private String slug;

    @NotNull
    private EventType eventType; // OCCASIONAL or REGULAR
}
