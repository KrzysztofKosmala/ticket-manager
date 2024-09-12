package pl.ticket.customer.service;


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
        //eventClient.capacityCheck(1);
//to nie dziala!!!!
        //CartSummaryDto cart = cartClient.getCart(orderDto.getCartId());
        //wyciagnac koszyk

        int i =1;

        return null;
    }
}
