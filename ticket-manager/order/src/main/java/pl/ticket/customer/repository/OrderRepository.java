package pl.ticket.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.customer.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>
{
}
