package pl.ticket.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Integer>
{

    @Query(value = "SELECT CASE WHEN capacity > 0 THEN true ELSE false END FROM Event WHERE id = :eventId", nativeQuery = true)
    boolean hasAvailableCapacity(@Param("eventId") Integer eventId);
}
