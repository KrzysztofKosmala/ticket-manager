package pl.ticket.aitoolsgateway.cart;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FakeCartToolHandlerTest {

    @Test
    void shouldReturnDeterministicCartSummary() {
        FakeCartToolHandler handler = new FakeCartToolHandler();

        CartToolHandler.CartResponse response = handler.getMyCart();

        assertThat(response.cartId()).isEqualTo(3001L);
        assertThat(response.found()).isTrue();
        assertThat(response.itemCount()).isEqualTo(3);
        assertThat(response.grossValue()).isEqualByComparingTo(new BigDecimal("279.49"));
        assertThat(response.items())
                .extracting(CartToolHandler.CartItem::productName)
                .containsExactly("Concert ticket", "Workshop pass");
    }

    @Test
    void shouldCountCartItemsFromFakeCart() {
        FakeCartToolHandler handler = new FakeCartToolHandler();

        CartToolHandler.CartItemsCountResponse response = handler.countMyCartItems();

        assertThat(response)
                .isEqualTo(new CartToolHandler.CartItemsCountResponse(3001L, 3, true));
    }
}
