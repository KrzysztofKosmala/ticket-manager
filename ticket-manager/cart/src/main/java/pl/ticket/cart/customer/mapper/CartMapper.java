package pl.ticket.cart.customer.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.ticket.cart.customer.model.Cart;
import pl.ticket.cart.customer.model.CartItem;
import pl.ticket.cart.customer.model.dto.CartSummaryDto;
import pl.ticket.cart.customer.model.dto.CartSummaryItemDto;
import pl.ticket.cart.customer.model.dto.SummaryDto;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CartMapper
{
    public CartSummaryDto mapToCartSummary(Cart cart)
    {
        return CartSummaryDto.builder()
                .id(cart.getId())
                .items(mapToCartItems(cart.getItems()))
                .summary(mapToSummary(cart.getItems()))
                .build();
    }

    private SummaryDto mapToSummary(List<CartItem> items)
    {
        return SummaryDto.builder()
                .grossValue(sumValues(items))
                .build();
    }

    private BigDecimal sumValues(List<CartItem> items)
    {
        return items.stream().map(this::calculateLineValue).reduce(BigDecimal::add).orElseThrow();
    }

    private List<CartSummaryItemDto> mapToCartItems(List<CartItem> items)
    {
       return items.stream().map(this::mapToCartItem).toList();
    }

    private CartSummaryItemDto mapToCartItem(CartItem cartItem)
    {
        return CartSummaryItemDto.builder()
                .id(cartItem.getId())
                .quantity(cartItem.getQuantity())
                //todo: można tu zwracac caly ticket pozuskujac go z ticket controllera
                .ticketId(cartItem.getTicketId())
                .lineValue(calculateLineValue(cartItem))
                .build();
    }

    private BigDecimal calculateLineValue(CartItem cartItem)
    {
        return cartItem.getTicketPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
