package pl.ticket.event.admin.event_occurrence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.ticket.event.admin.event_occurrence.model.AdminEventOccurrence;

@Repository
public interface AdminEventOccurrenceRepository extends JpaRepository<AdminEventOccurrence, Long>
{
}
