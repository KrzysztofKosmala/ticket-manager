package pl.ticket.event.customer.category.controller;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.event.common.model.Category;
import pl.ticket.event.customer.category.dto.CategoryEventsDto;
import pl.ticket.event.customer.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    public final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/{slug}/event")
    public CategoryEventsDto getCategoriesWithEvents(@PathVariable
                                              @Pattern(regexp = "[a-z0-9\\-]+")
                                              String slug, Pageable pageable) {

        return categoryService.getCategoriesWithEvents(slug, pageable);
    }
}
