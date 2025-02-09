package pl.ticket.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import pl.ticket.common.model.OrderStatus;
import pl.ticket.common.model.dto.OrderDto;
import pl.ticket.common.model.dto.OrderSummary;
import pl.ticket.customer.service.OrderService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController
{

    private final OrderService orderService;

    @PostMapping
    public OrderSummary placeOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt)
    {

        //sprawdzić dostępność
        String userId = jwt.getSubject();

        return orderService.placeOrder(orderDto, userId);
    }

    @GetMapping("/{orderId}/status")
    public OrderStatus checkStatus(@PathVariable Long orderId)
    {
        return orderService.getStatus(orderId);
    }

}
