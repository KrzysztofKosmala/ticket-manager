package pl.ticket.event.internal.ticket.repository;




import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.event.internal.ticket.model.InternalTicket;

import java.util.List;

public interface InternalTicketRepository extends JpaRepository<InternalTicket, Long>
{


    @Query("SELECT t FROM InternalTicket t " +
            "JOIN FETCH t.eventOccurrence " +
            "JOIN FETCH t.event " +
            "WHERE t.id IN :ids")
    List<InternalTicket> findByIdInWithEventDetails(@Param("ids") List<Long> ids);
}
