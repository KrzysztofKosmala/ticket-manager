package pl.ticket.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.customer.model.dto.OrderDto;
import pl.ticket.customer.model.dto.OrderSummary;
import pl.ticket.customer.service.OrderService;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController
{

    private final OrderService orderService;

    @PostMapping
    public OrderSummary placeOrder(@RequestBody OrderDto orderDto, @AuthenticationPrincipal Jwt jwt)
    {
        String userId = jwt.getSubject();

        return orderService.placeOrder(orderDto, userId);
    }

}
