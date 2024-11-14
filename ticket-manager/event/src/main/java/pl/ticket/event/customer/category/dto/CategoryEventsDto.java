package pl.ticket.event.customer.category.dto;

import org.springframework.data.domain.Page;
import pl.ticket.dto.EventDto;
import pl.ticket.event.common.model.Category;

public record CategoryEventsDto(Category category, Page<EventDto> events) {
}
