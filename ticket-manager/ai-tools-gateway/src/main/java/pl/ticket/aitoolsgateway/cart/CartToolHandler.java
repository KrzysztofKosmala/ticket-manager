package pl.ticket.aitoolsgateway.cart;

import java.math.BigDecimal;
import java.util.List;

public interface CartToolHandler {

    CartResponse getMyCart();

    CartItemsCountResponse countMyCartItems();

    record CartResponse(
            Long cartId,
            List<CartItem> items,
            BigDecimal grossValue,
            long itemCount,
            boolean found
    ) {
    }

    record CartItem(
            Long productId,
            String productName,
            int quantity,
            BigDecimal lineValue
    ) {
    }

    record CartItemsCountResponse(
            Long cartId,
            long itemCount,
            boolean found
    ) {
    }
}
