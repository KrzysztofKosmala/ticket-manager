package pl.ticket.cart.internal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.cart.common.mapper.CartMapper;
import pl.ticket.cart.internal.service.InternalCartService;


@RestController
@RequestMapping("/api/v1/internal/carts")
@RequiredArgsConstructor
public class InternalCartController
{
    private final InternalCartService cartService;
    private final CartMapper cartMapper;

    @DeleteMapping("/{id}")
    public void deleteCart(@PathVariable Long id)
    {
        cartService.deleteCartById(id);
    }
}
