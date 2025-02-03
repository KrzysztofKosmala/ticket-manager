package pl.ticket.cart.internal.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import pl.ticket.cart.common.mapper.CartMapper;
import pl.ticket.cart.internal.service.InternalCartService;
import pl.ticket.dto.CartSummaryDto;


@RestController
@RequestMapping("/api/v1/internal/carts")
@RequiredArgsConstructor
@Slf4j
public class InternalCartController
{
    private final InternalCartService cartService;
    private final CartMapper cartMapper;

    @GetMapping("/{id}")
    public CartSummaryDto getCart(@PathVariable Long id)
    {
        log.trace("Get cart with id {}", id);
        return cartMapper.mapToCartSummary(cartService.getCart(id));
    }

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Long id)
    {
        cartService.deleteCartById(id);
    }
}
