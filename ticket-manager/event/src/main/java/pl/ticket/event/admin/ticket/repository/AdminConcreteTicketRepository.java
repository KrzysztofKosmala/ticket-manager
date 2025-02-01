package pl.ticket.event.admin.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.ticket.model.AdminConcreteTicket;

import java.util.List;


public interface AdminConcreteTicketRepository extends JpaRepository<AdminConcreteTicket, Long>
{
    List<AdminConcreteTicket> findByGeneralTicketId(Long generalTicketId);
}
