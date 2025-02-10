package pl.ticket.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pl.ticket.admin.service.AdminOrderService;
import pl.ticket.common.model.Order;
import pl.ticket.dto.OrderDto;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/admin/orders")
@Slf4j
public class AdminOrderController
{

    private final AdminOrderService orderService;


    @GetMapping
    public Page<OrderDto> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "placeDate") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        log.trace("Getting paged and sorted orders");
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        return orderService.getOrders(PageRequest.of(page, size, sort));
    }



    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

/*    @GetMapping("/{email}")
    public OrderDto getOrderByEmail(@PathVariable String email) {
        return orderService.getOrderByEmail(email);
    }

    @GetMapping
    public OrderDto getOrderByParam(@RequestParam(required = false) Long id,
                                    @RequestParam(required = false) String email)
    {
        return switch (String.valueOf(id != null ? "ID" : email != null ? "EMAIL" : "INVALID")) {
            case "ID" -> return orderService.getOrderById(orderId);

            case "EMAIL" -> OrderMapper.mapToDto(
                    orderService.findOrderByEmail(email)
                            .orElseThrow(() -> new OrderNotFoundException("Order with email " + email + " not found"))
            );
            default -> throw new IllegalArgumentException("Either id or email must be provided");
        };
    }*/

}