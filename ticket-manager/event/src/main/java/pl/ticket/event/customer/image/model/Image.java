package pl.ticket.event.customer.image.model;

import jakarta.persistence.*;
import lombok.*;
import pl.ticket.event.admin.event.model.AdminEvent;
import pl.ticket.event.customer.event.model.Event;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "description")
    private String desc;

    @Column(name = "thumbImage")
    private String thumbImage;

    @OneToMany(mappedBy = "image", fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();
}
