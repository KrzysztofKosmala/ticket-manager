package pl.ticket.feign.cart;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("cart")
public interface CartClient
{
    @GetMapping("api/v1/carts/{id}")
    CartSummaryDto getCart(@PathVariable("id") Long id);
}
