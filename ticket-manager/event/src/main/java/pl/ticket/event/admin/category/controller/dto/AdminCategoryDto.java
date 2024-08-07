package pl.ticket.event.admin.category.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class AdminCategoryDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String slug;
}
