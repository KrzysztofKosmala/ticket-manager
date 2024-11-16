package pl.ticket.common.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.common.model.dto.OrderSummary;
import pl.ticket.dto.*;

import java.time.LocalDateTime;

public class OrderMapper
{
    public static Order createNewOrder(OrderDto orderDto, CartSummaryDto cart, String userId) {
        return Order.builder()
                .firstname(orderDto.getFirstname())
                .lastname(orderDto.getLastname())
                .email(orderDto.getEmail())
                .phone(orderDto.getPhone())
                .placeDate(LocalDateTime.now())
                .orderStatus(OrderStatus.CREATED)
                .grossValue(cart.getSummary().getGrossValue())
                .paymentId(orderDto.getPaymentId())
                .userId(userId)
                .orderHash(RandomStringUtils.randomAlphanumeric(12))
                .build();
    }

    public static OrderEvent toOrderEvent(Order order)
    {
        return OrderEvent.builder()
                .orderId(order.getId())
                .clientEmail(order.getEmail())
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
                .productName(orderRow.getProductName())
                .description(orderRow.getDescription())
                .productId(orderRow.getProductId())
                .price(orderRow.getPrice())
                .build();
    }
    public static OrderRow toOrderRow(Long orderId, CartSummaryItemDto itemDto, TicketWithDetailsDto ticket) {

        return OrderRow.builder()
                .orderId(orderId)
                .productId(itemDto.getProduct().getId())
                .price(itemDto.getProduct().getPrice())
                .quantity(itemDto.getQuantity())
                .productName(ticket.getEvent().getTitle())
                .description(String.format("Data: %s Godzina: %s.", ticket.getEventOccurrence().getDate(), ticket.getEventOccurrence().getTime()))
                .build();
    }

    public static OrderSummary createOrderSummary(Order newOrder, String redirectUrl) {
        return OrderSummary.builder()
                .id(newOrder.getId())
                .placeDate(newOrder.getPlaceDate())
                .status(newOrder.getOrderStatus())
                .grossValue(newOrder.getGrossValue())
                .paymentId(newOrder.getPaymentId())
                .redirectUrl(redirectUrl)
                .build();
    }
}
