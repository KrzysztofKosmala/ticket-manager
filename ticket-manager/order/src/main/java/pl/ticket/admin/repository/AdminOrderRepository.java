package pl.ticket.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.common.model.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminOrderRepository extends JpaRepository<Order, Long>
{
    Optional<Order> findOrderById(Long id);

    Optional<Order> findOrderByEmail(String email);
}
