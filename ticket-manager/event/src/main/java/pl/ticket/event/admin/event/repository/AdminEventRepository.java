package pl.ticket.event.admin.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.event.model.AdminEvent;

public interface AdminEventRepository extends JpaRepository<AdminEvent, Integer>
{
}
