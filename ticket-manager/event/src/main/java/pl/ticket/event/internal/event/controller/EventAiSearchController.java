package pl.ticket.event.internal.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.event.internal.event.service.EventAiSearchService;

@RestController
@RequestMapping("/internal/ai/events")
@RequiredArgsConstructor
public class EventAiSearchController {

    private final EventAiSearchService eventAiSearchService;

    @PostMapping("/search")
    public EventSearchResponse searchEvents(@RequestBody(required = false) EventSearchRequest request) {
        return eventAiSearchService.searchEvents(request);
    }
}
