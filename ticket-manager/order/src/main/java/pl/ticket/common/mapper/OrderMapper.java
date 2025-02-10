package pl.ticket.common.mapper;

import org.apache.commons.lang3.RandomStringUtils;
import pl.ticket.common.model.Order;
import pl.ticket.common.model.OrderRow;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.common.model.dto.OrderCreationRequest;
import pl.ticket.common.model.dto.OrderSummary;
import pl.ticket.dto.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper
{
    public static Order createNewOrder(OrderCreationRequest orderCreationRequest, CartSummaryDto cart, String userId) {
        return Order.builder()
                .firstname(orderCreationRequest.getFirstname())
                .lastname(orderCreationRequest.getLastname())
                .email(orderCreationRequest.getEmail())
                .phone(orderCreationRequest.getPhone())
                .placeDate(LocalDateTime.now())
                .orderStatus(OrderStatus.CREATED)
                .grossValue(cart.getSummary().getGrossValue())
                .paymentId(orderCreationRequest.getPaymentId())
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

    public static OrderDto mapToDto(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPlaceDate(order.getPlaceDate());
        orderDto.setOrderStatus(order.getOrderStatus().name());
        orderDto.setOrderRows(order.getOrderRows().stream().map(OrderMapper::toOrderRowDto).collect(Collectors.toList()));
        orderDto.setGrossValue(order.getGrossValue());
        orderDto.setFirstname(order.getFirstname());
        orderDto.setLastname(order.getLastname());
        orderDto.setEmail(order.getEmail());
        orderDto.setPhone(order.getPhone());
        orderDto.setPaymentId(order.getPaymentId());
        orderDto.setUserId(order.getUserId());
        orderDto.setOrderHash(order.getOrderHash());
        return orderDto;
    }

/*    public static OrderRow toOrderRow(CartSummaryItemDto cartSummaryItemDto)
    {
        return OrderRow.builder()
                .productId(cartSummaryItemDto.getProduct().getId())
                .price(cartSummaryItemDto.getProduct().getPrice())
                .quantity(cartSummaryItemDto.getQuantity())
                .build();
    }*/

    public static OrderRowDto toOrderRowDto(OrderRow orderRow)
    {
        return OrderRowDto.builder()
                .id(orderRow.getId())
                .productName(orderRow.getProductName())
                .description(orderRow.getDescription())
                .productId(orderRow.getProductId())
                .price(orderRow.getPrice())
                .build();
    }
    public static List<OrderRow> toOrderRow(Long orderId, CartSummaryItemDto itemDto, TicketWithDetailsDto ticket) {


        List<OrderRow> orderRows = new ArrayList<>();
        for(int i=0; i<itemDto.getQuantity(); i++ )
        {
            orderRows.add(OrderRow.builder()
                    .orderId(orderId)
                    .productId(itemDto.getProduct().getId())
                    .price(itemDto.getProduct().getPrice())
                    .productName(ticket.getEvent().getTitle())
                    .description(String.format("Data: %s Godzina: %s.", ticket.getEventOccurrence().getDate(), ticket.getEventOccurrence().getTime()))
                    .build());
        }

        return orderRows;
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
