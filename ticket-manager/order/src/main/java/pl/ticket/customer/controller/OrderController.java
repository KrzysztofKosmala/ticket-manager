package pl.ticket.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.customer.service.OrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController
{

    private final OrderService orderService;

    @PostMapping
    public OrderSummary placeOrder(@RequestBody OrderDto orderDto)
    {
        return orderService.placeOrder(orderDto);
    }

}
