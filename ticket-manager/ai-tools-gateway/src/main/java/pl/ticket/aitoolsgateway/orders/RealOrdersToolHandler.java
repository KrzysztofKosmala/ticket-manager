package pl.ticket.aitoolsgateway.orders;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.feign.order.OrderClient;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.orders.handler", havingValue = "real")
public class RealOrdersToolHandler implements OrdersToolHandler {

    private final OrderClient orderClient;

    public RealOrdersToolHandler(OrderClient orderClient) {
        this.orderClient = orderClient;
    }

    @Override
    public OrderSearchResponse searchMyOrders(OrderSearchRequest request) {
        return orderClient.searchOrders(request);
    }

    @Override
    public OrderStatusResponse getMyOrderStatus(Long orderId) {
        OrderSearchResponse response = orderClient.searchOrders(new OrderSearchRequest(
                new OrderSearchRequest.Filters(orderId, null, null, null, null, null),
                null,
                1,
                0,
                false
        ));

        return response.items().stream()
                .findFirst()
                .map(order -> new OrderStatusResponse(order.id(), order.status(), true))
                .orElseGet(() -> new OrderStatusResponse(orderId, "UNKNOWN", false));
    }
}
