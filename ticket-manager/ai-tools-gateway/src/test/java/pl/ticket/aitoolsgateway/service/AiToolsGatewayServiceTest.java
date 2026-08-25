package pl.ticket.aitoolsgateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import pl.ticket.aitoolsgateway.event.EventToolHandler;
import pl.ticket.aitoolsgateway.orders.OrdersToolHandler;
import pl.ticket.dto.EventSearchRequest;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AiToolsGatewayServiceTest {

    @Test
    void shouldExposeGatewayToolCallbacks() {
        AiToolsGatewayService service = service();

        ToolCallbackProvider provider = toolCallbackProvider(service);

        assertThat(provider.getToolCallbacks())
                .extracting(callback -> callback.getToolDefinition().name())
                .containsExactlyInAnyOrder(
                        "tm_my_orders_search",
                        "tm_events_search"
                );
    }

    @Test
    void shouldSearchOrdersThroughConfiguredHandler() {
        OrdersToolHandler ordersToolHandler = mock(OrdersToolHandler.class);
        OrderSearchRequest request = new OrderSearchRequest(null, null, 10, 0, false);
        OrderSearchResponse response = new OrderSearchResponse(List.of(), 0, false);
        when(ordersToolHandler.searchMyOrders(request)).thenReturn(response);
        AiToolsGatewayService service = service(ordersToolHandler);

        OrderSearchResponse result = service.searchMyOrders(request);

        assertThat(result)
                .isSameAs(response);
        verify(ordersToolHandler).searchMyOrders(request);
    }

    @Test
    void shouldDelegateReadOnlyToolMethodsToConfiguredHandlers() {
        OrdersToolHandler ordersToolHandler = mock(OrdersToolHandler.class);
        EventToolHandler eventToolHandler = mock(EventToolHandler.class);
        EventSearchRequest searchRequest = new EventSearchRequest(
                new EventSearchRequest.Filters("spring", null, null, null, null),
                null,
                10,
                0
        );
        EventSearchResponse searchResponse = new EventSearchResponse(List.of());
        when(eventToolHandler.searchEvents(searchRequest)).thenReturn(searchResponse);
        AiToolsGatewayService service = new AiToolsGatewayService(
                ordersToolHandler,
                eventToolHandler
        );

        assertThat(service.searchEvents(searchRequest)).isSameAs(searchResponse);
        verify(eventToolHandler).searchEvents(searchRequest);
    }

    private ToolCallbackProvider toolCallbackProvider(AiToolsGatewayService service) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(service)
                .build();
    }

    private AiToolsGatewayService service() {
        return service(mock(OrdersToolHandler.class));
    }

    private AiToolsGatewayService service(OrdersToolHandler ordersToolHandler) {
        return new AiToolsGatewayService(
                ordersToolHandler,
                mock(EventToolHandler.class)
        );
    }
}
