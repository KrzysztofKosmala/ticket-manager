package pl.ticket.internal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.dto.OrderSearchRequest;
import pl.ticket.dto.OrderSearchResponse;
import pl.ticket.internal.repository.InternalOrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private static final int DEFAULT_LIMIT = 20;
    private static final int MAX_LIMIT = 100;

    private final InternalOrderRepository internalOrderRepository;

    public OrderSearchResponse searchOrders(String userId, OrderSearchRequest request) {
        OrderSearchRequest.Filters filters = request != null ? request.filters() : null;
        OrderSearchRequest.Sort sort = request != null ? request.sort() : null;
        int limit = normalizeLimit(request != null ? request.limit() : null);
        int offset = normalizeOffset(request != null ? request.offset() : null);
        boolean includeRows = request != null && Boolean.TRUE.equals(request.includeRows());

        List<OrderStatus> statuses = parseStatuses(filters != null ? filters.statuses() : null);
        Specification<Order> spec = Specification.where(OrderSearchSpecification.forUser(userId))
                .and(OrderSearchSpecification.withOrderId(filters != null ? filters.orderId() : null))
                .and(OrderSearchSpecification.withStatuses(statuses))
                .and(OrderSearchSpecification.placedAfter(filters != null ? filters.dateFrom() : null))
                .and(OrderSearchSpecification.placedBefore(filters != null ? filters.dateTo() : null))
                .and(OrderSearchSpecification.withMinGrossValue(filters != null ? filters.minGrossValue() : null))
                .and(OrderSearchSpecification.withMaxGrossValue(filters != null ? filters.maxGrossValue() : null));

        long totalCount = internalOrderRepository.count(spec);
        if (limit == 0) {
            return new OrderSearchResponse(Collections.emptyList(), totalCount, false);
        }

        PageRequest pageRequest = PageRequest.of(offset / limit, limit, resolveSort(sort));
        Page<Order> page = internalOrderRepository.findAll(spec, pageRequest);

        List<OrderSearchResponse.OrderSummary> items = page.getContent().stream()
                .map(order -> toSummary(order, includeRows))
                .toList();

        return new OrderSearchResponse(items, totalCount, page.hasNext());
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_LIMIT;
        }
        if (limit <= 0) {
            return 0;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private int normalizeOffset(Integer offset) {
        return offset == null || offset < 0 ? 0 : offset;
    }

    private Sort resolveSort(OrderSearchRequest.Sort sort) {
        String field = sort != null ? sort.field() : null;
        String direction = sort != null ? sort.direction() : null;
        Sort.Direction sortDirection = direction == null
                ? Sort.Direction.DESC
                : Sort.Direction.fromString(direction);
        String sortField = resolveSortField(field);
        return Sort.by(sortDirection, sortField);
    }

    private String resolveSortField(String field) {
        if (field == null) {
            return "placeDate";
        }
        String normalized = field.toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "placedate", "place_date" -> "placeDate";
            case "grossvalue", "gross_value" -> "grossValue";
            case "status", "orderstatus" -> "orderStatus";
            default -> "placeDate";
        };
    }

    private OrderSearchResponse.OrderSummary toSummary(Order order, boolean includeRows) {
        List<OrderSearchResponse.OrderRowSummary> rows = includeRows
                ? mapRows(order.getOrderRows())
                : null;
        return new OrderSearchResponse.OrderSummary(
                order.getId(),
                order.getPlaceDate(),
                order.getOrderStatus() != null ? order.getOrderStatus().name() : null,
                order.getGrossValue(),
                order.getPaymentId(),
                rows
        );
    }

    private List<OrderSearchResponse.OrderRowSummary> mapRows(List<OrderRow> orderRows) {
        if (orderRows == null) {
            return Collections.emptyList();
        }
        return orderRows.stream()
                .map(row -> new OrderSearchResponse.OrderRowSummary(
                        row.getProductId(),
                        row.getProductName(),
                        row.getDescription(),
                        row.getPrice(),
                        row.getShipmentId()
                ))
                .toList();
    }

    private List<OrderStatus> parseStatuses(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return null;
        }
        return statuses.stream()
                .map(status -> {
                    try {
                        return OrderStatus.valueOf(status);
                    } catch (IllegalArgumentException ex) {
                        return null;
                    }
                })
                .filter(value -> value != null)
                .toList();
    }
}
