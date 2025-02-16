package pl.ticket.event.admin.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.ticket.event.admin.ticket.dto.AdminTicketInitUpdateDto;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminEventInitUpdateDto
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
    private String image;

    private List<AdminTicketInitUpdateDto> tickets;


}