package pl.ticket.event.internal.ticket.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.customer.ticket.model.InternalTicket;

public interface InternalTicketRepository extends JpaRepository<InternalTicket, Long>
{
}
