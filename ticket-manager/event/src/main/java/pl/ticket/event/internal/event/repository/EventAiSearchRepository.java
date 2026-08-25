package pl.ticket.event.internal.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.ticket.dto.EventSearchResponse;
import pl.ticket.event.internal.ticket.model.InternalTicket;

import java.time.LocalDate;
import java.util.List;

public interface EventAiSearchRepository extends JpaRepository<InternalTicket, Long> {

    @Query("""
            SELECT new pl.ticket.dto.EventSearchResponse$EventSummary(
                   e.id,
                   o.id,
                   e.title,
                   e.description,
                   e.categoryId,
                   o.date,
                   o.time,
                   MIN(t.price),
                   SUM(t.amount)
            )
            FROM InternalTicket t
            JOIN t.event e
            JOIN t.eventOccurrence o
            WHERE (:query IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:dateFrom IS NULL OR o.date >= :dateFrom)
              AND (:dateTo IS NULL OR o.date <= :dateTo)
              AND (:categoryId IS NULL OR e.categoryId = :categoryId)
            GROUP BY e.id, o.id, e.title, e.description, e.categoryId, o.date, o.time
            HAVING (:onlyAvailable = false OR SUM(t.amount) > 0)
            ORDER BY o.date ASC, o.time ASC, e.title ASC
            """)
    List<EventSearchResponse.EventSummary> search(
            @Param("query") String query,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            @Param("categoryId") Long categoryId,
            @Param("onlyAvailable") boolean onlyAvailable,
            Pageable pageable
    );
}
