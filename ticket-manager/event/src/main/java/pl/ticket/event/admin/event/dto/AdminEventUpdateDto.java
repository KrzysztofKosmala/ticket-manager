package pl.ticket.event.admin.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventUpdateDto
{
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
    private Long imageId;

    private List<AdminTicketUpdateDto> tickets;
}
