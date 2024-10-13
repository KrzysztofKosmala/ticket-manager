package pl.ticket.cart.internal.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.ticket.cart.internal.service.InternalCartItemService;

@RestController
@RequestMapping("/api/v1/internal/cartItems")
@RequiredArgsConstructor
public class InternalCartItemController
{
    private final InternalCartItemService internalCartItemService;

    @DeleteMapping("/byCartId/{cartId}")
    public void deleteItemsByCartId(@PathVariable Long cartId)
    {
        internalCartItemService.deleteItemsByCartId(cartId);
    }
}
