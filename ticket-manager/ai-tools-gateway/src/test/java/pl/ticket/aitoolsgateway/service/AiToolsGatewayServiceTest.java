package pl.ticket.aitoolsgateway.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import pl.ticket.aitoolsgateway.cart.CartToolHandler;
import pl.ticket.aitoolsgateway.event.EventToolHandler;
import pl.ticket.aitoolsgateway.orders.OrdersToolHandler;
import pl.ticket.aitoolsgateway.payment.PaymentToolHandler;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
                        "tm_my_order_status_get",
                        "tm_my_cart_get",
                        "tm_my_cart_items_count",
                        "tm_my_order_payment_status_get",
                        "tm_events_search",
                        "tm_event_capacity_check",
                        "tm_event_details_get"
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
        CartToolHandler cartToolHandler = mock(CartToolHandler.class);
        PaymentToolHandler paymentToolHandler = mock(PaymentToolHandler.class);
        EventToolHandler eventToolHandler = mock(EventToolHandler.class);
        OrdersToolHandler.OrderStatusResponse orderStatus = new OrdersToolHandler.OrderStatusResponse(1001L, "PAID", true);
        CartToolHandler.CartResponse cart = new CartToolHandler.CartResponse(3001L, List.of(), BigDecimal.ZERO, 0, true);
        CartToolHandler.CartItemsCountResponse itemCount = new CartToolHandler.CartItemsCountResponse(3001L, 0, true);
        PaymentToolHandler.PaymentStatusResponse payment = new PaymentToolHandler.PaymentStatusResponse(1001L, "PAID", BigDecimal.ZERO, "FAKE", true);
        EventToolHandler.EventSearchRequest searchRequest = new EventToolHandler.EventSearchRequest("spring", null, null, null);
        EventToolHandler.EventSearchResponse searchResponse = new EventToolHandler.EventSearchResponse(List.of());
        EventToolHandler.EventCapacityRequest capacityRequest = new EventToolHandler.EventCapacityRequest(
                "Spring Festival",
                LocalDate.of(2026, 7, 12),
                "Warsaw"
        );
        EventToolHandler.EventCapacityResponse capacity = new EventToolHandler.EventCapacityResponse(
                9001L,
                "Spring Festival",
                LocalDateTime.of(2026, 7, 12, 19, 0),
                "Warsaw",
                true,
                42,
                "AVAILABLE",
                true
        );
        EventToolHandler.EventDetailsRequest detailsRequest = new EventToolHandler.EventDetailsRequest("Spring Festival", null, null);
        EventToolHandler.EventDetailsResponse details = new EventToolHandler.EventDetailsResponse(
                9001L,
                "Spring Festival",
                LocalDateTime.of(2026, 7, 12, 19, 0),
                "Warsaw",
                "Main Hall",
                "VIP",
                BigDecimal.TEN,
                42,
                true,
                true
        );
        when(ordersToolHandler.getMyOrderStatus(1001L)).thenReturn(orderStatus);
        when(cartToolHandler.getMyCart()).thenReturn(cart);
        when(cartToolHandler.countMyCartItems()).thenReturn(itemCount);
        when(paymentToolHandler.getMyOrderPaymentStatus(1001L)).thenReturn(payment);
        when(eventToolHandler.searchEvents(searchRequest)).thenReturn(searchResponse);
        when(eventToolHandler.checkEventCapacity(capacityRequest)).thenReturn(capacity);
        when(eventToolHandler.getEventDetails(detailsRequest)).thenReturn(details);
        AiToolsGatewayService service = new AiToolsGatewayService(
                ordersToolHandler,
                cartToolHandler,
                paymentToolHandler,
                eventToolHandler
        );

        assertThat(service.getMyOrderStatus(1001L)).isSameAs(orderStatus);
        assertThat(service.getMyCart()).isSameAs(cart);
        assertThat(service.countMyCartItems()).isSameAs(itemCount);
        assertThat(service.getMyOrderPaymentStatus(1001L)).isSameAs(payment);
        assertThat(service.searchEvents(searchRequest)).isSameAs(searchResponse);
        assertThat(service.checkEventCapacity(capacityRequest)).isSameAs(capacity);
        assertThat(service.getEventDetails(detailsRequest)).isSameAs(details);
        verify(ordersToolHandler).getMyOrderStatus(1001L);
        verify(cartToolHandler).getMyCart();
        verify(cartToolHandler).countMyCartItems();
        verify(paymentToolHandler).getMyOrderPaymentStatus(1001L);
        verify(eventToolHandler).searchEvents(searchRequest);
        verify(eventToolHandler).checkEventCapacity(capacityRequest);
        verify(eventToolHandler).getEventDetails(detailsRequest);
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
                mock(CartToolHandler.class),
                mock(PaymentToolHandler.class),
                mock(EventToolHandler.class)
        );
    }
}
