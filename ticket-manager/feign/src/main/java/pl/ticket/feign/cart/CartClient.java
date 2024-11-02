package pl.ticket.feign.cart;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.ticket.dto.CartSummaryDto;

@FeignClient("cart")
public interface CartClient
{
    @GetMapping("api/v1/internal/carts/{id}")
    CartSummaryDto getCart(@PathVariable("id") Long id);
    @DeleteMapping("api/v1/internal/carts/{id}")
    void deleteCart(@PathVariable("id") Long id);
    @DeleteMapping("api/v1/internal/cartItems/byCartId/{cartId}")
    void deleteItemsByCartId(@PathVariable("cartId") Long cartId);
}
