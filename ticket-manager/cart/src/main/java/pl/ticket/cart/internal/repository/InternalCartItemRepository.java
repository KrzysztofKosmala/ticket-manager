package pl.ticket.cart.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.cart.common.model.CartItem;

public interface InternalCartItemRepository extends JpaRepository<CartItem, Long>
{
    @Query("delete from CartItem ci where ci.cartId=:cartId")
    @Modifying
    void deleteByCartId(@Param("cartId") Long cartId);
}
