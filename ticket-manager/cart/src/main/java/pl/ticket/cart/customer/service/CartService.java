package pl.ticket.cart.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.cart.customer.model.Cart;
import pl.ticket.cart.customer.model.CartItem;
import pl.ticket.cart.customer.model.dto.CartProductDto;
import pl.ticket.cart.customer.repository.CartRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService
{

    private final CartRepository cartRepository;

    public Cart getCart(Long id)
    {
        return cartRepository.findById(id).orElseThrow();
    }

    @Transactional
    public Cart addProductToCart(Long id, CartProductDto cartProductDto)
    {
        Cart cart = getInitializedCart(id);
/*        Optional<CartItem> sameItemAlreadyInCart = findSameItemAlreadyInCart(cart, cartProductDto.getTicket().getId());

        if(sameItemAlreadyInCart.isEmpty())
        {*/
            //todo: moze zamiast wysylac ticket dto z ceną powinnismy ja pozyskiwać z controllera ticketu??
            cart.addTicket(CartItem.builder()
                    .quantity(cartProductDto.getQuantity())
                    .productId(cartProductDto.getProductDto().getId())
                    .productPrice(cartProductDto.getProductDto().getPrice())
                    .cartId(cart.getId())
                    .build());

      /*  }else
        {
            CartItem cartItem = sameItemAlreadyInCart.get();

            int quantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity + cartProductDto.getQuantity());

        }*/
        return cart;
    }

    private Optional<CartItem> findSameItemAlreadyInCart(Cart cart, Long ticketId)
    {
        return cart.getItems().stream()
                .filter(cartItem -> cartItem.getProductId().equals(ticketId))
                .findFirst();
    }

    private Cart getInitializedCart(Long id)
    {
        if(id == null || id <= 0)
        {
            return saveNewCart();
        }

        return cartRepository.findById(id).orElseGet(this::saveNewCart);
    }

    private Cart saveNewCart()
    {
        return cartRepository.save(Cart.builder()
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build());
    }

    @Transactional
    public Cart updateCart(Long id, List<CartProductDto> cartProductDtos)
    {
        Cart cart = cartRepository.findById(id).orElseThrow();
        cart.getItems().forEach(cartItem -> {
            cartProductDtos.stream()
                    .filter(cartProductDto -> cartItem.getProductId().equals(cartProductDto.getProductDto().getId()))
                    .findFirst()
                    .ifPresent(cartProductDto -> cartItem.setQuantity(cartProductDto.getQuantity()));
        });
        return cart;
    }
}
