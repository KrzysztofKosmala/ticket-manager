package pl.ticket.aitoolsgateway.orders;

import org.junit.jupiter.api.Test;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.dto.OrderSearchResponse.OrderSummary;
import pl.ticket.feign.order.OrderClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RealOrdersToolHandlerTest {

    @Test
    void shouldDelegateSearchToOrderClient() {
        OrderClient orderClient = mock(OrderClient.class);
        RealOrdersToolHandler handler = new RealOrdersToolHandler(orderClient);
        OrderSearchRequest request = new OrderSearchRequest(null, null, 10, 0, false);
        OrderSearchResponse response = new OrderSearchResponse(List.of(), 0, false);
        when(orderClient.searchOrders(request)).thenReturn(response);

        OrderSearchResponse result = handler.searchMyOrders(request);

        assertThat(result)
                .isSameAs(response);
        verify(orderClient).searchOrders(request);
    }

    @Test
    void shouldReadOrderStatusThroughOrderSearchEndpoint() {
        OrderClient orderClient = mock(OrderClient.class);
        RealOrdersToolHandler handler = new RealOrdersToolHandler(orderClient);
        OrderSearchResponse response = new OrderSearchResponse(
                List.of(new OrderSummary(1001L, LocalDateTime.of(2026, 6, 10, 12, 30), "PAID", BigDecimal.TEN, 501L, null)),
                1,
                false
        );
        when(orderClient.searchOrders(new OrderSearchRequest(
                new OrderSearchRequest.Filters(1001L, null, null, null, null, null),
                null,
                1,
                0,
                false
        ))).thenReturn(response);

        OrdersToolHandler.OrderStatusResponse result = handler.getMyOrderStatus(1001L);

        assertThat(result)
                .isEqualTo(new OrdersToolHandler.OrderStatusResponse(1001L, "PAID", true));
    }
}
