package pl.ticket.event.customer.category.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.common.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>
{
    Category findBySlug(String slug);
}
