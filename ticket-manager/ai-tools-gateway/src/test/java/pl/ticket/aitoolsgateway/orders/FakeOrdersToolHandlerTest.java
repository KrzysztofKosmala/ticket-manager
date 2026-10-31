package pl.ticket.aitoolsgateway.orders;

import org.junit.jupiter.api.Test;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FakeOrdersToolHandlerTest {

    @Test
    void shouldReturnDeterministicOrdersWithoutRowsByDefault() {
        FakeOrdersToolHandler handler = new FakeOrdersToolHandler();

        OrderSearchResponse response = handler.searchMyOrders(null);

        assertThat(response.totalCount())
                .isEqualTo(2);
        assertThat(response.hasMore())
                .isFalse();
        assertThat(response.items())
                .extracting(OrderSearchResponse.OrderSummary::id)
                .containsExactly(1001L, 1002L);
        assertThat(response.items())
                .allMatch(order -> order.rows() == null);
    }

    @Test
    void shouldApplyBasicFiltersPaginationAndRowsFlag() {
        FakeOrdersToolHandler handler = new FakeOrdersToolHandler();
        OrderSearchRequest request = new OrderSearchRequest(
                new OrderSearchRequest.Filters(null, List.of("PAID"), null, null, null, null),
                null,
                1,
                0,
                true
        );

        OrderSearchResponse response = handler.searchMyOrders(request);

        assertThat(response.totalCount())
                .isEqualTo(1);
        assertThat(response.items())
                .singleElement()
                .satisfies(order -> {
                    assertThat(order.status()).isEqualTo("PAID");
                    assertThat(order.rows()).isNotEmpty();
                });
    }

    @Test
    void shouldReturnOrderStatusByOrderId() {
        FakeOrdersToolHandler handler = new FakeOrdersToolHandler();

        OrdersToolHandler.OrderStatusResponse response = handler.getMyOrderStatus(1001L);

        assertThat(response)
                .isEqualTo(new OrdersToolHandler.OrderStatusResponse(1001L, "PAID", true));
    }
}
