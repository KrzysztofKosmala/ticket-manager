package pl.ticket.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.customer.model.OrderRow;

@Repository
public interface OrderRowRepository extends JpaRepository<OrderRow, Long>
{
}
