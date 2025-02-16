package pl.ticket.event.admin.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.ticket.event.admin.event.dto.AdminEventUpdateDto;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.customer.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminEventRepository extends JpaRepository<AdminEvent, Long>
{
    @Query("SELECT e FROM AdminEvent e JOIN FETCH e.image")
    List<AdminEvent> findAllPaged(Pageable pageable);







}
