package pl.ticket.event.admin.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.common.dto.AdminTicketCreationDto;

import java.util.List;

@Data
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
    private Long categoryId;

    @NotNull
    private EventType eventType;

    private Boolean isCommonTicketPool;

    private List<AdminTicketCreationDto> tickets;
}
