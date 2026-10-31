package pl.ticket.aitoolsgateway.event;

import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;

public interface EventToolHandler {

    EventSearchResponse searchEvents(EventSearchRequest request);
}
