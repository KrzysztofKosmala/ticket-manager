package pl.ticket.cart.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ticket.cart.common.model.Cart;

@Repository
public interface InternalCartRepository extends JpaRepository<Cart, Long>
{
    @Query("delete from Cart c where c.id=:id")
    @Modifying
    void deleteCartById(@Param("id") Long id);
}
