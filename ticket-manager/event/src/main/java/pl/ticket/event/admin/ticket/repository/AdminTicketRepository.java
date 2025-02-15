package pl.ticket.event.admin.ticket.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.model.AdminTicketType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AdminTicketRepository extends JpaRepository<AdminTicket, Long>
{
    @Query("""
        SELECT t
        FROM AdminTicket t
        JOIN t.eventOccurrence eo
        WHERE t.event.id = :eventId
        AND (eo.date > :date OR (eo.date = :date AND eo.time > :time))
        AND t.type = :ticketType
        """)
    List<AdminTicket> findTicketsByEventIdTypeAndFutureDate(
            @Param("eventId") Long eventId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time,
            @Param("ticketType") AdminTicketType ticketType);
}
