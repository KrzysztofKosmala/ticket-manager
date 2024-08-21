package pl.ticket.event.customer.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.event.customer.event.model.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>
{
    Optional<Event> findBySlug(String slug);
    Page<Event> findByCategoryId(Long id, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN FETCH e.occurrences")
    List<Event> findAllEventsPaged(Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.occurrences WHERE e.id = :id")
    Optional<Event> findByIdWithOccurrences(@Param("id") Long id);

    @Query("SELECT e FROM Event e JOIN FETCH e.occurrences o WHERE o.date = :date")
    List<Event> findByDatePaged(@Param("date") LocalDate date, Pageable pageable);

    @Query(value = "SELECT CASE WHEN capacity > 0 THEN true ELSE false END FROM Event WHERE id = :eventId", nativeQuery = true)
    boolean hasAvailableCapacity(@Param("eventId") Integer eventId);
}
