package pl.ticket.event.admin.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.event.admin.event.model.AdminEvent;

@Repository
public interface AdminEventRepository extends JpaRepository<AdminEvent, Long>
{
}
