package pl.ticket.cart.customer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.ticket.cart.customer.model.Cart;
import pl.ticket.cart.customer.model.CartItem;
import pl.ticket.cart.customer.model.dto.CartTicketDto;
import pl.ticket.cart.customer.repository.CartRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    public Cart addTicketToCart(Long id, CartTicketDto cartTicketDto)
    {
        Cart cart = getInitializedCart(id);


        //todo: moze zamiast wysylac ticket dto z ceną powinnismy ja pozyskiwać z controllera ticketu??
        cart.addTicket(CartItem.builder()
                .quantity(cartTicketDto.getQuantity())
                .ticketId(cartTicketDto.getTicket().getId())
                .ticketPrice(cartTicketDto.getTicket().getPrice())
                .cartId(cart.getId())
                .build());

        return cart;
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
                .build());
    }

    @Transactional
    public Cart updateCart(Long id, List<CartTicketDto> cartTicketDtos)
    {
        Cart cart = cartRepository.findById(id).orElseThrow();
        cart.getItems().forEach(cartItem -> {
            cartTicketDtos.stream()
                    .filter(cartProductDto -> cartItem.getTicketId().equals(cartProductDto.getTicket().getId()))
                    .findFirst()
                    .ifPresent(cartProductDto -> cartItem.setQuantity(cartProductDto.getQuantity()));
        });
        return cart;
    }
}
