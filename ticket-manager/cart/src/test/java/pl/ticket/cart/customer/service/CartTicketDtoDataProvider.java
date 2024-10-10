package pl.ticket.cart.customer.service;

import pl.ticket.cart.customer.model.dto.CartProductDto;
import pl.ticket.cart.customer.model.dto.ProductDto;

import java.math.BigDecimal;

public class CartTicketDtoDataProvider

{
    public CartProductDto getCartProductDto()
    {
       return CartProductDto.builder()
                .productDto(ProductDto.builder()
                        .id(1L)
                        .price(new BigDecimal(20))
                        .build())
                .quantity(2)
                .build();
    }

    public CartProductDto getCartProductDtoWhenSameProductIsAlreadyInCart()
    {
        return CartProductDto.builder()
                .productDto(ProductDto.builder()
                        .id(2L)
                        .price(new BigDecimal(20))
                        .build())
                .quantity(2)
                .build();
    }
}
