package pl.ticket.cart.customer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime created;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "cartId")
    private List<CartItem> items;

    public void addTicket(CartItem cartItem)
    {
        if (items == null) {
            items = new ArrayList<>();
        }
        new ArrayList<>(items).stream()
                .filter(item -> cartItem.getProductId().equals(item.getProductId()))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + cartItem.getQuantity()),
                        () -> items.add(cartItem)
                );

    }

}
