package pl.ticket.event.admin.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.category.model.AdminCategory;

public interface AdminCategoryRepository extends JpaRepository<AdminCategory, Long> {
}
