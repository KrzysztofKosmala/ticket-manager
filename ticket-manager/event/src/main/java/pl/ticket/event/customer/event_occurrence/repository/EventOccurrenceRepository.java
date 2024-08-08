package pl.ticket.event.customer.event_occurrence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

public interface EventOccurrenceRepository extends JpaRepository<EventOccurrence, Long>
{
}
