package pl.ticket.event.internal.ticket.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.dto.ConcreteTicketDto;
import pl.ticket.event.internal.ticket.model.InternalConcreteTicket;

import java.util.List;

public interface InternalConcreteTicketRepository extends JpaRepository<InternalConcreteTicket, Long>
{
    @Query("""
    SELECT new pl.ticket.dto.ConcreteTicketDto(
        ct.id,
        gt.id,
        e.title,
        eo.date,
        eo.time,
        e.description,
        ct.qrCode
    )
    FROM InternalConcreteTicket ct
    JOIN ct.generalTicket gt
    JOIN gt.event e
    JOIN gt.eventOccurrence eo
    WHERE gt.id IN :generalTicketIds
    """)
    List<ConcreteTicketDto> findConcreteTicketDtosByGeneralTicketIds(@Param("generalTicketIds") List<Long> concreteTicketIds);
}
