package pl.ticket.aitoolsgateway.orders;

import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;

public interface OrdersToolHandler {

    OrderSearchResponse searchMyOrders(OrderSearchRequest request);

    OrderStatusResponse getMyOrderStatus(Long orderId);

    record OrderStatusResponse(
            Long orderId,
            String status,
            boolean found
    ) {
    }
}
