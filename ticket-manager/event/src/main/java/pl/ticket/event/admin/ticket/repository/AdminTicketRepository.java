package pl.ticket.event.admin.ticket.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto;
import pl.ticket.event.admin.ticket.model.AdminTicket;
import pl.ticket.event.admin.ticket.model.AdminTicketType;

import java.math.BigDecimal;
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



    @Modifying
    @Query("""
    UPDATE AdminTicket t 
    SET t.oldPrice = t.price, t.price = :newPrice 
    WHERE t.event.id = :eventId 
    AND t.type = :ticketType
    AND t.price <> :newPrice
    AND t.eventOccurrence.id IN (
        SELECT eo.id 
        FROM AdminEventOccurrence eo 
        WHERE eo.eventId = :eventId 
        AND (eo.date > :date OR (eo.date = :date AND eo.time > :time))
    )
""")
    int updateTicketsByEventId(
            @Param("eventId") Long eventId,
            @Param("ticketType") AdminTicketType ticketType,
            @Param("newPrice") BigDecimal newPrice,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);

    @Query("""
        SELECT new pl.ticket.event.admin.ticket.dto.AdminTicketUpdateDto(t.type, t.price)
        FROM AdminTicket t
        WHERE t.event.id = :eventId
        ORDER BY t.type, t.price
    """)
    List<AdminTicketUpdateDto> findTicketsByEventId(@Param("eventId") Long eventId);
}


