package pl.ticket.aitoolsgateway.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.ticket.aitoolsgateway.event.EventToolHandler;
import pl.ticket.aitoolsgateway.knowledge.KnowledgeToolHandler;
import pl.ticket.aitoolsgateway.orders.OrdersToolHandler;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.dto.KnowledgeSearchResult;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

import java.util.List;

@Service
public class AiToolsGatewayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiToolsGatewayService.class);

    private final OrdersToolHandler ordersToolHandler;
    private final EventToolHandler eventToolHandler;
    private final KnowledgeToolHandler knowledgeToolHandler;

    public AiToolsGatewayService(
            OrdersToolHandler ordersToolHandler,
            EventToolHandler eventToolHandler,
            KnowledgeToolHandler knowledgeToolHandler
    ) {
        this.ordersToolHandler = ordersToolHandler;
        this.eventToolHandler = eventToolHandler;
        this.knowledgeToolHandler = knowledgeToolHandler;
    }

    @Tool(
            name = "tm_my_orders_search",
            description = "Wyszukuje zamowienia aktualnego uzytkownika. Zawsze przekaz obiekt request; gdy nie ma filtrow, uzyj pustego obiektu albo pustych filtrow."
    )
    public OrderSearchResponse searchMyOrders(
            @ToolParam(description = "Wymagany obiekt request z polami filters, limit, offset i includeRows.", required = true)
            OrderSearchRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_my_orders_search, request={}", request);
        OrderSearchResponse response = ordersToolHandler.searchMyOrders(request);
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_my_orders_search, returnedItems={}, totalCount={}, hasMore={}",
                response.items().size(),
                response.totalCount(),
                response.hasMore()
        );
        return response;
    }

    @Tool(
            name = "tm_events_search",
            description = "Wyszukuje wydarzenia po nazwie, dacie, kategorii i dostepnosci miejsc. Zawsze przekaz obiekt request; gdy nie ma filtrow, uzyj pustego obiektu albo pustych filtrow."
    )
    public EventSearchResponse searchEvents(
            @ToolParam(description = "Wymagany obiekt request z polami filters, sort, limit i offset.", required = true)
            EventSearchRequest request) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_events_search, request={}", request);
        EventSearchResponse response = eventToolHandler.searchEvents(request);
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_events_search, returnedEvents={}",
                response.items().size()
        );
        return response;
    }

    @Tool(
            name = "tm_knowledge_search",
            description = "Przeszukuje dokumentacje i stala baze wiedzy Ticket Managera. Uzywaj do pytan o znaczenie statusow, zasady i przebieg procesow. Nie uzywaj do biezacych danych konkretnego uzytkownika."
    )
    public List<KnowledgeSearchResult> searchKnowledge(
            @ToolParam(description = "Pytanie lub fraza opisujaca szukana informacje.", required = true)
            String query) {
        LOGGER.info("AI tools gateway tool started: toolName=tm_knowledge_search, query={}", query);
        List<KnowledgeSearchResult> response = knowledgeToolHandler.searchKnowledge(query);
        LOGGER.info(
                "AI tools gateway tool completed: toolName=tm_knowledge_search, returnedChunks={}",
                response.size()
        );
        return response;
    }

}
