package pl.ticket.event.admin.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.ticket.model.AdminTicket;

public interface AdminTicketRepository extends JpaRepository<AdminTicket, Integer>
{
}
