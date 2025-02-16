package pl.ticket.event.admin.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
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
    private Long imageId;

    private List<AdminTicketUpdateDto> tickets;

    public AdminEventUpdateDto(String title, String description, Integer capacity, String slug, Long categoryId, Long imageId) {
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.slug = slug;
        this.categoryId = categoryId;
        this.imageId = imageId;
        this.tickets = new ArrayList<>(); // Uniknięcie NullPointerException
    }
}
