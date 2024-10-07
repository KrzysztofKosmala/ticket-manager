package pl.ticket.customer.service.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import pl.ticket.customer.model.Order;
import pl.ticket.customer.model.OrderStatus;
import pl.ticket.customer.model.Payment;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.feign.cart.CartSummaryDto;
import pl.ticket.feign.cart.CartSummaryItemDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderMapper
{
    public static Order createNewOrder(OrderDto orderDto, CartSummaryDto cart, Payment payment, Long userId) {
        return Order.builder()
                .firstname(orderDto.getFirstname())
                .lastname(orderDto.getLastname())
                .email(orderDto.getEmail())
                .phone(orderDto.getPhone())
                .placeDate(LocalDateTime.now())
                .orderStatus(OrderStatus.NEW)
                .grossValue(cart.getSummary().getGrossValue())
                .payment(payment)
                .userId(userId)
                .orderHash(RandomStringUtils.randomAlphanumeric(12))
                .build();
    }


}
