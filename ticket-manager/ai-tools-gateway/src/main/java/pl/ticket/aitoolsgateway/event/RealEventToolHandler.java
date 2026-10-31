package pl.ticket.aitoolsgateway.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.feign.event.EventClient;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.event.handler", havingValue = "real")
public class RealEventToolHandler implements EventToolHandler {

    private final EventClient eventClient;

    public RealEventToolHandler(EventClient eventClient) {
        this.eventClient = eventClient;
    }

    @Override
    public EventSearchResponse searchEvents(EventSearchRequest request) {
        return eventClient.searchEvents(request);
    }
}
