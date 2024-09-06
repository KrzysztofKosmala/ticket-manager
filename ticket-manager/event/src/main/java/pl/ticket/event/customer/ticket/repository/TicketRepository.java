package pl.ticket.event.customer.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.event.customer.ticket.model.Ticket;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    @Query("SELECT t FROM Ticket t JOIN FETCH t.eventOccurrence o WHERE o.eventId = :id")
    List<Ticket> findTicketsByEventId(@Param("id") Long id);

    @Query("SELECT t FROM Ticket t WHERE t.eventOccurrence.id = :id")
    List<Ticket> findTicketsOccurrenceId(@Param("id") Long id);

}
