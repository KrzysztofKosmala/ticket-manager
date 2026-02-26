package pl.ticket.internal.service;

import org.springframework.data.jpa.domain.Specification;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderSearchSpecification {

    private OrderSearchSpecification() {
    }

    public static Specification<Order> forUser(String userId) {
        return (root, query, cb) -> cb.equal(root.get("userId"), userId);
    }

    public static Specification<Order> withOrderId(Long orderId) {
        return (root, query, cb) -> orderId == null ? null : cb.equal(root.get("id"), orderId);
    }

    public static Specification<Order> withStatuses(List<OrderStatus> statuses) {
        return (root, query, cb) -> (statuses == null || statuses.isEmpty())
                ? null
                : root.get("orderStatus").in(statuses);
    }

    public static Specification<Order> placedAfter(LocalDateTime dateFrom) {
        return (root, query, cb) -> dateFrom == null ? null : cb.greaterThanOrEqualTo(root.get("placeDate"), dateFrom);
    }

    public static Specification<Order> placedBefore(LocalDateTime dateTo) {
        return (root, query, cb) -> dateTo == null ? null : cb.lessThanOrEqualTo(root.get("placeDate"), dateTo);
    }

    public static Specification<Order> withMinGrossValue(BigDecimal minGrossValue) {
        return (root, query, cb) -> minGrossValue == null ? null : cb.greaterThanOrEqualTo(root.get("grossValue"), minGrossValue);
    }

    public static Specification<Order> withMaxGrossValue(BigDecimal maxGrossValue) {
        return (root, query, cb) -> maxGrossValue == null ? null : cb.lessThanOrEqualTo(root.get("grossValue"), maxGrossValue);
    }
}
