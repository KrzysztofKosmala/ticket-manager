package pl.ticket.discount.admin.repossitory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ticket.discount.admin.model.AdminDiscount;

@Repository
public interface AdminDiscountRepository extends JpaRepository<AdminDiscount, Long>
{
    @Modifying
    @Query("UPDATE AdminDiscount d SET d.isActive = false WHERE d.id = :id")
    void deactivateDiscount(@Param("id") Long id);
}
