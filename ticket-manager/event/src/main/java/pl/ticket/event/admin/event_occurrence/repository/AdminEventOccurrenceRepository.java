package pl.ticket.event.admin.event_occurrence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;

public interface AdminEventOccurrenceRepository extends JpaRepository<AdminEventOccurrence, Long>
{
}
