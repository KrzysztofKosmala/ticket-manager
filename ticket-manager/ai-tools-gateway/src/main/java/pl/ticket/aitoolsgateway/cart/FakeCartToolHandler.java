package pl.ticket.aitoolsgateway.cart;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@ConditionalOnProperty(name = "ai-tools-gateway.tools.cart.handler", havingValue = "fake", matchIfMissing = true)
public class FakeCartToolHandler implements CartToolHandler {

    @Override
    public CartResponse getMyCart() {
        List<CartItem> items = List.of(
                new CartItem(2001L, "Concert ticket", 1, new BigDecimal("199.99")),
                new CartItem(2002L, "Workshop pass", 2, new BigDecimal("79.50"))
        );

        return new CartResponse(
                3001L,
                items,
                new BigDecimal("279.49"),
                items.stream().mapToLong(CartItem::quantity).sum(),
                true
        );
    }

    @Override
    public CartItemsCountResponse countMyCartItems() {
        CartResponse cart = getMyCart();

        return new CartItemsCountResponse(cart.cartId(), cart.itemCount(), cart.found());
    }
}
