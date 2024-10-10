package pl.ticket.cart.customer.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import pl.ticket.cart.customer.model.Cart;
import pl.ticket.cart.customer.model.dto.CartProductDto;
import pl.ticket.cart.customer.repository.CartItemRepository;
import pl.ticket.cart.customer.repository.CartRepository;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartServiceTest extends PrePost
{
    @Autowired
    CartService cartService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    public static final CartTicketDtoDataProvider CART_TICKET_DTO_DATA_PROVIDER = new CartTicketDtoDataProvider();
    private static Stream<Arguments> provideInputDataForAddingTicketToCart()
    {

        return Stream.of(
                Arguments.of(
                        0L,
                        CART_TICKET_DTO_DATA_PROVIDER.getCartProductDto(),
                        1,
                        1
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideInputDataForAddingTicketToCart")
    public void shouldAddProductToCart(Long cartId, CartProductDto cartProductDto, int expectedCartNumberIncrease, int expectedCartItemNumberIncrease)
    {
        long countedCartsBefore = cartRepository.count();
        long countedCartItemsBefore = cartItemRepository.count();

        Cart cart = cartService.addProductToCart(cartId, cartProductDto);

        long countedCartsAfter = cartRepository.count();
        long countedCartItemsAfter = cartItemRepository.count();

        assertEquals(expectedCartNumberIncrease, countedCartsAfter - countedCartsBefore, "Unexpected event count after creation");
        assertEquals(expectedCartItemNumberIncrease, countedCartItemsAfter - countedCartItemsBefore, "Unexpected occurrences count after creation");

    }
    
    private static Stream<Arguments> provideInputDataForShouldIncreaseAmountOfTicketInExistingCart()
    {

        return Stream.of(
                Arguments.of(
                        333L,
                        CART_TICKET_DTO_DATA_PROVIDER.getCartProductDtoWhenSameProductIsAlreadyInCart(),
                        0,
                        0,
                        2
                )
        );
    }
    @ParameterizedTest
    @MethodSource("provideInputDataForShouldIncreaseAmountOfTicketInExistingCart")
    public void shouldIncreaseAmountOfTicketInExistingCart
            (
                    Long cartId,
                    CartProductDto cartProductDto,
                    int expectedCartNumberIncrease,
                    int expectedCartItemNumberIncrease,
                    int expectedQuantityIncrease
            )
    {
        long countedCartsBefore = cartRepository.count();
        long countedCartItemsBefore = cartItemRepository.count();

        Optional<Cart> byId = cartRepository.findById(cartId);
        int quantityBefore = byId.get().getItems().stream().findFirst().get().getQuantity();

        Cart cart = cartService.addProductToCart(cartId, cartProductDto);

        Optional<Cart> byIdAfter = cartRepository.findById(cartId);
        int quantityAfter = byIdAfter.get().getItems().stream().findFirst().get().getQuantity();

        long countedCartsAfter = cartRepository.count();
        long countedCartItemsAfter = cartItemRepository.count();

        assertEquals(expectedCartNumberIncrease, countedCartsAfter - countedCartsBefore, "Unexpected event count after creation");
        assertEquals(expectedCartItemNumberIncrease, countedCartItemsAfter - countedCartItemsBefore, "Unexpected occurrences count after creation");
        assertEquals(expectedQuantityIncrease, quantityAfter - quantityBefore, "Unexpected occurrences count after creation");

    }
}