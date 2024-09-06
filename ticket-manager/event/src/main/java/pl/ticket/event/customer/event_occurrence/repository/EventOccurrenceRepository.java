package pl.ticket.event.customer.event_occurrence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.event.customer.event_occurrence.model.EventOccurrence;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventOccurrenceRepository extends JpaRepository<EventOccurrence, Long> {

    @Query("SELECT e FROM EventOccurrence e WHERE e.eventId = :id AND e.time = :time AND e.date = :date")
    EventOccurrence findEventOccurrenceByEventIdAndTime(@Param("id") Long id, @Param("time") LocalTime time, @Param("date")LocalDate date);
}
