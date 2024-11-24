package pl.ticket.event.admin.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.ticket.event.admin.image.model.AdminImage;

public interface AdminImageRepository extends JpaRepository<AdminImage, Long>
{}
