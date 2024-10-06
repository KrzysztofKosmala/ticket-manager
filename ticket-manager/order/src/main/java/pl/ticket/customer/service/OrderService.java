package pl.ticket.customer.service;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.feign.cart.CartClient;
import pl.ticket.feign.cart.CartSummaryDto;
import pl.ticket.feign.event.EventClient;

@Service
public record OrderService(CartClient cartClient, EventClient eventClient)
{

    public OrderSummary placeOrder(OrderDto orderDto)
    {

        Long cartId = orderDto.getCartId();
        CartSummaryDto cart = cartClient.getCart(cartId);
        //wyciagnac koszyk

        int i =1;

        return null;
    }
}
