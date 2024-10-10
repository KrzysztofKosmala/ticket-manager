package pl.ticket.customer.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import pl.ticket.customer.model.Order;
import pl.ticket.customer.model.OrderRow;
import pl.ticket.customer.model.OrderStatus;
import pl.ticket.customer.model.Payment;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.dto.OrderCreatedEvent;
import pl.ticket.dto.OrderRowDto;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.dto.CartSummaryItemDto;

import java.time.LocalDateTime;

public class OrderMapper
{
    public static Order createNewOrder(OrderDto orderDto, CartSummaryDto cart, Payment payment, String userId) {
        return Order.builder()
                .firstname(orderDto.getFirstname())
                .lastname(orderDto.getLastname())
                .email(orderDto.getEmail())
                .phone(orderDto.getPhone())
                .placeDate(LocalDateTime.now())
                .orderStatus(OrderStatus.CREATED)
                .grossValue(cart.getSummary().getGrossValue())
                .payment(payment)
                .userId(userId)
                .orderHash(RandomStringUtils.randomAlphanumeric(12))
                .build();
    }

    public static OrderCreatedEvent toOrderCreatedEvent(Order order)
    {
        return OrderCreatedEvent.builder()
                .orderId(order.getId())
                .orderRows(order.getOrderRows().stream().map(OrderMapper::toOrderRowDto).toList())
                .build();
    }

    public static OrderRow toOrderRow(CartSummaryItemDto cartSummaryItemDto)
    {
        return OrderRow.builder()
                .productId(cartSummaryItemDto.getProduct().getId())
                .price(cartSummaryItemDto.getProduct().getPrice())
                .quantity(cartSummaryItemDto.getQuantity())
                .build();
    }

    public static OrderRowDto toOrderRowDto(OrderRow orderRow)
    {
        return OrderRowDto.builder()
                .id(orderRow.getId())
                .quantity(orderRow.getQuantity())
                .orderId(orderRow.getOrderId())
                .productId(orderRow.getProductId())
                .price(orderRow.getPrice())
                .build();
    }
    public static OrderRow mapToOrderRowWithQuantity(Long orderId, CartSummaryItemDto cartItem) {
        return OrderRow.builder()
                .quantity(cartItem.getQuantity())
                .productId(cartItem.getProduct().getId())
                .price(cartItem.getProduct().getPrice())
                .orderId(orderId)
                .build();
    }
}
