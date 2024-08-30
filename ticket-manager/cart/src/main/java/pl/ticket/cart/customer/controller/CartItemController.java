package pl.ticket.cart.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.ticket.cart.customer.service.CartItemService;

@RestController
@RequestMapping("/api/v1/cartItems")
@RequiredArgsConstructor
public class CartItemController
{

    private final CartItemService cartItemService;

    @DeleteMapping("/{id}")
    public void deleteCartItem(@PathVariable Long id) {
        cartItemService.delete(id);
    }

    @GetMapping("/count/{cartId}")
    public Long countItemInCart(@PathVariable Long cartId) {
        return cartItemService.countItemInCart(cartId);
    }

}
