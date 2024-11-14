package pl.ticket.event.internal.ticket.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;

public interface InternalConcreteTicketRepository extends JpaRepository<InternalConcreteTicket, Long>
{

}
