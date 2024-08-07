package pl.ticket.event.customer.category.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.ticket.event.common.dto.EventDto;
import pl.ticket.event.customer.category.dto.CategoryEventsDto;
import pl.ticket.event.common.model.Category;
import pl.ticket.event.customer.category.repository.CategoryRepository;
import pl.ticket.event.customer.event.model.Event;
import pl.ticket.event.customer.event.repository.EventRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public CategoryEventsDto getCategoriesWithEvents(String slug, Pageable pageable) {
        Category category = categoryRepository.findBySlug(slug);
        Page<Event> page = eventRepository.findByCategoryId(category.getId(), pageable);
        List<EventDto> eventDtos = page.getContent().stream()
                .map(event -> EventDto.builder()
                        .id(event.getId())
                        .title(event.getTitle())
                        .description(event.getDescription())
                        .slug(event.getSlug())
                        .build())
                .toList();
        return new CategoryEventsDto(category, new PageImpl<>(eventDtos, pageable, page.getTotalElements()));
    }
}
