package pl.ticket.common.mapper;

import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.dto.CartSummaryDto;
import pl.ticket.dto.EmailMessage;

public class EmailMessageGenerator
{
    /*TODO: poprawić to generowanie ładnych maili*/
    public static EmailMessage orderCreatedMessage(OrderDto order, CartSummaryDto cart)
    {
        return EmailMessage.builder()
                .to(order.getEmail())
                .subject("Twoje zamówienie zostało przyjęte.")
                .body(cart.getItems().toString())
                .build();
    }


}
