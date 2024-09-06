package pl.ticket.cart.customer.service;

import pl.ticket.cart.customer.model.dto.CartTicketDto;
import pl.ticket.cart.customer.model.dto.TicketDto;

import java.math.BigDecimal;

public class CartTicketDtoDataProvider

{
    public CartTicketDto getCartTicketDto()
    {
       return CartTicketDto.builder()
                .ticket(TicketDto.builder()
                        .id(1L)
                        .price(new BigDecimal(20))
                        .build())
                .quantity(2)
                .build();
    }

    public CartTicketDto getCartTicketDtoWhenSameTicketIsAlreadyInCart()
    {
        return CartTicketDto.builder()
                .ticket(TicketDto.builder()
                        .id(2L)
                        .price(new BigDecimal(20))
                        .build())
                .quantity(2)
                .build();
    }
}
