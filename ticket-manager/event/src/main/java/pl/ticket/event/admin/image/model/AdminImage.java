package pl.ticket.event.admin.image.model;

import jakarta.persistence.*;
import lombok.*;
import pl.ticket.event.admin.event.model.AdminEvent;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
public class AdminImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "desctiption")
    private String desc;

    @Column(name = "thumbImage")
    private String thumbImage;

    @ManyToMany(mappedBy = "images", cascade = CascadeType.ALL)
    private List<AdminEvent> events;
}

