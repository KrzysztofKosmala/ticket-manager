package pl.ticket.event.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.model.AdminEvent;

public interface AdminEventRepository extends JpaRepository<AdminEvent, Integer>
{
}
