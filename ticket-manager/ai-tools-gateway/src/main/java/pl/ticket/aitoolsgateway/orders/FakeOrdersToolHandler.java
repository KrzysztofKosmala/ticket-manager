package pl.ticket.aitoolsgateway.orders;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchRequest.Filters;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.dto.OrderSearchResponse.OrderRowSummary;
import pl.ticket.dto.OrderSearchResponse.OrderSummary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.orders.handler", havingValue = "fake", matchIfMissing = true)
public class FakeOrdersToolHandler implements OrdersToolHandler {

    @Override
    public OrderSearchResponse searchMyOrders(OrderSearchRequest request) {
        List<OrderSummary> filteredOrders = applyFilters(fakeOrders(includeRows(request)), filters(request));
        int offset = offset(request);
        int limit = limit(request, filteredOrders.size());
        List<OrderSummary> page = page(filteredOrders, offset, limit);

        return new OrderSearchResponse(page, filteredOrders.size(), offset + page.size() < filteredOrders.size());
    }

    @Override
    public OrderStatusResponse getMyOrderStatus(Long orderId) {
        return fakeOrders(false).stream()
                .filter(order -> order.id().equals(orderId))
                .findFirst()
                .map(order -> new OrderStatusResponse(order.id(), order.status(), true))
                .orElseGet(() -> new OrderStatusResponse(orderId, "UNKNOWN", false));
    }

    private List<OrderSummary> fakeOrders(boolean includeRows) {
        return List.of(
                new OrderSummary(
                        1001L,
                        LocalDateTime.of(2026, 6, 10, 12, 30),
                        "PAID",
                        new BigDecimal("199.99"),
                        501L,
                        rows(includeRows)
                ),
                new OrderSummary(
                        1002L,
                        LocalDateTime.of(2026, 6, 18, 9, 15),
                        "CREATED",
                        new BigDecimal("79.50"),
                        null,
                        rows(includeRows)
                )
        );
    }

    private List<OrderRowSummary> rows(boolean includeRows) {
        if (!includeRows) {
            return null;
        }

        return List.of(new OrderRowSummary(
                2001L,
                "Test ticket",
                "Fake order row returned by ai-tools-gateway",
                new BigDecimal("199.99"),
                7001L
        ));
    }

    private List<OrderSummary> applyFilters(List<OrderSummary> orders, Filters filters) {
        if (filters == null) {
            return orders;
        }

        return orders.stream()
                .filter(order -> filters.orderId() == null || filters.orderId().equals(order.id()))
                .filter(order -> filters.statuses() == null || filters.statuses().isEmpty() || filters.statuses().contains(order.status()))
                .filter(order -> filters.dateFrom() == null || !order.placeDate().isBefore(filters.dateFrom()))
                .filter(order -> filters.dateTo() == null || !order.placeDate().isAfter(filters.dateTo()))
                .filter(order -> filters.minGrossValue() == null || order.grossValue().compareTo(filters.minGrossValue()) >= 0)
                .filter(order -> filters.maxGrossValue() == null || order.grossValue().compareTo(filters.maxGrossValue()) <= 0)
                .toList();
    }

    private List<OrderSummary> page(List<OrderSummary> orders, int offset, int limit) {
        if (offset >= orders.size()) {
            return List.of();
        }

        return orders.subList(offset, Math.min(offset + limit, orders.size()));
    }

    private Filters filters(OrderSearchRequest request) {
        return request == null ? null : request.filters();
    }

    private boolean includeRows(OrderSearchRequest request) {
        return request != null && Boolean.TRUE.equals(request.includeRows());
    }

    private int offset(OrderSearchRequest request) {
        if (request == null || request.offset() == null || request.offset() < 0) {
            return 0;
        }

        return request.offset();
    }

    private int limit(OrderSearchRequest request, int totalCount) {
        if (request == null || request.limit() == null || request.limit() <= 0) {
            return totalCount;
        }

        return request.limit();
    }
}
