package pl.ticket.cart.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.ticket.cart.common.mapper.CartMapper;
import pl.ticket.cart.common.dto.CartProductDto;
import pl.ticket.cart.customer.service.CartService;
import pl.ticket.dto.CartSummaryDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController
{
    private final CartService cartService;
    private final CartMapper cartMapper;

    @GetMapping("/{id}")
    public CartSummaryDto getCart(@PathVariable Long id)
    {
        return cartMapper.mapToCartSummary(cartService.getCart(id));
    }

    @PutMapping("/{id}")
    public CartSummaryDto addProductToCart(@PathVariable(value = "id", required = false) Long id, @RequestBody CartProductDto cartProductDto)
    {
        return cartMapper.mapToCartSummary(cartService.addProductToCart(id, cartProductDto));
    }

    @PutMapping("/{id}/update")
    public CartSummaryDto updateCart(@PathVariable Long id, @RequestBody List<CartProductDto> cartProductDtos)
    {
        return cartMapper.mapToCartSummary(cartService.updateCart(id, cartProductDtos));
    }

}
