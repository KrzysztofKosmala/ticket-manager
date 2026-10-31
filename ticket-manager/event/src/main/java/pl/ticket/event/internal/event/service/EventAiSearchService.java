package pl.ticket.event.internal.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.event.internal.event.repository.EventAiSearchRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventAiSearchService {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    private final EventAiSearchRepository eventAiSearchRepository;

    public EventSearchResponse searchEvents(EventSearchRequest request) {
        EventSearchRequest.Filters filters = request != null ? request.filters() : null;

        Integer requestedLimit = request != null ? request.limit() : null;
        int limit = requestedLimit == null ? DEFAULT_LIMIT : Math.min(requestedLimit, MAX_LIMIT);

        if (limit <= 0) {
            return new EventSearchResponse(List.of());
        }

        Integer requestedOffset = request != null ? request.offset() : null;
        int offset = requestedOffset == null || requestedOffset < 0 ? 0 : requestedOffset;
        int page = offset / limit;

        String query = null;
        if (filters != null && filters.query() != null && !filters.query().isBlank()) {
            query = filters.query().trim();
        }

        List<EventSearchResponse.EventSummary> items = eventAiSearchRepository.search(
                query,
                filters != null ? filters.dateFrom() : null,
                filters != null ? filters.dateTo() : null,
                filters != null ? filters.categoryId() : null,
                filters != null && Boolean.TRUE.equals(filters.onlyAvailable()),
                PageRequest.of(page, limit)
        );

        return new EventSearchResponse(items);
    }
}
